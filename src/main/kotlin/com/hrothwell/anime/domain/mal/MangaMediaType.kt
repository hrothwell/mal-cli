package com.hrothwell.anime.domain.mal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MangaMediaType(val malValue: String) {
  @SerialName("unknown")
  UNKNOWN("unknown"),

  @SerialName("manga")
  MANGA("manga"),

  @SerialName("novel")
  NOVEL("novel"),

  @SerialName("one_shot")
  ONE_SHOT("one_shot"),

  @SerialName("doujinshi")
  DOUJINSHI("doujinshi"),

  @SerialName("manhwa")
  MANHWA("manhwa"),

  @SerialName("manhua")
  MANHUA("manhua"),

  @SerialName("oel")
  OEL("oel")
}