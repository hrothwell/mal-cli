package com.hrothwell.anime.domain.mal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Status in a user's list
 */
@Serializable
enum class AnimeListStatus(val malValue: String) {
  @SerialName("completed")
  COMPLETED("completed"),

  @SerialName("plan_to_watch")
  PLAN_TO_WATCH("plan_to_watch"),

  @SerialName("watching")
  WATCHING("watching"),

  @SerialName("on_hold")
  ON_HOLD("on_hold"),

  @SerialName("dropped")
  DROPPED("dropped")
}