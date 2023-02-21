package com.hrothwell.anime.domain.mal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MALOAuthResponse(
  @SerialName("token_type")
  val tokenType: String,
  @SerialName("expires_in")
  val expiresIn: Long,
  @SerialName("access_token")
  val accessToken: String,
  @SerialName("refresh_token")
  val refreshToken: String
)