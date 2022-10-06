package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.hrothwell.anime.client.MALClient

class Refresh: CliktCommand(
  help = "call to refresh your oauth tokens"
) {

  override fun run() {
    echo("refreshing tokens")
    MALClient.refreshOAuthToken()
    echo("tokens refreshed")
  }
}