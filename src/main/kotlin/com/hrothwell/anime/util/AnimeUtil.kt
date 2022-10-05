package com.hrothwell.anime.util

import com.github.kittinunf.fuel.core.Response
import com.hrothwell.anime.exception.MALResponseException
import kotlin.system.exitProcess

/**
 * Static util for common functions used in commands
 */
class AnimeUtil {
  companion object{

    val RED = "\u001b[31m"

    val quickErrorHelp = """
      Quick help / common situations:
        - 400/403: no client_id set in mal-secret.json or said client_id is invalid. See https://myanimelist.net/apiconfig
        - 401: unauthorized, try running "login" again
        - 404: user was not found. Was the user a real user? If no user passed in, is your mal-secret.json setup correctly?
    """.trimIndent()

    fun handlePotentialHttpErrors(response: Response) {
      if(response.statusCode != 200){
        throw MALResponseException("""
          Could not call ${response.url}
          ${response.statusCode} error returned from MAL: ${response.responseMessage}
          $quickErrorHelp
        """.trimIndent())
      }
    }
  }
}