package com.hrothwell.anime

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.domain.ListStatus
import com.hrothwell.anime.domain.MALUserListResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import java.io.File

class Anime : CliktCommand(
  help = "pick a random anime from your MAL lists",
  epilog = """
    You will need a MAL api key to use this. This api key should be placed in your home directory as anime-cli/mal-secret.txt
    Ex: C:\Users\hone_the_rat\anime-cli\mal-secret.txt
  """.trimIndent()
) {
  private val possibleListStatusValues = ListStatus.values().map { it.malValue }.toTypedArray()
  private val RED = "\u001b[31m"
  private val user by option("-u", "--user-name", help = "your user name").default("hone_the_rat")

  private val list by option(
    "-l",
    "--list-status",
    help = "list to select from"
  ).choice(choices = possibleListStatusValues)
    .default("plan_to_watch")

  override fun run() {
    getRandomAnime(user, list)
  }

  fun getRandomAnime(user: String, list: String) {
    // TODO screw it force users to put a client id in a file somewhere because it just wouldn't work nicely with getResourceAsStream or anything
    //  also don't want to just have my client secret in git. Don't know how this will work if running natively tho
    val home = System.getProperty("user.home")
    val secretLocation = "$home/anime-cli/mal-secret.txt"
    val clientId = try {
      File(secretLocation).readText()
    } catch (t: Throwable) {
      echoError("You need to place your MAL client id in this file: $secretLocation")
      return
    }

    val headers = "X-MAL-CLIENT-ID" to clientId
    val listStatus = "status" to list
    val limit = "limit" to 1000

    val request =
      Fuel.get(path = "https://api.myanimelist.net/v2/users/$user/animelist", parameters = listOf(listStatus, limit))
        .appendHeader(headers)

    // TODO check for errors before getting result here
    //   (also why does Fuel do this this way it feels weird to call .third)
    val response = request.response()
    val json = String(response.third.get())

    try {
      val jsonReader = Json{ignoreUnknownKeys = true}
      // work around for kotlinx and graalvm native stuff. Wrap it as a list then do this
      val result = jsonReader.decodeFromString<MALUserListResponse>(json)
      echo("watch this: ${result.data.random().node.title}")
    } catch (t: Throwable) {
      echoError("couldn't read the response from MAL: $t")
    }
  }

  private fun echoWarn(msg: String) {
    // TODO add colors
    echo(msg)
  }

  private fun echoError(msg: String) {
    echo("$RED $msg", err = true)
  }
}