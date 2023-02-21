package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.hrothwell.anime.util.AnimeUtil

class Anime : CliktCommand(
  help = "CLI for interacting with MAL",
  epilog = """
    extended user guide, source code, report issues, etc - https://github.com/hrothwell/anime-cli
  """.trimIndent()
) {
  val debug by option(
    "--debug", help = """
    run in debug mode and get more log output
  """.trimIndent()
  ).flag("--no-debug", default = false)

  override fun run() {
    AnimeUtil.debug = debug
    if (AnimeUtil.debug) {
      echo("debug is on")
    }
  }
}