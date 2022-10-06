package com.hrothwell.anime

import com.github.ajalt.clikt.core.subcommands
import com.hrothwell.anime.commands.*

// TODO a debug logger option would be nice
object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    Anime().subcommands(Login(), Random(), Suggest(), Refresh()).main(args)
  }
}