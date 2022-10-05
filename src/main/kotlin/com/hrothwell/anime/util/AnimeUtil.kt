package com.hrothwell.anime.util

import com.github.kittinunf.fuel.core.Response
import kotlin.system.exitProcess

/**
 * Static util for common functions used in commands
 */
class AnimeUtil {
  companion object{

    val RED = "\u001b[31m"

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

    private fun quickErrorHelp(){
      printError("""
      Quick help / common situations:
        - 400/403: no client_id set in mal-secret.json or said client_id is invalid. See https://myanimelist.net/apiconfig
        - 401: unauthorized, try running "login" again
        - 404: user was not found. Was the user a real user? If no user passed in, is your mal-secret.json setup correctly?
    """.trimIndent())
    }
  }
}