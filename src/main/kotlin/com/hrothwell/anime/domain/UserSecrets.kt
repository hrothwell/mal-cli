package com.hrothwell.anime.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserSecrets(
  val user_name: String,
  val client_id: String
)