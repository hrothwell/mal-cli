package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.domain.*
import kotlinx.serialization.decodeFromString

class Random: CliktCommand(
  help = "pick a random anime from your MAL lists",
  epilog = """
    extended user guide, source code, etc - https://github.com/hrothwell/anime-cli
  """.trimIndent()
) {
  private val possibleListStatusValues = ListStatus.values().map { it.malValue }.toTypedArray()
  private val user by option("-u", "--user-name", help = """
    user name. if not provided this will default to "user_name" value in "$secretLocation"
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

  private fun getRandomAnime(user: String?, list: String) {

    val clientSecrets = getUserSecrets()
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
    handlePotentialHttpErrors(response.second)

    try {
      val json = String(response.third.get())
      val result = jsonReader.decodeFromString<MALUserListResponse>(json)
      echo("Anime selected: ${result.data.random().node.title}")
    } catch (t: Throwable) {
      echoError("couldn't read the response from MAL: $t")
      echoError("Stack trace of error: \n")
      t.printStackTrace(System.err)
    }
  }

  // can't put in constants as echo is protected
  fun echoError(msg: String) {
    echo("$RED $msg", err = true)
  }
}