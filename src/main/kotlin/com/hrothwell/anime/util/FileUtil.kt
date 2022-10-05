package com.hrothwell.anime.util

import com.hrothwell.anime.domain.UserSecrets
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

class FileUtil {
  companion object{
    val home = System.getProperty("user.home")
    private val secretLocation = "$home/anime-cli/mal-secret.json"
    val jsonReader = Json{ignoreUnknownKeys = true}

    fun getUserSecrets(): UserSecrets {
      return try {
        val clientSecretsJsonContent = File(secretLocation).readText()
        jsonReader.decodeFromString<UserSecrets>(clientSecretsJsonContent)
      } catch (t: Throwable) {
        AnimeUtil.printError("ERROR trying to get secrets from companion object function")
        AnimeUtil.printError(
          """ Error getting mal-secret.json
        You need to place your MAL client id in this file: $secretLocation. File contents should look like:
        {
          "user_name": "my user name",
          "client_id": "my MAL API client ID"
        }
      """.trimIndent()
        )
        t.printStackTrace(System.err)
        exitProcess(1)
      }
    }

    /**
     * TODO this and other file methods could use testing probably. Testing in general would be nice to have
     */
    fun updateUserSecrets(userSecrets: UserSecrets){
      try{
        val newUserSecretsJson = jsonReader.encodeToString(userSecrets)
        File(secretLocation).writeText(newUserSecretsJson)
      } catch(t: Throwable){
        AnimeUtil.printError("Unable to update mal-secret.json")
        t.printStackTrace(System.err)
        exitProcess(1)
      }
    }
  }
}