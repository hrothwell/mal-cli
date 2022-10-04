package com.hrothwell.anime.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserSecrets(
  val user_name: String,
  val client_id: String,
//  val oauth_authorization_code: String?,
//  val oauth_token: String? // TODO fine to keep this here?
)