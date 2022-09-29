package com.hrothwell.anime.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Node(
  val id: Long,
  val title: String
)