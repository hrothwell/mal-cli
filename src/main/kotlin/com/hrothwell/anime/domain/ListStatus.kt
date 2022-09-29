package com.hrothwell.anime.domain

enum class ListStatus(val malValue: String) {
  COMPLETED("completed"),
  PLAN_TO_WATCH("plan_to_watch"),
  WATCHING("watching"),
  ON_HOLD("on_hold"),
  DROPPED("dropped")
}