package com.hrothwell.anime.util

import com.github.kittinunf.fuel.core.Response
import com.hrothwell.anime.domain.UserSecrets
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

/**
 * Static util for common functions used in commands
 */
class AnimeUtil {
  companion object{
    val commands = hashSetOf("login", "random")
    val defaultHelp = """
      Possible commands: $commands
      To learn more run commands with "-h" flag
    """.trimIndent()
    val RED = "\u001b[31m"
    val home = System.getProperty("user.home")
    private val secretLocation = "$home/anime-cli/mal-secret.json"
    val jsonReader = Json{ignoreUnknownKeys = true}

    fun getUserSecrets(): UserSecrets{
      return try {
        val clientSecretsJsonContent = File(secretLocation).readText()
        jsonReader.decodeFromString<UserSecrets>(clientSecretsJsonContent)
      } catch (t: Throwable) {
        println("ERROR trying to get secrets from companion object function")
        printError(""" Error getting mal-secret.json
        You need to place your MAL client id in this file: $secretLocation. File contents should look like:
        {
          "user_name": "my user name",
          "client_id": "my MAL API client ID"
        }
      """.trimIndent())
        t.printStackTrace(System.err)
        exitProcess(1)
      }
    }

    fun quickErrorHelp(){
      // TODO add 401 to this list, need to know more about oauth flow first tho
      printError("""
      Quick help / common situations:
        - 400/403: no client_id set in mal-secret.json or said client_id is invalid. See https://myanimelist.net/apiconfig
        - 404: user was not found. Was the user a real user? If no user passed in, is your mal-secret.json setup correctly?
    """.trimIndent())
    }

    fun handlePotentialHttpErrors(response: Response) {
      if(response.statusCode != 200){
        printError("${response.statusCode} error returned from MAL: ${response.responseMessage}")
        quickErrorHelp()
        exitProcess(1)
      }
    }

    // TODO if this doesn't work right just put echoError in everything nbd
    fun printError(msg: String){
      System.err.print("$RED $msg")
    }
  }
}