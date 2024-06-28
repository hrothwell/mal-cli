package com.hrothwell.mal.util

import com.hrothwell.mal.domain.response.AnimeNode
import com.hrothwell.mal.domain.response.MangaNode
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

    fun printDebug(msg: String) {
      if (debug) {
        println(msg)
      }
    }

    fun openUrl(url: String) {
      if (shouldOpenUrls) {
        try {
            println(url)
//          if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//            Desktop.getDesktop().browse(URI(url))
//          } else {
//            println(url)
//          }
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
