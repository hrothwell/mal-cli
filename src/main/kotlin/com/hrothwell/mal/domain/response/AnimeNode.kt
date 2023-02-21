package com.hrothwell.mal.domain.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeNode(
  val id: Long,
  val title: String,
  val status: AnimeAiringStatus? = null,
  val mean: Float? = null,
  val rank: Int? = null,
  val popularity: Int? = null,
  @SerialName("num_episodes")
  val numEpisodes: Int? = null,
  @SerialName("average_episode_duration")
  val averageEpisodeDurationSeconds: Int? = null,
)