package com.hrothwell.mal.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.hrothwell.mal.client.MalClient
import com.hrothwell.mal.util.MalUtil

class Search : CliktCommand(
  help = """
    Search MAL for an anime/manga
  """.trimIndent()
) {

  private val keywords by option(
    "-q", "--query", help = """
    search terms to use, surround multiple words in quotes: "Tokyo Ghoul" or 'Tokyo Ghoul'
  """.trimIndent()
  ).required()

  private val limit by option(
    "-l", "--limit", help = """
    limit of items to return from the search. MAL limits to 100
  """.trimIndent()
  ).default("1")

  override fun run() {
    try {
      if (MalUtil.isAnime) {
        val animeList = MalClient.getAnimeList(keywords, limit.toInt())
        echo(animeList.map { it.title })
        if (animeList.size == 1) {
          MalUtil.openAnime(animeList.first())
        }
      } else {
        val mangaList = MalClient.getMangaList(keywords, limit.toInt())
        echo(mangaList.map { it.title })
        if (mangaList.size == 1) {
          MalUtil.openManga(mangaList.first())
        }
      }

    } catch (t: Throwable) {
      echoError(
        """
        error searching anime / manga. Message: ${t.message}
        
        cause: ${t.cause}
      """.trimIndent()
      )
    }
  }

  private fun echoError(msg: String) {
    echo("${MalUtil.RED} $msg ${MalUtil.RESET}", err = true)
  }
}