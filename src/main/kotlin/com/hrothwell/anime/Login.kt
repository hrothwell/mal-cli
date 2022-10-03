package com.hrothwell.anime

import com.github.ajalt.clikt.core.CliktCommand
import com.github.kittinunf.fuel.Fuel
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import kotlin.random.Random

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
//    val userSecrets = getUserSecrets()
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('-', '.', '_', '~')
    val randomString = (1..100)
      .map{ Random.nextInt(0, charPool.size)}
      .map(charPool::get)
      .joinToString("")

    val myServerEndpoint = "http://localhost:8080/anime"

    // TODO MAL specific code. If they already are "signed in" and we have an oauth token that is valid, probably don't need to do this
//    val responseType = "response_type" to "code"
//    val clientId = "client_id" to userSecrets.client_id
//    val codeChallenge = "code_challenge" to randomString
//    val redirectUri = "redirect_uri" to myServerEndpoint
//    val params = listOf(responseType, clientId, codeChallenge, redirectUri)
//    val authorizationResult = Fuel.get("https://myanimelist.net/v1/oauth2/authorize", parameters = params)

    // TODO start a local server, then use that as redirect url?
    //  need HttpHandler and HttpContext
    val localServer = HttpServer.create()
    val httpContext = localServer.createContext("/anime", MyHttpHandler())
    localServer.bind(InetSocketAddress(8080), 0)
    localServer.start()

    // TODO put auth call here. Probably need to be able to "wait" for them to open the URL from MAL and click "authorize", can my http server shut off after just one
    //  message is received? Start a prompt and wait for user input? "Did you authorize the application? y/n" and then continue
    val result = Fuel.get("$myServerEndpoint?hello=world").response().third.get()
    val resultString = String(result)
    println("resultString $resultString")

    localServer.stop(0)
  }

  // TODO Probably need to also return some html that just tells user to go back to CLI
  private class MyHttpHandler: HttpHandler {
    override fun handle(exchange: HttpExchange?) {
      println("handling http request")
      val f = exchange?.requestURI
      // TODO this will store values we need for further calls, where to store them?
      val params = f?.query
      println("Here is query string: $params")
      exchange?.sendResponseHeaders(200, 0)
      val outStream = exchange?.responseBody
      outStream?.write(params?.toByteArray()!!)
      exchange?.close()
    }
  }
}