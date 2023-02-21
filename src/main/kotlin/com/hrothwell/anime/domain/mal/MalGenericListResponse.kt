package com.hrothwell.anime.domain.mal

import kotlinx.serialization.Serializable

@Serializable
data class MalGenericListResponse<T>(
  val data: List<T>,
  // TODO paging
)