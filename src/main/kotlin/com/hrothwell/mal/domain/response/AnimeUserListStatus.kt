package com.hrothwell.mal.domain.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Some values that could be useful and are actually returned... the documentation
 * says more is returned but it just isnt there, with or without oauth
 */
@Serializable
data class AnimeUserListStatus(
  val status: String,
  val score: Int,
  @SerialName("num_episodes_watched")
  val numEpisodesWatched: Int,
  @SerialName("updated_at")
  val updatedAt: String
)