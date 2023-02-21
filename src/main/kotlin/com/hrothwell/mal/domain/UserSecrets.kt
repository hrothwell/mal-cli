package com.hrothwell.mal.domain

import com.hrothwell.mal.domain.response.MalOAuthResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSecrets(
  @SerialName("user_name")
  val userName: String,
  @SerialName("client_id")
  val clientId: String,
  @SerialName("oauth_authorization_code")
  val oauthAuthorizationCode: String? = null,
  @SerialName("oauth_tokens")
  val oauthTokens: MalOAuthResponse? = null
)