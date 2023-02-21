package com.hrothwell.anime.client

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.Result
import com.hrothwell.anime.domain.mal.AiringStatus
import com.hrothwell.anime.domain.mal.AnimeNode
import com.hrothwell.anime.domain.mal.MALAnimeListResponse
import com.hrothwell.anime.domain.mal.MALOAuthResponse
import com.hrothwell.anime.exception.OAuthCallException
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.FileUtil
import kotlinx.serialization.decodeFromString
import kotlin.math.absoluteValue

/**
 * Client for all calls that are NOT part of the initial authorization flow (for now)
 */
class MALClient {
  companion object {

    private val allFields = "fields" to "status,mean,rank,popularity,num_episodes,average_episode_duration,list_status"

    fun getRandomAnime(user: String?, list: String, includeNotYetAired: Boolean): AnimeNode? {
      AnimeUtil.printDebug("getRandomAnime - enter")
      val clientSecrets = FileUtil.getUserSecrets()

      val listStatus = "status" to list
      val limit = "limit" to 1000
      val userPathParam = user ?: clientSecrets.userName

      val request =
        Fuel.get(
          path = "https://api.myanimelist.net/v2/users/$userPathParam/animelist",
          parameters = listOf(listStatus, limit, allFields)
        )

      val json = callWithClientId(request).get()

      AnimeUtil.printDebug("getRandomAnime - decoding MAL response")
      val result = FileUtil.jsonReader.decodeFromString<MALAnimeListResponse>(json)
      println("MALAnimeListResponse: ${result.data.first()}")

      return result.data.filter { it.node.status != AiringStatus.not_yet_aired || includeNotYetAired }
        .randomOrNull()?.node
    }

    /**
     * essentially a "search" function
     */
    fun getAnimeList(query: String, limit: Int): List<AnimeNode> {
      AnimeUtil.printDebug("getAnimeList - enter")
      val q = "q" to query
      val limitParam = "limit" to limit.absoluteValue
      val params = listOf(q, limitParam, allFields)
      val url = "https://api.myanimelist.net/v2/anime"

      val request = Fuel.get(url, parameters = params)
      val json = callWithClientId(request).get()
      val result = FileUtil.jsonReader.decodeFromString<MALAnimeListResponse>(json)

      AnimeUtil.printDebug("getAnimeList - exit")
      return result.data.map { it.node }
    }

    /**
     * MAL api seems to return the same item when you set this to one, so could just always pull 100 and randomize it
     */
    fun getSuggestedAnime(limit: Int): List<AnimeNode> {
      AnimeUtil.printDebug("getSuggestedAnime - enter")
      val limitToUse = if (limit.absoluteValue > 100) 100 else limit.absoluteValue
      val url = "https://api.myanimelist.net/v2/anime/suggestions?limit=$limitToUse"

      AnimeUtil.printDebug("getSuggestedAnime - calling callWithOauth")
      val request = Fuel.get(url)
      val jsonResult = callWithOauth(request).get()

      AnimeUtil.printDebug("getSuggestedAnime - decoding MAL response")
      val malAnimeListResponse = FileUtil.jsonReader.decodeFromString<MALAnimeListResponse>(jsonResult)
      return malAnimeListResponse.data.map {
        it.node
      }
    }

    private fun callWithClientId(request: Request): Result<String, FuelError> {
      AnimeUtil.printDebug("callWithClientId - enter")
      val clientSecrets = FileUtil.getUserSecrets()
      val header = "X-MAL-CLIENT-ID" to clientSecrets.clientId
      AnimeUtil.printDebug("callWithClientId - get response")
      val response = request.appendHeader(header).responseString()
      AnimeUtil.handlePotentialHttpErrors(response.second)
      return response.third
    }

    private fun callWithOauth(originalRequest: Request): Result<String, FuelError> {
      try {
        AnimeUtil.printDebug("callWithOauth - enter")

        val userSecrets = FileUtil.getUserSecrets()
        val updatedRequest =
          originalRequest.appendHeader("Authorization" to "Bearer ${userSecrets.oauthTokens?.accessToken}")

        AnimeUtil.printDebug("callWithOauth - attempt call")
        val (request, response, result) = updatedRequest.responseString()
        return when (result) {
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
      } catch (t: Throwable) {
        throw OAuthCallException(
          """
          Failed while making using oauth token
        """.trimIndent(), t
        )
      }
    }

    fun refreshOAuthToken(): String? {
      AnimeUtil.printDebug("refreshOAuthToken - enter")

      val userSecrets = FileUtil.getUserSecrets()
      val grantType = "grant_type" to "refresh_token"
      val refreshToken = "refresh_token" to userSecrets.oauthTokens?.refreshToken
      val clientId = "client_id" to userSecrets.clientId
      val params = listOf(grantType, refreshToken, clientId)

      AnimeUtil.printDebug("refreshOAuthToken - posting for response")
      val refreshResponse = Fuel.post("https://myanimelist.net/v1/oauth2/token", params).response()
      AnimeUtil.handlePotentialHttpErrors(refreshResponse.second)

      AnimeUtil.printDebug("refreshOAuthToken - decode response and updating secrets")
      val newOAuthJson = String(refreshResponse.third.get())
      val malOAuthResponse = FileUtil.jsonReader.decodeFromString<MALOAuthResponse>(newOAuthJson)
      val updatedSecrets = userSecrets.copy(oauthTokens = malOAuthResponse)
      FileUtil.updateUserSecrets(updatedSecrets)
      return updatedSecrets.oauthTokens?.accessToken
    }
  }
}