package com.hrothwell.anime

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.domain.MALUserListResponse
import java.io.File

class Anime : CliktCommand(
  help = "pick a random anime from your MAL lists",
  epilog = """
    You will need a MAL api key to use this. This api key should be placed in your home directory as anime-cli/mal-secret.txt
    Ex: C:\Users\hone_the_rat\anime-cli\mal-secret.txt
  """.trimIndent()
) {
  private val RED = "\u001b[31m"
  private val user by option("-u", "--user", help = "your user name").default("hone_the_rat")
  private val list by option("-l", "--list", help = "list to select from").default("plan_to_watch")

  private val objectMapper = jacksonObjectMapper()
  override fun run() {
    // disable err to ignore the stupid warning messages about illegal reflection or whatever
    echo(getRandomAnime(user, list))
  }

  fun getRandomAnime(user: String, list: String): Any? {
    // TODO screw it force the file to $HOME/something
    val home = System.getProperty("user.home")
    val secretLocation = "$home/anime-cli/mal-secret.txt"
    val clientId = try {
      File(secretLocation).readText()
    } catch (t: Throwable) {
      echoError("You need to place your MAL client id in this file: $secretLocation")
      return ""
    }

    val headers = "X-MAL-CLIENT-ID" to clientId
    val listStatus = "listStatus" to list
    val limit = "limit" to 1000
    val request =
      Fuel.get(path = "https://api.myanimelist.net/v2/users/$user/animelist", parameters = listOf(listStatus, limit))
        .appendHeader(headers)
    // TODO check for errors before getting result here
    val response = request.response().third.get()

    return try {
      val result = objectMapper.readValue(response, MALUserListResponse::class.java)
      result.data.random()
    } catch (t: Throwable) {
      echo("oops $t")
      response
    }
  }

  fun echoError(msg: String) {
    echo("$RED $msg", err = true)
  }
}