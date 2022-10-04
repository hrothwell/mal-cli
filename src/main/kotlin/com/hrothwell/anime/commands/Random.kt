package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.domain.ListStatus
import com.hrothwell.anime.domain.MALUserListResponse
import com.hrothwell.anime.util.AnimeUtil
import kotlinx.serialization.decodeFromString

class Random: CliktCommand(
  help = """
    Select a random anime from your lists
  """.trimIndent()
) {

  private val possibleListStatusValues = ListStatus.values().map { it.malValue }.toTypedArray()
  private val user by option("-u", "--user-name", help = """
    user name. if not provided this will default to "user_name" value in "${AnimeUtil.home}/anime-cli/mal-secret.json"
  """.trimIndent())

  private val list by option(
    "-l",
    "--list-status",
    help = "list to select from"
  ).choice(choices = possibleListStatusValues)
    .default("plan_to_watch")

  override fun run() {
    getRandomAnime(user, list)
  }

  fun getRandomAnime(user: String?, list: String) {

    val clientSecrets = AnimeUtil.getUserSecrets()

    val headers = "X-MAL-CLIENT-ID" to clientSecrets.client_id
    val listStatus = "status" to list
    val limit = "limit" to 1000
    val userPathParam = user ?: clientSecrets.user_name

    val request =
      Fuel.get(path = "https://api.myanimelist.net/v2/users/$userPathParam/animelist", parameters = listOf(listStatus, limit))
        .appendHeader(headers)

    // TODO check for errors before getting result here
    //   (also why does Fuel do this this way it feels weird to call .third)
    val response = request.response()
    AnimeUtil.handlePotentialHttpErrors(response.second)

    try {
      val json = String(response.third.get())
      val result = AnimeUtil.jsonReader.decodeFromString<MALUserListResponse>(json)
      echo("Anime selected: ${result.data.randomOrNull()?.node?.title ?: "$user's $list list was empty"}")
    } catch (t: Throwable) {
      echoError("couldn't read the response from MAL: $t")
      echoError("Stack trace of error: \n")
      t.printStackTrace(System.err)
    }
  }

  private fun echoError(msg: String) {
    echo("${AnimeUtil.RED} $msg", err = true)
  }
}