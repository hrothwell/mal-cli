package com.hrothwell.anime

import com.github.ajalt.clikt.core.subcommands
import com.hrothwell.anime.commands.*

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    Mal().subcommands(
      Login(),
      Random(),
      Suggest(),
      Refresh(),
      Search()
    ).main(args)
  }
}