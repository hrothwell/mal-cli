package com.hrothwell.anime.domain
import kotlinx.serialization.Serializable
@Serializable
data class MALAnimeListResponse(
  val data: List<Data>
)