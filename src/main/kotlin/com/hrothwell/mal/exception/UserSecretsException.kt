package com.hrothwell.mal.exception

data class UserSecretsException(
  val msg: String,
  val rootCause: Throwable
) : Throwable(msg, rootCause)