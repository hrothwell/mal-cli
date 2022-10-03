package com.hrothwell.anime

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    Anime().main(args)
  // TODO adding subcommands seemed to break native image as it no longer created reflection-config.json. Tried several things with no luck.
    // see if there is some other way to set subcommands, or just use flags to dictate logic?
  }
}