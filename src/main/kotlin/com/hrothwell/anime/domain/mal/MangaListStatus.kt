package com.hrothwell.anime.domain.mal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MangaListStatus(val malValue: String) {
  @SerialName("completed")
  COMPLETED("completed"),

  @SerialName("plan_to_read")
  PLAN_TO_READ("plan_to_read"),

  @SerialName("reading")
  READING("reading"),

  @SerialName("on_hold")
  ON_HOLD("on_hold"),

  @SerialName("dropped")
  DROPPED("dropped")
}