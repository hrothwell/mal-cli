package com.hrothwell.anime.domain.mal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * similar to the anime one, it appears that quite a bit of things are missing...
 */
@Serializable
data class MangaUserListStatus(
  val status: MangaListStatus,
  val score: Int,
  @SerialName("num_volumes_read")
  val numVolumesRead: Int,
  @SerialName("num_chapters_read")
  val numChaptersRead: Int,
  @SerialName("updated_at")
  val updatedAt: String
)