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

  val loud by option(
    "--loud", help = """
      attempt to open any anime pages, open login url, etc. default true / loud
    """.trimIndent()
  ).flag("--quiet", default = true)

  override fun run() {
    AnimeUtil.debug = debug
    AnimeUtil.shouldOpenUrls = loud
    if (AnimeUtil.debug) {
      echo("debug is on")
    }
  }
}