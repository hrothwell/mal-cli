package com.hrothwell.mal.client

import com.hrothwell.mal.domain.response.AnimeAiringStatus
import com.hrothwell.mal.domain.response.AnimeData
import com.hrothwell.mal.domain.response.AnimeNode
import com.hrothwell.mal.domain.response.MalGenericListResponse
import com.hrothwell.mal.domain.response.MalOAuthResponse
import com.hrothwell.mal.domain.response.MangaData
import com.hrothwell.mal.domain.response.MangaNode
import com.hrothwell.mal.domain.response.MangaPublishingStatus
import com.hrothwell.mal.util.FileUtil
import com.hrothwell.mal.util.MalUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlin.math.absoluteValue

/**
 * Client for all calls that are NOT part of the initial authorization flow (for now)
 * TODO there is probably a way to make the random/list functions and the manga/anime nodes more reusable/shared, but
 *   right now doesn't feel worth the effort so just duplicating code. Could utilize some sort of Result object similar to Rust maybe, or just make a super type
 */
class MalClient {
  companion object {
    val ktor = HttpClient(CIO) {
      defaultRequest {
        url("https://api.myanimelist.net/v2/")
        header("X-MAL-CLIENT-ID", FileUtil.getUserSecrets().clientId)
      }

      install(ContentNegotiation) {
        json(FileUtil.jsonReader)
      }

      install(Logging) {
        logger = ClientLogger()
        level = LogLevel.BODY
      }
    }

    private val allAnimeFields = "status,mean,rank,popularity,num_episodes,average_episode_duration,list_status"

    private val allMangaFields = "status,mean,rank,popularity,num_volumes,num_chapters,list_status"

    fun getRandomAnime(user: String?, list: String, includeNotYetAired: Boolean): AnimeNode? {
      MalUtil.printDebug("getRandomAnime - enter")
      val clientSecrets = FileUtil.getUserSecrets()

      val userPathParam = user ?: clientSecrets.userName

      val response = runBlocking {
        ktor.get("users/$userPathParam/animelist") {
          parameter("status", list)
          parameter("limit", 1000)
          parameter("fields", allAnimeFields)
        }
      }

      val result = runBlocking {
        response.getBody<MalGenericListResponse<AnimeData>>()
      } ?: MalGenericListResponse(emptyList())

      return result.data.filter {
        !it.node.status.equals(
          AnimeAiringStatus.NOT_YET_AIRED.name,
          true
        ) || includeNotYetAired
      }.randomOrNull()?.node
    }

    fun getRandomManga(user: String?, list: String, includeNotYetPublished: Boolean): MangaNode? {
      MalUtil.printDebug("getRandomManga - enter")
      val clientSecrets = FileUtil.getUserSecrets()
      val userPathParam = user ?: clientSecrets.userName

      val response = runBlocking {
        ktor.get("users/$userPathParam/mangalist") {
          parameter("status", list)
          parameter("limit", 1000)
          parameter("fields", allMangaFields)
        }
      }

      val result = runBlocking {
        response.getBody<MalGenericListResponse<MangaData>>()
      } ?: MalGenericListResponse(emptyList())

      return result.data.filter {
        !it.node.status.equals(
          MangaPublishingStatus.NOT_YET_PUBLISHED.name,
          true
        ) || includeNotYetPublished
      }.randomOrNull()?.node
    }

    fun searchAnime(query: String, limit: Int): List<AnimeNode> {
      MalUtil.printDebug("getAnimeList - enter")
      val response = runBlocking {
        ktor.get("anime") {
          parameter("q", query)
          parameter("limit", limit.absoluteValue)
          parameter("fields", "status,mean,rank,popularity,num_episodes,average_episode_duration,list_status")
        }
      }

      val result = runBlocking {
        response.getBody<MalGenericListResponse<AnimeData>>()
      } ?: MalGenericListResponse(emptyList())

      return result.data.map { it.node }
    }

    fun searchManga(query: String, limit: Int): List<MangaNode> {
      MalUtil.printDebug("getMangaList - enter")
      val response = runBlocking {
        ktor.get("manga") {
          parameter("q", query)
          parameter("limit", limit.absoluteValue)
          parameter("fields", "status,mean,rank,popularity,num_episodes,average_episode_duration,list_status")
        }
      }

      val result = runBlocking {
        response.getBody<MalGenericListResponse<MangaData>>()
      } ?: MalGenericListResponse(emptyList())

      return result.data.map { it.node }
    }

    /**
     * MAL api seems to return the same item when you set this to one, so could just always pull 100 and randomize it
     */
    fun getSuggestedAnime(limit: Int): List<AnimeNode> {
      MalUtil.printDebug("getSuggestedAnime - enter")
      val limitToUse = minOf(limit.absoluteValue, 100)

      val response = runBlocking {
        ktor.get("anime/suggestions") {
          parameter("limit", limitToUse)
          oauthHeader()
        }
      }

      val result = runBlocking {
        response.getBody<MalGenericListResponse<AnimeData>>()
      } ?: MalGenericListResponse(emptyList())

      return result.data.map {
        it.node
      }
    }

    private fun HttpMessageBuilder.oauthHeader(token: String? = null) {
      header("Authorization", "Bearer ${token ?: FileUtil.getUserSecrets().oauthTokens?.accessToken}")
    }

    private suspend inline fun <reified T> HttpResponse.getBody(): T? {
      return try {
        if (this.status.isSuccess()) {
          return this.body<T>()
        } else {
          MalUtil.printError("Invalid response from MAL: $this")
          null
        }
      } catch (e: Exception) {
        MalUtil.printError("Failed to read body from MAL: $this")
        null
      }
    }

    fun refreshOAuthToken(): String? {
      MalUtil.printDebug("refreshOAuthToken - enter")

      val userSecrets = FileUtil.getUserSecrets()

      val response = runBlocking {
        ktor.post("https://myanimelist.net/v1/oauth2/token") {
          setBody(
            FormDataContent(
              Parameters.build {
                append("grant_type", "refresh_token")
                append("refresh_token", userSecrets.oauthTokens?.refreshToken!!)
                append("client_id", userSecrets.clientId)
              }
            )
          )
        }
      }

      val result = runBlocking {
        response.getBody<MalOAuthResponse>()!!
      }

      val updatedSecrets = userSecrets.copy(oauthTokens = result)
      FileUtil.updateUserSecrets(updatedSecrets)
      return updatedSecrets.oauthTokens?.accessToken
    }
  }
}
