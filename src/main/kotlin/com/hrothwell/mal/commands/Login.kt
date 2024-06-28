package com.hrothwell.mal.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.hrothwell.mal.client.MalClient
import com.hrothwell.mal.domain.response.MalOAuthResponse
import com.hrothwell.mal.exception.LoginException
import com.hrothwell.mal.util.FileUtil
import com.hrothwell.mal.util.MalUtil
import com.hrothwell.mal.util.MalUtil.Companion.openUrl
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import kotlin.collections.set
import kotlin.random.Random
import kotlin.system.exitProcess

class Login : CliktCommand(
  help = """
    Login/authorize this app to have more access to MAL API
  """.trimIndent()
) {

  private val PORT = 8080

  // this differs from the port, this is what is listed in the MAL api config
  private val redirectUrl = "http://localhost:$PORT/mal"
  override fun run() {
    try {
      login()
    } catch (t: Throwable) {
      echoError(
        """
        Error logging in. Message: ${t.message}
        Cause: ${t.cause}
      """.trimIndent()
      )
      exitProcess(1)
    }
  }

  private fun login() {
    MalUtil.printDebug("login - enter")
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('-', '.', '_', '~')
    val pkceCodeVerifier = (1..100)
      .map { Random.nextInt(0, charPool.size) }
      .map(charPool::get)
      .joinToString("")

    MalUtil.printDebug("login - starting local server")
    val localServer = HttpServer.create()
    localServer.createContext("/mal", MALOAuthHttpHandler())
    localServer.bind(InetSocketAddress(PORT), 0)
    localServer.start()

    MalUtil.printDebug("login - building user auth url")
    val userSecrets = FileUtil.getUserSecrets()
    val responseType = "response_type=code"
    val clientId = "client_id=${userSecrets.clientId}"
    val codeChallenge = "code_challenge=$pkceCodeVerifier"
    val redirectUri = "redirect_uri=$redirectUrl"
    val grantType = "grant_type=authorization_code"

    val userAuthUrl =
      "https://myanimelist.net/v1/oauth2/authorize?$grantType&$responseType&$clientId&$redirectUri&$codeChallenge"

    openUrl(userAuthUrl)

    // user goes to MAL via link, authorizes, hits MALOAuthHttpHandler which updates secrets file

    if (confirm("Did you approve the application?", default = false, showDefault = true) == true) {
      localServer.stop(0)
      exchangeAuthCode(pkceCodeVerifier)
    } else {
      throw LoginException("Login process terminated by user")
    }
  }

  /**
   * Exchange oauth_authorization_code and code_verifier for Oauth token
   * After getting token information, update secrets
   */
  private fun exchangeAuthCode(pkceCodeVerifier: String) {
    MalUtil.printDebug("exchangeAuthCode - enter")
    val secrets = FileUtil.getUserSecrets()

    MalUtil.printDebug("exchangeAuthCode - get response")

    val response = runBlocking {
      MalClient.ktor.post("https://myanimelist.net/v1/oauth2/token") {
        setBody(
          FormDataContent(
            Parameters.build {
              append("client_id", secrets.clientId)
              append("grant_type", "authorization_code")
              append("code", secrets.oauthAuthorizationCode!!)
              append("redirect_uri", redirectUrl)
              append("code_verifier", pkceCodeVerifier)
            }
          )
        )
      }
    }

    MalUtil.printDebug("exchangeAuthCode - deconde response")
    val result = runBlocking {
      if (response.status.isSuccess()) {
        response.body<MalOAuthResponse>()
      } else {
        echoError("Could not read response")
        echoError(response.bodyAsText())

        throw Exception()
      }
    }
    val updatedSecrets = secrets.copy(oauthTokens = result)

    FileUtil.updateUserSecrets(updatedSecrets)
  }

  private fun echoError(msg: String) {
    echo("${MalUtil.RED} $msg ${MalUtil.RESET}", err = true)
  }

  private class MALOAuthHttpHandler : HttpHandler {
    override fun handle(exchange: HttpExchange?) {
      try {
        MalUtil.printDebug("MALOAuthHttpHandler.handle - enter")

        val uri = exchange?.requestURI!!
        val params = queryStringToMap(uri.query)
        val oauthAuthorizationCode = params["code"]!!
        val userSecrets = FileUtil.getUserSecrets()
        val updatedSecrets = userSecrets.copy(oauthAuthorizationCode = oauthAuthorizationCode)
        FileUtil.updateUserSecrets(updatedSecrets)
        exchange.sendResponseHeaders(200, 0)
        val outStream = exchange.responseBody
        outStream?.write("return to CLI".toByteArray())
        MalUtil.printDebug("MALOAuthHttpHandler.handle - exit")
      } catch (t: Throwable) {
        exchange?.sendResponseHeaders(404, 0)
        exchange?.responseBody?.write("something didn't work right".toByteArray())
        throw LoginException("Error handling http request response from MAL during login process", t)
      } finally {
        exchange?.close()
      }
    }

    private fun queryStringToMap(queryString: String): Map<String, String> {
      MalUtil.printDebug("MALOAuthHttpHandler.queryStringToMap - enter")
      val params = queryString.split("&")
      val queryMap = mutableMapOf<String, String>()
      params.forEach {
        val keyValue = it.split("=")
        if (keyValue.size > 1) {
          queryMap[keyValue[0]] = keyValue[1]
        } else {
          queryMap[keyValue[0]] = ""
        }
      }
      MalUtil.printDebug("MALOAuthHttpHandler.queryStringToMap - exit")
      return queryMap
    }
  }
}
