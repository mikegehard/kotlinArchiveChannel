package io.github.mikegehard.slack

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.Instant

class SlackMessageDeserializer : StdDeserializer<SlackMessage>(SlackMessage::class.java) {
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): SlackMessage {
        val node: JsonNode = jp.codec.readTree(jp)
        val parts = node.get("ts").textValue().split(".")
        return if (parts.size == 2) {
            SlackMessage(Instant.ofEpochSecond(parts[0].toLong(), parts[1].toLong() * 10000))
        } else {
            SlackMessage(Instant.ofEpochSecond(parts[0].toLong()))
        }
    }
}
