package com.hrothwell.mal.domain.response

import kotlinx.serialization.Serializable

@Serializable
data class MalGenericListResponse<T>(
  val data: List<T>,
  // TODO paging
)