package com.hrothwell.anime.client

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.Result
import com.hrothwell.anime.commands.Anime
import com.hrothwell.anime.domain.MALOAuthResponse
import com.hrothwell.anime.domain.MALAnimeListResponse
import com.hrothwell.anime.exception.OAuthCallException
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.FileUtil
import kotlinx.serialization.decodeFromString

/**
 * Client for all calls that are NOT part of the authorization flow (for now)
 */
class MALClient {
  companion object{

    fun getRandomAnime(user: String?, list: String): String {
      AnimeUtil.printDebug("getRandomAnime - enter")
      val clientSecrets = FileUtil.getUserSecrets()

      val headers = "X-MAL-CLIENT-ID" to clientSecrets.client_id
      val listStatus = "status" to list
      val limit = "limit" to 1000
      val userPathParam = user ?: clientSecrets.user_name

      val request =
        Fuel.get(path = "https://api.myanimelist.net/v2/users/$userPathParam/animelist", parameters = listOf(listStatus, limit))
          .appendHeader(headers)

      AnimeUtil.printDebug("getRandomAnime - calling to get response and handle http errors")
      val response = request.response()
      AnimeUtil.handlePotentialHttpErrors(response.second)

      AnimeUtil.printDebug("getRandomAnime - decoding MAL response")
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
      AnimeUtil.printDebug("getSuggestedAnime - enter")
      val limitToUse = if(limit.toInt() > 100) "100" else limit
      val url = "https://api.myanimelist.net/v2/anime/suggestions?limit=$limitToUse"

      AnimeUtil.printDebug("getSuggestedAnime - calling callWithOauth")
      val request = Fuel.get(url)
      val jsonResult = callWithOauth(request).get()

      AnimeUtil.printDebug("getSuggestedAnime - decoding MAL response")
      val malAnimeListResponse = FileUtil.jsonReader.decodeFromString<MALAnimeListResponse>(jsonResult)
      return malAnimeListResponse.data.map{
        it.node.title
      }
    }

    private fun callWithOauth(originalRequest: Request): Result<String, FuelError> {
      try{
        AnimeUtil.printDebug("callWithOauth - enter")

        val userSecrets = FileUtil.getUserSecrets()
        val updatedRequest = originalRequest.appendHeader("Authorization" to "Bearer ${userSecrets.oauth_tokens?.access_token}")

        AnimeUtil.printDebug("callWithOauth - attempt call")
        val (request, response, result) = updatedRequest.responseString()
        return when (result){
          is Result.Failure -> {
            println("token invalid, refreshing and trying again")
            val newToken = refreshOAuthToken()
            request.header("Authorization" to "Bearer $newToken").responseString().third
          }
          else -> {
            AnimeUtil.printDebug("callWithOauth - token valid, return response")
            result
          }
        }
      } catch(t: Throwable){
        throw OAuthCallException("""
          Failed while making using oauth token
        """.trimIndent(), t)
      }
    }

    fun refreshOAuthToken(): String? {
      AnimeUtil.printDebug("refreshOAuthToken - enter")

      val userSecrets = FileUtil.getUserSecrets()
      val grantType = "grant_type" to "refresh_token"
      val refreshToken = "refresh_token" to userSecrets.oauth_tokens?.refresh_token
      val clientId = "client_id" to userSecrets.client_id
      val params = listOf(grantType, refreshToken, clientId)

      AnimeUtil.printDebug("refreshOAuthToken - posting for response")
      val refreshResponse = Fuel.post("https://myanimelist.net/v1/oauth2/token", params).response()
      AnimeUtil.handlePotentialHttpErrors(refreshResponse.second)

      AnimeUtil.printDebug("refreshOAuthToken - decode response and updating secrets")
      val newOAuthJson = String(refreshResponse.third.get())
      val malOAuthResponse = FileUtil.jsonReader.decodeFromString<MALOAuthResponse>(newOAuthJson)
      val updatedSecrets = userSecrets.copy(oauth_tokens = malOAuthResponse)
      FileUtil.updateUserSecrets(updatedSecrets)
      return updatedSecrets.oauth_tokens?.access_token
    }
  }
}