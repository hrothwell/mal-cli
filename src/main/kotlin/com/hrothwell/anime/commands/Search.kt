package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.hrothwell.anime.client.MALClient
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.AnimeUtil.Companion.openAnime

class Search: CliktCommand(
  help = """
    Search MAL for an anime
  """.trimIndent()
) {

  private val keywords by option("-q", "--query", help = """
    search terms to use, surround multiple words in quotes: "Tokyo Ghoul" or 'Tokyo Ghoul'
  """.trimIndent()).required()

  private val limit by option("-l", "--limit", help = """
    limit of items to return from the search. MAL limits to 100
  """.trimIndent()).default("1")

  override fun run() {
    try{
      val animeList = MALClient.getAnimeList(keywords, limit.toInt())
      echo(animeList.map{ it.title })
      if(animeList.size == 1){
        openAnime(animeList.first())
      }
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