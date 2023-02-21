package com.hrothwell.anime.domain.mal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaNode(
  val id: Long,
  val title: String,
  val status: MangaPublishingStatus? = null,
  val mean: Float? = null,
  val rank: Int? = null,
  val popularity: Int? = null,
  @SerialName("num_volumes")
  val numVolumes: Int? = null,
  @SerialName("num_chapters")
  val numChapters: Int? = null,
)