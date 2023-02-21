package com.hrothwell.anime.domain.mal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MangaPublishingStatus {
  @SerialName("finished")
  FINISHED,

  @SerialName("currently_publishing")
  CURRENTLY_PUBLISHING,

  @SerialName("not_yet_published")
  NOT_YET_PUBLISHED
}