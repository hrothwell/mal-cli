package com.hrothwell.mal.domain.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaNode(
  val id: Long,
  val title: String,
  val status: String? = null,
  val mean: Float? = null,
  val rank: Int? = null,
  val popularity: Int? = null,
  @SerialName("num_volumes")
  val numVolumes: Int? = null,
  @SerialName("num_chapters")
  val numChapters: Int? = null,
)