package com.hrothwell.anime.domain.mal

import kotlinx.serialization.Serializable

@Serializable
data class MALAnimeListResponse(
  val data: List<AnimeData>,
  // TODO paging
)