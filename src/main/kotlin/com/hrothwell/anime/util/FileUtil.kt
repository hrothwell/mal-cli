package com.hrothwell.anime.util

import com.hrothwell.anime.domain.UserSecrets
import com.hrothwell.anime.exception.UserSecretsException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class FileUtil {
  companion object {
    val home = System.getProperty("user.home")
    private val secretLocation = "$home/mal-cli/mal-secret.json"
    val jsonReader = Json { ignoreUnknownKeys = true }

    fun getUserSecrets(): UserSecrets {
      AnimeUtil.printDebug("getUserSecrets - enter")
      return try {
        AnimeUtil.printDebug("getUserSecrets - read file from $secretLocation")
        val clientSecretsJsonContent = File(secretLocation).readText()
        AnimeUtil.printDebug("getUserSecrets - decode secret")
        jsonReader.decodeFromString<UserSecrets>(clientSecretsJsonContent)
      } catch (t: Throwable) {
        throw UserSecretsException(
          """
          ERROR trying to get secrets from companion object function
          
          Error getting mal-secret.json
          File not found/invalid at: $secretLocation
        {
          "user_name": "my user name",
          "client_id": "my MAL API client ID"
        }
          
        """.trimIndent(), t
        )
      }
    }

    /**
     * TODO this and other file methods could use testing probably. Testing in general would be nice to have
     */
    fun updateUserSecrets(userSecrets: UserSecrets) {
      AnimeUtil.printDebug("updateUserSecrets - enter")
      try {
        AnimeUtil.printDebug("updateUserSecrets - encoding new user secrets")
        val newUserSecretsJson = jsonReader.encodeToString(userSecrets)
        AnimeUtil.printDebug("updateUserSecrets - writing new secrets to file")
        File(secretLocation).writeText(newUserSecretsJson)
      } catch (t: Throwable) {
        throw UserSecretsException(
          """
          Error updating secrets
        """.trimIndent(), t
        )
      }
    }
  }
}