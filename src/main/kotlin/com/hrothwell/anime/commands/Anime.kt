package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand

class Anime : CliktCommand(
  help = "CLI for interacting with MAL",
  epilog = """
    extended user guide, source code, etc - https://github.com/hrothwell/anime-cli
  """.trimIndent()
) {
  override fun run() = Unit
}