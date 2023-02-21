package com.hrothwell.anime.domain.mal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaData(
  val node: MangaNode,
  @SerialName("list_status")
  val listStatus: MangaUserListStatus? = null
)