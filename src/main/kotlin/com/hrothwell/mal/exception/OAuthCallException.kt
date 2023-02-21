package com.hrothwell.mal.exception

data class OAuthCallException(
  val msg: String,
  val rootCause: Throwable?
) : Throwable(msg, rootCause)