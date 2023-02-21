package com.hrothwell.anime.domain.mal

import kotlinx.serialization.Serializable

@Serializable
enum class AiringStatus {
  finished_airing, currently_airing, not_yet_aired
}