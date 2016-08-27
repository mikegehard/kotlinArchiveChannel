package io.github.mikegehard.slack

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.Instant

// You need the custom deserializer because the Slack API returns
// this timestamp as a string value that contains a float.
// Example "ts": "123.12345"
@JsonDeserialize(using = SlackMessageDeserializer::class)
data class SlackMessage(
        val timestamp: Instant
)

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
