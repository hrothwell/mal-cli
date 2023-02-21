package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.hrothwell.anime.client.MalAnimeClient
import com.hrothwell.anime.domain.mal.AnimeListStatus
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.FileUtil

class Random : CliktCommand(
  help = """
    Select a random anime from your lists
  """.trimIndent()
) {

  private val possibleListStatusValues = AnimeListStatus.values().map { it.malValue }.toTypedArray()
  private val user by option(
    "-u", "--user-name", help = """
    user name. if not provided this will default to "user_name" value in "${FileUtil.home}/anime-cli/mal-secret.json"
  """.trimIndent()
  )

  private val list by option(
    "-l",
    "--list-status",
    help = "list to select from"
  ).choice(choices = possibleListStatusValues)
    .default("plan_to_watch")

  private val includeNotYetAired by option(
    "--include-not-yet-aired", help = """
    include anime that have not yet aired, default to exclude
  """.trimIndent()
  )
    .flag("--exclude-not-yet-aired", default = false)

  override fun run() {
    try {
      val anime = MalAnimeClient.getRandomAnime(user, list, includeNotYetAired)
      val title = if (anime != null) "Random selection: ${anime.title}" else "$user's list was empty"
      echo(title)
      AnimeUtil.openAnime(anime)
    } catch (t: Throwable) {
      echoError(
        """
        Unable to retrieve random anime. Message: ${t.message}
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