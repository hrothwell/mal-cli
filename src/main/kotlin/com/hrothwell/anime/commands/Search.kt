package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.hrothwell.anime.client.MALClient
import com.hrothwell.anime.util.AnimeUtil

class Search: CliktCommand(
  help = """
    Search MAL for an anime
  """.trimIndent()
) {

  private val keywords by option("-q", "--query", help = """
    search terms to use, surrounded mutliple words in quotes: "Tokyo Ghoul" or 'Tokyo Ghoul'
  """.trimIndent()).default("Made in Abyss")
  private val limit by option("-l", "--limit", help = """
    limit of items to return from the search. MAL limits to 100
  """.trimIndent()).default("1")

  override fun run() {
    try{
      echo(MALClient.getAnimeList(keywords, limit))
    } catch(t: Throwable){
      echoError("""
        error searching anime. Message: ${t.message}
        
        cause: ${t.cause}
      """.trimIndent())
    }
  }

  private fun echoError(msg: String) {
    echo("${AnimeUtil.RED} $msg ${AnimeUtil.RESET}", err = true)
  }
}