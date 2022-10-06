package com.hrothwell.anime.client

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.Result
import com.hrothwell.anime.domain.MALOAuthResponse
import com.hrothwell.anime.domain.MALAnimeListResponse
import com.hrothwell.anime.exception.OAuthCallException
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.FileUtil
import kotlinx.serialization.decodeFromString

/**
 * Client for all calls that are NOT part of the login flow (for now)
 */
class MALClient {
  companion object{
    // todo consistent process to check and refresh token, retry calls that fail for 401 once. If fail again, prompt to login again

    fun getRandomAnime(user: String?, list: String): String {
      val clientSecrets = FileUtil.getUserSecrets()

      val headers = "X-MAL-CLIENT-ID" to clientSecrets.client_id
      val listStatus = "status" to list
      val limit = "limit" to 1000
      val userPathParam = user ?: clientSecrets.user_name

      val request =
        Fuel.get(path = "https://api.myanimelist.net/v2/users/$userPathParam/animelist", parameters = listOf(listStatus, limit))
          .appendHeader(headers)

      val response = request.response()
      AnimeUtil.handlePotentialHttpErrors(response.second)

      val json = String(response.third.get())
      val result = FileUtil.jsonReader.decodeFromString<MALAnimeListResponse>(json)
      val animeTitle = result.data.randomOrNull()?.node?.title
      return animeTitle?.let{
        "Random selection: $it"
      } ?: "$userPathParam's $list was empty"
    }

    /**
     * MAL api seems to return the same item when you set this to one, so could just always pull 100 and randomize it
     */
    fun getSuggestedAnime(limit: String): List<String> {
      val limitToUse = if(limit.toInt() > 100) "100" else limit
      val url = "https://api.myanimelist.net/v2/anime/suggestions?limit=$limitToUse"
      val request = Fuel.get(url)
      val jsonResult = callWithOauth(request).get()
      val malAnimeListResponse = FileUtil.jsonReader.decodeFromString<MALAnimeListResponse>(jsonResult)
      return malAnimeListResponse.data.map{
        it.node.title
      }
    }

    private fun callWithOauth(originalRequest: Request): Result<String, FuelError> {
      try{
        val userSecrets = FileUtil.getUserSecrets()
        val updatedRequest = originalRequest.appendHeader("Authorization" to "Bearer ${userSecrets.oauth_tokens?.access_token}")
        val (request, response, result) = updatedRequest.responseString()
        return when (result){
          is Result.Failure -> {
            println("token invalid, refreshing and trying again")
            val newToken = refreshOAuthToken()
            request.header("Authorization" to "Bearer $newToken").responseString().third
          }
          else -> result
        }
      } catch(t: Throwable){
        throw OAuthCallException("""
          Failed while making using oauth token
        """.trimIndent(), t)
      }
    }

    fun refreshOAuthToken(): String? {
      val userSecrets = FileUtil.getUserSecrets()
      val grantType = "grant_type" to "refresh_token"
      val refreshToken = "refresh_token" to userSecrets.oauth_tokens?.refresh_token
      val clientId = "client_id" to userSecrets.client_id
      val params = listOf(grantType, refreshToken, clientId)
      val refreshResponse = Fuel.post("https://myanimelist.net/v1/oauth2/token", params).response()
      AnimeUtil.handlePotentialHttpErrors(refreshResponse.second)
      val newOAuthJson = String(refreshResponse.third.get())
      val malOAuthResponse = FileUtil.jsonReader.decodeFromString<MALOAuthResponse>(newOAuthJson)
      val updatedSecrets = userSecrets.copy(oauth_tokens = malOAuthResponse)
      FileUtil.updateUserSecrets(updatedSecrets)
      return updatedSecrets.oauth_tokens?.access_token
    }
  }
}