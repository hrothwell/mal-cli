package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.hrothwell.anime.client.MALClient
import com.hrothwell.anime.util.AnimeUtil

class Refresh: CliktCommand(
  help = "call to refresh your oauth tokens"
) {

  override fun run() {
    echo("refreshing tokens")
    try{
      MALClient.refreshOAuthToken()
    } catch(t: Throwable){
      echoError("""
        unable to refresh tokens. Message: ${t.message}
        cause: ${t.cause}
      """.trimIndent())
    }
    echo("tokens refreshed")
  }
  private fun echoError(msg: String) {
    echo("${AnimeUtil.RED} $msg ${AnimeUtil.RESET}", err = true)
  }
}