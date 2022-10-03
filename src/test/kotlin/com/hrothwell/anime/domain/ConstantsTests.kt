package com.hrothwell.anime.domain

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertFails

class ConstantsTests {

  @Test
  fun `get user secrets error message`(){
    assertFails{
      getUserSecrets(location = "this location does not exist")
    }
  }

  @Test
  fun `quick error help`(){
    assertDoesNotThrow { quickErrorHelp() }
  }
}