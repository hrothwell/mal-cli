package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.domain.mal.MalOAuthResponse
import com.hrothwell.anime.exception.LoginException
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.AnimeUtil.Companion.openUrl
import com.hrothwell.anime.util.FileUtil
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.decodeFromString
import java.net.InetSocketAddress
import kotlin.random.Random
import kotlin.system.exitProcess

class Login : CliktCommand(
  help = """
    Login/authorize this app to have more access to MAL API
  """.trimIndent()
) {

  private val redirectUrl = "http://localhost:8080/anime"
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
    AnimeUtil.printDebug("login - enter")
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('-', '.', '_', '~')
    val pkceCodeVerifier = (1..100)
      .map { Random.nextInt(0, charPool.size) }
      .map(charPool::get)
      .joinToString("")

    AnimeUtil.printDebug("login - starting local server")
    val localServer = HttpServer.create()
    localServer.createContext("/anime", MALOAuthHttpHandler())
    localServer.bind(InetSocketAddress(8080), 0)
    localServer.start()

    AnimeUtil.printDebug("login - building user auth url")
    val userSecrets = FileUtil.getUserSecrets()
    val responseType = "response_type=code"
    val clientId = "client_id=${userSecrets.clientId}"
    val codeChallenge = "code_challenge=$pkceCodeVerifier"
    val redirectUri = "redirect_uri=$redirectUrl"
    val grantType = "grant_type=authorization_code"

    val userAuthUrl =
      "https://myanimelist.net/v1/oauth2/authorize?$grantType&$responseType&$clientId&$redirectUri&$codeChallenge"
    echo("If it does not open automatically, use this URL to authenticate with MAL: $userAuthUrl")

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
    AnimeUtil.printDebug("exchangeAuthCode - enter")
    val secrets = FileUtil.getUserSecrets()

    AnimeUtil.printDebug("exchangeAuthCode - building request")
    val redirect = "redirect_uri" to redirectUrl
    val clientId = "client_id" to secrets.clientId
    val grantType = "grant_type" to "authorization_code"
    val code = "code" to secrets.oauthAuthorizationCode
    val codeVerifier = "code_verifier" to pkceCodeVerifier
    val params = listOf(clientId, grantType, codeVerifier, code, redirect)

    val request = Fuel.post("https://myanimelist.net/v1/oauth2/token", params)

    AnimeUtil.printDebug("exchangeAuthCode - get response")
    val tokenResult = request.response()
    AnimeUtil.handlePotentialHttpErrors(tokenResult.second)

    AnimeUtil.printDebug("exchangeAuthCode - deconde response")
    val tokenJson = String(tokenResult.third.get())
    val tokenResponse = FileUtil.jsonReader.decodeFromString<MalOAuthResponse>(tokenJson)
    val updatedSecrets = secrets.copy(oauthTokens = tokenResponse)

    FileUtil.updateUserSecrets(updatedSecrets)
  }

  private fun echoError(msg: String) {
    echo("${AnimeUtil.RED} $msg ${AnimeUtil.RESET}", err = true)
  }

  private class MALOAuthHttpHandler : HttpHandler {
    override fun handle(exchange: HttpExchange?) {
      try {
        AnimeUtil.printDebug("MALOAuthHttpHandler.handle - enter")

        val uri = exchange?.requestURI!!
        val params = queryStringToMap(uri.query)
        val oauthAuthorizationCode = params["code"]!!
        val userSecrets = FileUtil.getUserSecrets()
        val updatedSecrets = userSecrets.copy(oauthAuthorizationCode = oauthAuthorizationCode)
        FileUtil.updateUserSecrets(updatedSecrets)
        exchange.sendResponseHeaders(200, 0)
        val outStream = exchange.responseBody
        outStream?.write("return to CLI".toByteArray())

        AnimeUtil.printDebug("MALOAuthHttpHandler.handle - exit")
        exchange.close()
      } catch (t: Throwable) {
        throw LoginException("Error handling http request response from MAL during login process", t)
      }
    }

    private fun queryStringToMap(queryString: String): Map<String, String> {
      AnimeUtil.printDebug("MALOAuthHttpHandler.queryStringToMap - enter")
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
      AnimeUtil.printDebug("MALOAuthHttpHandler.queryStringToMap - exit")
      return queryMap
    }
  }
}