package com.hrothwell.anime.commands

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.kittinunf.fuel.Fuel
import com.hrothwell.anime.util.AnimeUtil
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.util.*
import kotlin.random.Random
import kotlin.system.exitProcess

// TODO because subcommands don't work, will need to move this in to Anime.kt or think of something else
class Login: CliktCommand(
  help = """
    Login/authorize this app to have more access to your MAL account
  """.trimIndent()
) {

  override fun run() {
    login()
  }

  /**
   * TODO OAuth Steps
   * - Generate a code verifier and challenge (one time only?)
   * - request oauth authentication (one time only?)
   *  - MAL authenticates user, the user authorizes the client
   * - MAL redirects client to the REDIRECT URI we provide...
   *    - TODO this is what worries me, we have a CLI app so idk how this will quite work...
   * - Store this authorization code and this is what is used for refresh and access tokens?
   * -
   */

  private fun login(){
    val userSecrets = AnimeUtil.getUserSecrets()
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('-', '.', '_', '~')
    val randomString = (1..100)
      .map{ Random.nextInt(0, charPool.size)}
      .map(charPool::get)
      .joinToString("")

    val myServerEndpoint = "http://localhost:8080/anime"

    // TODO start a local server, then use that as redirect url? is this needed?
    //  need HttpHandler and HttpContext
    val localServer = HttpServer.create()
    val httpContext = localServer.createContext("/anime", MyHttpHandler())
    localServer.bind(InetSocketAddress(8080), 0)
    localServer.start()

    // TODO If they already are "signed in" and we have an oauth token that is valid, probably don't need to do this
    val responseType = "response_type=code"
    val clientId = "client_id=${userSecrets.client_id}"
    val codeChallenge = "code_challenge=$randomString"
    val redirectUri = "redirect_uri=$myServerEndpoint"
    val grantType = "grant_type=authorization_code"

    val userAuthUrl = "https://myanimelist.net/v1/oauth2/authorize?$grantType&$responseType&$clientId&$redirectUri&$codeChallenge"
    echo("click here: $userAuthUrl")

    if(confirm("Was it successful?", default = false, showDefault = false) == true){
      // TODO continue oauth setup
      echo("thanks!")
    }

    localServer.stop(0)
  }

  // TODO Probably need to also return some html that just tells user to go back to CLI
  //  Looking at the example here: https://gitlab.com/-/snippets/2039434 it seems they don't do anything with a server and instead just get the response from MAL, so maybe don't need this
  private class MyHttpHandler: HttpHandler {
    override fun handle(exchange: HttpExchange?) {
      println("handling http request, uri: ${exchange?.requestURI}")
      val f = exchange?.requestURI
      // TODO this will store values we need for further calls, where to store them?
      val params = f?.query
      println("Here is query string: $params")
      exchange?.sendResponseHeaders(200, 0)
      val outStream = exchange?.responseBody
      // TODO parse and write oauth token to file
//      val userSecrets = AnimeUtil.getUserSecrets()
//      val updatedSecrets = userSecrets.copy(oauth_authorization_code = "oauth_authorization_code")
      outStream?.write(params?.toByteArray()!!)
      exchange?.close()
      println("Exiting exchange of http handler")
    }
  }
}