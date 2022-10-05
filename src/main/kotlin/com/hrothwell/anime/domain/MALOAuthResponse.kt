package com.hrothwell.anime.domain

import kotlinx.serialization.Serializable

@Serializable
data class MALOAuthResponse(
  val token_type: String,
  val expires_in: Long,
  val access_token: String,
  val refresh_token: String
)