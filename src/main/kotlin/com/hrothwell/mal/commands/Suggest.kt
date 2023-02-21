package com.hrothwell.mal.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.hrothwell.mal.client.MalClient
import com.hrothwell.mal.util.MalUtil

class Suggest : CliktCommand(
  help = "Get anime suggestions! Manga suggestions not yet supported by MAL"
) {

  val limit by option(
    "-l", "--limit", help = """
    limit on the amount of anime to suggest. Default 1, max 100
  """.trimIndent()
  )
    .default("1")

  override fun run() {
    try {
      val animeList = MalClient.getSuggestedAnime(limit.toInt())
      echo("Try: ${animeList.map { it.title }}")
      if (animeList.size == 1) {
        MalUtil.openAnime(animeList.first())
      }
    } catch (t: Throwable) {
      echoError(
        """
        unable to suggest anime. Message: ${t.message}
        cause: ${t.cause}
      """.trimIndent()
      )
    }
  }

  private fun echoError(msg: String) {
    echo("${MalUtil.RED} $msg ${MalUtil.RESET}", err = true)
  }
}