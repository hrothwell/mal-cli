package com.hrothwell.anime.exception

data class MALResponseException(
  val msg: String
) : Throwable(msg)