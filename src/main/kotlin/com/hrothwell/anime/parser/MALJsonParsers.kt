package com.hrothwell.anime.parser
//
//import com.google.gson.JsonDeserializationContext
//import com.google.gson.JsonDeserializer
//import com.google.gson.JsonElement
//import com.google.gson.JsonObject
//import com.hrothwell.anime.domain.Data
//import com.hrothwell.anime.domain.Node
//import java.lang.reflect.Type
//
//class DataParser : JsonDeserializer<Data> {
//  override fun deserialize(element: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Data {
//    val json = element as JsonObject
//    val node = json.get("node")
//
//    return Data(node = node.toResponse())
//  }
//}
//
//class NodeParser : JsonDeserializer<Node> {
//  override fun deserialize(element: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Node {
//    val json = element as JsonObject
//    val title = json.get("title").asString
//    val id = json.get("it").asLong
//
//    return Node(id = id, title = title)
//  }
//}