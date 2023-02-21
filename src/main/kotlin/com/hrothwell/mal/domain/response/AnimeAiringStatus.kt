package com.hrothwell.mal.domain.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AnimeAiringStatus {
  @SerialName("finished_airing")
  FINISHED_AIRING,

  @SerialName("currently_airing")
  CURRENTLY_AIRING,

  @SerialName("not_yet_aired")
  NOT_YET_AIRED
}