package com.hrothwell.anime.domain
import kotlinx.serialization.Serializable
@Serializable
data class MALUserListResponse(
  val data: List<Data>
)