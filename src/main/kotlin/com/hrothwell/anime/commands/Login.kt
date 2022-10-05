package com.hrothwell.anime.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.domain.MALOAuthResponse
import com.hrothwell.anime.util.AnimeUtil
import com.hrothwell.anime.util.FileUtil
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.decodeFromString
import java.net.InetSocketAddress
import kotlin.random.Random
import kotlin.system.exitProcess

class Login: CliktCommand(
  help = """
    Login/authorize this app to have more access to MAL API
  """.trimIndent()
) {

  private val redirectUrl = "http://localhost:8080/anime"
  override fun run() {
    login()
  }

  private fun login(){
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('-', '.', '_', '~')
    val pkceCodeVerifier = (1..100)
      .map{ Random.nextInt(0, charPool.size)}
      .map(charPool::get)
      .joinToString("")

    val localServer = HttpServer.create()
    localServer.createContext("/anime", MALOAuthHttpHandler())
    localServer.bind(InetSocketAddress(8080), 0)
    localServer.start()

    val userSecrets = FileUtil.getUserSecrets()
    val responseType = "response_type=code"
    val clientId = "client_id=${userSecrets.client_id}"
    val codeChallenge = "code_challenge=$pkceCodeVerifier"
    val redirectUri = "redirect_uri=$redirectUrl"
    val grantType = "grant_type=authorization_code"

    val userAuthUrl = "https://myanimelist.net/v1/oauth2/authorize?$grantType&$responseType&$clientId&$redirectUri&$codeChallenge"
    echo("Open this URL to authenticate with MAL: $userAuthUrl")

    // user goes to MAL via link, authorizes, hits MyHttpHandler which updates secrets file

    if(confirm("Did you approve the application?", default = false, showDefault = false) == true){
      localServer.stop(0)
      exchangeAuthCode(pkceCodeVerifier)
    } else{
      echoError("Login process terminated by user")
      exitProcess(1)
    }
  }

  /**
   * Exchange oauth_authorization_code and code_verifier for Oauth token
   * After getting token information, update secrets
   */
  private fun exchangeAuthCode(pkceCodeVerifier: String){

    val secrets = FileUtil.getUserSecrets()

    val redirect = "redirect_uri" to redirectUrl
    val clientId = "client_id" to secrets.client_id
    val grantType = "grant_type" to "authorization_code"
    val code = "code" to secrets.oauth_authorization_code
    val codeVerifier = "code_verifier" to pkceCodeVerifier
    val params = listOf(clientId, grantType, codeVerifier, code, redirect)

    val request = Fuel.post("https://myanimelist.net/v1/oauth2/token", params)
    val tokenResult = request.response()
    AnimeUtil.handlePotentialHttpErrors(tokenResult.second)
    val tokenJson = String(tokenResult.third.get())
    val tokenResponse = FileUtil.jsonReader.decodeFromString<MALOAuthResponse>(tokenJson)
    val updatedSecrets = secrets.copy(oauth_tokens = tokenResponse)
    FileUtil.updateUserSecrets(updatedSecrets)
  }

  private fun echoError(msg: String) {
    echo("${AnimeUtil.RED} $msg", err = true)
  }
  private class MALOAuthHttpHandler: HttpHandler {
    override fun handle(exchange: HttpExchange?) {
      try{
        val uri = exchange?.requestURI!!
        val params = queryStringToMap(uri.query)
        val oauthAuthorizationCode = params["code"]!!
        val userSecrets = FileUtil.getUserSecrets()
        val updatedSecrets = userSecrets.copy(oauth_authorization_code = oauthAuthorizationCode)
        FileUtil.updateUserSecrets(updatedSecrets)
        exchange.sendResponseHeaders(200, 0)
        val outStream = exchange.responseBody
        outStream?.write("return to CLI".toByteArray())
        exchange.close()
      } catch(t: Throwable){
        AnimeUtil.printError("Error handling http request")
        t.printStackTrace()
        exitProcess(1)
      }
    }

    private fun queryStringToMap(queryString: String): Map<String, String>{
      val params = queryString.split("&")
      val queryMap = mutableMapOf<String, String>()
      params.forEach{
        val keyValue = it.split("=")
        if(keyValue.size > 1){
          queryMap[keyValue[0]] = keyValue[1]
        } else{
          queryMap[keyValue[0]] = ""
        }
      }
      return queryMap
    }
  }
}