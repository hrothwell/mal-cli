package com.hrothwell.mal

import com.github.ajalt.clikt.core.subcommands
import com.hrothwell.mal.commands.*

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