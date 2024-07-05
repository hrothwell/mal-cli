package com.hrothwell.mal.client

import com.hrothwell.mal.util.MalUtil
import io.ktor.client.plugins.logging.Logger

class ClientLogger : Logger {
  override fun log(message: String) {
    MalUtil.printDebug("ClientLogger: $message")
  }
}
