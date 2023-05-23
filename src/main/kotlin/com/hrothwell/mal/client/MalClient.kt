package com.hrothwell.mal.client

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.Result
import com.hrothwell.mal.domain.response.*
import com.hrothwell.mal.exception.OAuthCallException
import com.hrothwell.mal.util.FileUtil
import com.hrothwell.mal.util.MalUtil
import kotlinx.serialization.decodeFromString
import kotlin.math.absoluteValue

/**
 * Client for all calls that are NOT part of the initial authorization flow (for now)
 * TODO there is probably a way to make the random/list functions and the manga/anime nodes more reusable/shared, but
 *   right now doesn't feel worth the effort so just duplicating code. Could utilize some sort of Result object similar to Rust maybe, or just make a super type
 */
class MalClient {
  companion object {

    private val allAnimeFields =
      "fields" to "status,mean,rank,popularity,num_episodes,average_episode_duration,list_status"

    private val allMangaFields = "fields" to "status,mean,rank,popularity,num_volumes,num_chapters,list_status"

    fun getRandomAnime(user: String?, list: String, includeNotYetAired: Boolean): AnimeNode? {
      MalUtil.printDebug("getRandomAnime - enter")
      val clientSecrets = FileUtil.getUserSecrets()

      val listStatus = "status" to list
      val limit = "limit" to 1000
      val userPathParam = user ?: clientSecrets.userName

      val request =
        Fuel.get(
          path = "https://api.myanimelist.net/v2/users/$userPathParam/animelist",
          parameters = listOf(listStatus, limit, allAnimeFields)
        )

      val json = callWithClientId(request).get()

      MalUtil.printDebug("getRandomAnime - decoding MAL response")
      val result = FileUtil.jsonReader.decodeFromString<MalGenericListResponse<AnimeData>>(json)
      MalUtil.printDebug("MALAnimeListResponse: ${result.data.first()}")

      return result.data.filter { !it.node.status.equals(AnimeAiringStatus.NOT_YET_AIRED.name, true) || includeNotYetAired }
        .randomOrNull()?.node
    }

    fun getRandomManga(user: String?, list: String, includeNotYetPublished: Boolean): MangaNode? {
      MalUtil.printDebug("getRandomManga - enter")
      val clientSecrets = FileUtil.getUserSecrets()

      val listStatus = "status" to list
      val limit = "limit" to 1000
      val userPathParam = user ?: clientSecrets.userName

      val request =
        Fuel.get(
          path = "https://api.myanimelist.net/v2/users/$userPathParam/mangalist",
          parameters = listOf(listStatus, limit, allMangaFields)
        )

      val json = callWithClientId(request).get()

      MalUtil.printDebug("getRandomAnime - decoding MAL response")
      val result = FileUtil.jsonReader.decodeFromString<MalGenericListResponse<MangaData>>(json)
      MalUtil.printDebug("MALAnimeListResponse: ${result.data.first()}")

      return result.data.filter { !it.node.status.equals(MangaPublishingStatus.NOT_YET_PUBLISHED.name, true) || includeNotYetPublished }
        .randomOrNull()?.node
    }

    /**
     * essentially a "search" function
     */
    fun getAnimeList(query: String, limit: Int): List<AnimeNode> {
      MalUtil.printDebug("getAnimeList - enter")
      val q = "q" to query
      val limitParam = "limit" to limit.absoluteValue
      val params = listOf(q, limitParam, allAnimeFields)
      val url = "https://api.myanimelist.net/v2/anime"

      val request = Fuel.get(url, parameters = params)
      val json = callWithClientId(request).get()
      val result = FileUtil.jsonReader.decodeFromString<MalGenericListResponse<AnimeData>>(json)

      MalUtil.printDebug("getAnimeList - exit")
      return result.data.map { it.node }
    }

    /**
     * essentially a "search" function
     */
    fun getMangaList(query: String, limit: Int): List<MangaNode> {
      MalUtil.printDebug("getMangaList - enter")
      val q = "q" to query
      val limitParam = "limit" to limit.absoluteValue
      val params = listOf(q, limitParam, allMangaFields)
      val url = "https://api.myanimelist.net/v2/manga"

      val request = Fuel.get(url, parameters = params)
      val json = callWithClientId(request).get()
      val result = FileUtil.jsonReader.decodeFromString<MalGenericListResponse<MangaData>>(json)

      MalUtil.printDebug("getMangaList - exit")
      return result.data.map { it.node }
    }

    /**
     * MAL api seems to return the same item when you set this to one, so could just always pull 100 and randomize it
     */
    fun getSuggestedAnime(limit: Int): List<AnimeNode> {
      MalUtil.printDebug("getSuggestedAnime - enter")
      val limitToUse = if (limit.absoluteValue > 100) 100 else limit.absoluteValue
      val url = "https://api.myanimelist.net/v2/anime/suggestions?limit=$limitToUse"

      MalUtil.printDebug("getSuggestedAnime - calling callWithOauth")
      val request = Fuel.get(url)
      val jsonResult = callWithOauth(request).get()

      MalUtil.printDebug("getSuggestedAnime - decoding MAL response")
      val malAnimeListResponse = FileUtil.jsonReader.decodeFromString<MalGenericListResponse<AnimeData>>(jsonResult)
      return malAnimeListResponse.data.map {
        it.node
      }
    }

    private fun callWithClientId(request: Request): Result<String, FuelError> {
      MalUtil.printDebug("callWithClientId - enter")
      val clientSecrets = FileUtil.getUserSecrets()
      val header = "X-MAL-CLIENT-ID" to clientSecrets.clientId
      MalUtil.printDebug("callWithClientId - get response")
      val response = request.appendHeader(header).responseString()
      MalUtil.handlePotentialHttpErrors(response.second)
      return response.third
    }

    private fun callWithOauth(originalRequest: Request): Result<String, FuelError> {
      try {
        MalUtil.printDebug("callWithOauth - enter")

        val userSecrets = FileUtil.getUserSecrets()
        val updatedRequest =
          originalRequest.appendHeader("Authorization" to "Bearer ${userSecrets.oauthTokens?.accessToken}")

        MalUtil.printDebug("callWithOauth - attempt call")
        val (request, response, result) = updatedRequest.responseString()
        return when (result) {
          is Result.Failure -> {
            println("token invalid, refreshing and trying again")
            val newToken = refreshOAuthToken()
            request.header("Authorization" to "Bearer $newToken").responseString().third
          }
          else -> {
            MalUtil.printDebug("callWithOauth - token valid, return response")
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
      MalUtil.printDebug("refreshOAuthToken - enter")

      val userSecrets = FileUtil.getUserSecrets()
      val grantType = "grant_type" to "refresh_token"
      val refreshToken = "refresh_token" to userSecrets.oauthTokens?.refreshToken
      val clientId = "client_id" to userSecrets.clientId
      val params = listOf(grantType, refreshToken, clientId)

      MalUtil.printDebug("refreshOAuthToken - posting for response")
      val refreshResponse = Fuel.post("https://myanimelist.net/v1/oauth2/token", params).response()
      MalUtil.handlePotentialHttpErrors(refreshResponse.second)

      MalUtil.printDebug("refreshOAuthToken - decode response and updating secrets")
      val newOAuthJson = String(refreshResponse.third.get())
      val malOAuthResponse = FileUtil.jsonReader.decodeFromString<MalOAuthResponse>(newOAuthJson)
      val updatedSecrets = userSecrets.copy(oauthTokens = malOAuthResponse)
      FileUtil.updateUserSecrets(updatedSecrets)
      return updatedSecrets.oauthTokens?.accessToken
    }
  }
}