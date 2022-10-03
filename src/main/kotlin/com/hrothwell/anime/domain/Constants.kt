package com.hrothwell.anime.domain

import com.github.kittinunf.fuel.core.Response
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

// file locations
val home = System.getProperty("user.home")
val secretLocation = "$home/anime-cli/mal-secret.json"

val jsonReader = Json{ignoreUnknownKeys = true}

// Colors
val RED = "\u001b[31m"

// TODO printing this way (rather than using the echo) only does the first line red
fun getUserSecrets(location: String = secretLocation): UserSecrets {
  return try {
    val clientSecretsJsonContent = File(location).readText()
    jsonReader.decodeFromString(clientSecretsJsonContent)
  } catch (t: Throwable) {
    println("""
    $RED    You need to place your MAL client id in this file: $secretLocation. File contents should look like:
    $RED    {
    $RED      "user_name": "my user name",
    $RED      "client_id": "my MAL API client ID"
    $RED    } 
      """.trimIndent())
    throw t
  }
}

fun quickErrorHelp(){
  println("""
  $RED    Quick help / common situations:
  $RED      - 400/403: no client_id set in mal-secret.json or said client_id is invalid, username/password invalid, etc. See https://myanimelist.net/apiconfig
  $RED      - 404: user was not found. Was the user you passed in a real user? If no user passed in, is your mal-secret.json setup correctly?
    """.trimIndent())
}

fun handlePotentialHttpErrors(response: Response) {
  if(response.statusCode != 200){
    println("$RED ${response.statusCode} error returned from MAL: ${response.responseMessage}")
    quickErrorHelp()
    exitProcess(1)
  }
}