package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.domain.ListStatus
import com.hrothwell.anime.domain.MALUserListResponse
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.AnimeUtil.Companion.RED
import kotlinx.serialization.decodeFromString

class Anime : CliktCommand(
  help = "CLI for interacting with MAL",
  epilog = """
    extended user guide, source code, etc - https://github.com/hrothwell/anime-cli
  """.trimIndent()
) {
  override fun run() = Unit
}