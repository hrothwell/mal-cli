package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.hrothwell.anime.client.MalClient
import com.hrothwell.anime.domain.mal.AnimeListStatus
import com.hrothwell.anime.domain.mal.MangaListStatus
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.FileUtil

class Random : CliktCommand(
  help = """
    Select a random anime/manga from your lists
  """.trimIndent()
) {

  private val possibleAnimeListStatusValues =
    AnimeListStatus.values().map { it.malValue }
  private val possibleMangaListStatuses = MangaListStatus.values().map { it.malValue }


  private val allPossibleValues = (possibleAnimeListStatusValues + possibleMangaListStatuses).distinct().toTypedArray()

  private val user by option(
    "-u", "--user-name", help = """
    user name. if not provided this will default to "user_name" value in "${FileUtil.home}/anime-cli/mal-secret.json"
  """.trimIndent()
  )

  private val list by option(
    "-l",
    "--list-status",
    help = "list to select from"
  ).choice(choices = allPossibleValues)
    .default("plan_to_watch")

  private val includeNotYetReleased by option(
    "--include-not-yet-released", help = """
    include anime/manga that have not yet aired/published, default to exclude
  """.trimIndent()
  )
    .flag("--exclude-not-yet-released", default = false)

  override fun run() {
    try {
      if (AnimeUtil.isAnime) {
        val anime = MalClient.getRandomAnime(user, list, includeNotYetReleased)
        val title = if (anime != null) "Random selection: ${anime.title}" else "$user's list was empty"
        echo(title)
        AnimeUtil.openAnime(anime)
      } else {
        // little switcheroo of the default
        val mangaList = if (list == "plan_to_watch") "plan_to_read" else list
        val manga = MalClient.getRandomManga(user, mangaList, includeNotYetReleased)
        val title = if (manga != null) "Random selection: ${manga.title}" else "$user's list was empty"
        echo(title)
        AnimeUtil.openManga(manga)
      }
    } catch (t: Throwable) {
      echoError(
        """
        Unable to retrieve random anime / manga. Message: ${t.message}
        Cause: ${t.cause}
        Stacktrace: ${t.stackTraceToString()}
      """.trimIndent()
      )
    }
  }

  private fun echoError(msg: String) {
    echo("${AnimeUtil.RED} $msg ${AnimeUtil.RESET}", err = true)
  }
}