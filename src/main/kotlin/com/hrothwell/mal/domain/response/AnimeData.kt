package com.hrothwell.mal.domain.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeData(
  val node: AnimeNode,
  @SerialName("list_status")
  val listStatus: AnimeUserListStatus? = null
)