package com.hrothwell.anime.exception

data class LoginException(
  val msg: String,
  val rootCause: Throwable? = null
): Throwable(msg)