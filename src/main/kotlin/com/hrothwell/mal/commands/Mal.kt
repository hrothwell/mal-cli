package com.hrothwell.mal.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.hrothwell.mal.util.MalUtil

class Mal : CliktCommand(
  help = "CLI for interacting with MAL",
  epilog = """
    extended user guide, source code, report issues, etc - https://github.com/hrothwell/mal-cli
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
  
  val animeOrManga by option(
    "-a", help = """
      to run commands for anime (-a) or manga (-m)
    """.trimIndent()
  ).flag("-m", default = true)

  override fun run() {
    MalUtil.debug = debug
    MalUtil.shouldOpenUrls = loud
    MalUtil.isAnime = animeOrManga
    if (MalUtil.debug) {
      echo("debug is on")
    }
  }
}