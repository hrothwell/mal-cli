package com.hrothwell.mal.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.hrothwell.mal.client.MalClient
import com.hrothwell.mal.util.MalUtil

class Refresh : CliktCommand(
  help = "call to refresh your oauth tokens"
) {

  override fun run() {
    echo("refreshing tokens")
    try {
      MalClient.refreshOAuthToken()
    } catch (t: Throwable) {
      echoError(
        """
        unable to refresh tokens. Message: ${t.message}
        cause: ${t.cause}
      """.trimIndent()
      )
    }
    echo("tokens refreshed")
  }

  private fun echoError(msg: String) {
    echo("${MalUtil.RED} $msg ${MalUtil.RESET}", err = true)
  }
}