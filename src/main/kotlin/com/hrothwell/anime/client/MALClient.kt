package com.hrothwell.anime.client

import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.domain.MALUserListResponse
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.FileUtil
import kotlinx.serialization.decodeFromString

/**
 * Client for all calls that are NOT part of the login flow (for now)
 */
class MALClient {
  companion object{
    // todo consistent process to check and refresh token, retry calls that fail for 401 once. If fail again, prompt to login again

    fun getRandomAnime(user: String?, list: String): String{
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
      val result = FileUtil.jsonReader.decodeFromString<MALUserListResponse>(json)
      val animeTitle = result.data.randomOrNull()?.node?.title
      return animeTitle?.let{
        "Random selection: $it"
      } ?: "$userPathParam's $list was empty"
    }
  }
}