package com.hrothwell.anime.util

import com.github.kittinunf.fuel.core.Response
import com.hrothwell.anime.exception.MALResponseException
import kotlin.system.exitProcess

/**
 * Static util for common functions used in commands
 */
class AnimeUtil {
  companion object{
    var debug = false
    val RED = "\u001b[31m"
    val RESET = "\u001b[0m"

    val quickErrorHelp = """
      Quick help / common situations:
        - 400/403: no client_id set in mal-secret.json or said client_id is invalid. See https://myanimelist.net/apiconfig
        - 401: unauthorized, try running "login" again
        - 404: user was not found. Was the user a real user? If no user passed in, is your mal-secret.json setup correctly?
    """.trimIndent()

    fun handlePotentialHttpErrors(response: Response) {
      printDebug("Handling potential http errors for response. URL: ${response.url}")
      if(response.statusCode != 200){
        throw MALResponseException("""
          Could not call ${response.url}
          ${response.statusCode} error returned from MAL: ${response.body()}
          
          $quickErrorHelp
        """.trimIndent())
      }
    }

    fun printDebug(msg: String){
      if(debug){
        println(msg)
      }
    }

    fun openUrl(url: String){
      try{
        // use cmd if on windows to just open it
        if(System.getProperty("os.name").lowercase().contains("windows")){
          Runtime.getRuntime().exec("""
        cmd.exe /c start "" "$url"
      """.trimIndent())
        }
        // TODO will need to test if this actually works on mac (developing on Windows)
        else{
          Runtime.getRuntime().exec("""
            open "$url"
          """.trimIndent())
        }
      } catch(t: Throwable){
        System.err.println("couldn't open url, $t")
      }
    }
  }
}