package com.hrothwell.mal.exception

data class MALResponseException(
  val msg: String
) : Throwable(msg)