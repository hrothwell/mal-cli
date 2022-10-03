package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
// Main entry point, all other commands are subcommands
class Anime : CliktCommand(
  help = "CLI to interact with MyAnimeList api v2",
  epilog = """
    extended user guide, source code, etc - https://github.com/hrothwell/anime-cli
  """.trimIndent()
) {
  override fun run() = Unit
}