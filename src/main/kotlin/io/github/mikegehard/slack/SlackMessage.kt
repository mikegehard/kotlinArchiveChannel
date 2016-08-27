package io.github.mikegehard.slack

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.Instant

// You need the custom deserializer because the Slack API returns
// this timestamp as a string value that contains a float.
// Example "ts": "123.12345"
@JsonDeserialize(using = SlackMessageDeserializer::class)
data class SlackMessage(
        val timestamp: Instant
)
