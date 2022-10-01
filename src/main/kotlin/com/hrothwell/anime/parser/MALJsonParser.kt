package com.hrothwell.anime.parser

class MALJsonParser{

  // To avoid reflection, going to do this in a super duper messed up way...
  private val regexString = """
    "title":(\s?)"[A-Za-z\d-_\.\s]+"
    """.trim().trimIndent()
  private val regex = Regex(regexString)

  fun getTitles(malJsonString: String): List<String>{
    val matchResults = regex.findAll(malJsonString)
    val titleEntries = matchResults.map{
      // who needs object mappers lul
      it.value.replace("\"", "").split(":")[1]
    }.toList()
    return titleEntries
  }
}