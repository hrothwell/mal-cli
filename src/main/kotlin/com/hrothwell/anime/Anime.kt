package com.hrothwell.anime

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import com.hrothwell.anime.domain.ListStatus
import com.hrothwell.anime.domain.MALUserListResponse
import com.hrothwell.anime.domain.UserSecrets
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

class Anime : CliktCommand(
  help = "pick a random anime from your MAL lists",
  epilog = """
    extended user guide, source code, etc - https://github.com/hrothwell/anime-cli
  """.trimIndent()
) {
  val home = System.getProperty("user.home")
  val secretLocation = "$home/anime-cli/mal-secret.json"
  val jsonReader = Json{ignoreUnknownKeys = true}

  private val possibleListStatusValues = ListStatus.values().map { it.malValue }.toTypedArray()
  private val RED = "\u001b[31m"
  private val user by option("-u", "--user-name", help = """
    user name. if not provided this will default to "user_name" value in "${home}/anime-cli-mal-secret.json"
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

  fun handlePotentialHttpErrors(response: Response) {
    if(response.statusCode != 200){
      echoError("${response.statusCode} error returned from MAL: ${response.responseMessage}")
      quickErrorHelp()
      exitProcess(1)
    }
  }

  fun getUserSecrets(): UserSecrets{
    return try {
      val clientSecretsJsonContent = File(secretLocation).readText()
      jsonReader.decodeFromString<UserSecrets>(clientSecretsJsonContent)
    } catch (t: Throwable) {
      echoError("""
        You need to place your MAL client id in this file: $secretLocation. File contents should look like:
        {
          "user_name": "my user name",
          "client_id": "my MAL API client ID"
        }
      """.trimIndent())
      exitProcess(1)
    }
  }

  fun quickErrorHelp(){
    echoError("""
      Quick help / common situations:
        - 400/403: no client_id set in mal-secret.json or said client_id is invalid. See https://myanimelist.net/apiconfig
        - 404: user was not found. Was the user you passed in ($user) a real user? If no user passed in, is your mal-secret.json setup correctly?
    """.trimIndent())
  }

  private fun echoError(msg: String) {
    echo("$RED $msg", err = true)
  }
}