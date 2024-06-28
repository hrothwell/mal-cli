package com.hrothwell.mal.util

import com.github.kittinunf.fuel.core.Response
import com.hrothwell.mal.domain.response.AnimeNode
import com.hrothwell.mal.domain.response.MangaNode
import com.hrothwell.mal.exception.MALResponseException
import java.awt.Desktop
import java.net.URI

/**
 * Static util for common functions used in commands
 */
class MalUtil {
  companion object {
    var debug = false
    var shouldOpenUrls = true
    var isAnime = true // true, search anime, false search manga. Think of an enum name or something
    val RED = "\u001b[31m"
    val RESET = "\u001b[0m"

    val quickErrorHelp = """
      Quick help / common situations:
        - 400/403: no client_id set in mal-secret.json or said client_id is invalid. See https://myanimelist.net/apiconfig
        - 401: unauthorized, try running "login" again
        - 404: user was not found. Was the user a real user? If no user passed in, is your mal-secret.json setup correctly?
    """.trimIndent()

    fun handlePotentialHttpErrors(response: Response) {
      printDebug("Handling potential http errors for response. URL: ${response.url}")
      if (response.statusCode != 200) {
        throw MALResponseException(
          """
          Could not call ${response.url}
          ${response.statusCode} error returned from MAL: ${response.body()}
          
          $quickErrorHelp
        """.trimIndent()
        )
      }
    }

    fun printDebug(msg: String) {
      if (debug) {
        println(msg)
      }
    }

    fun openUrl(url: String) {
      if (shouldOpenUrls) {
        try {
          if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url));
          } else {
            println(url)
          }
        } catch (t: Throwable) {
          System.err.println("couldn't open url, $t")
        }
      }
    }

    // Should maybe move under MALClient? doesn't interact with the same API though
    fun openAnime(anime: AnimeNode?) {
      if (anime == null) return
      openUrl("https://myanimelist.net/anime/${anime.id}")
    }

    fun openManga(manga: MangaNode?) {
      if (manga == null) return
      openUrl("https://myanimelist.net/manga/${manga.id}")
    }
  }
}
