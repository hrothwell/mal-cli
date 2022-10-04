package com.hrothwell.anime

import com.github.ajalt.clikt.core.subcommands
import com.hrothwell.anime.commands.Anime
import com.hrothwell.anime.commands.Login
import com.hrothwell.anime.commands.Random

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    Anime().subcommands(Login(), Random()).main(args)
  }
}