package io.github.mikegehard.slack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class SlackChannel(
        val id: String,
        @JsonProperty("num_members") val members: Int,
        @JsonProperty("latest") val lastMessage: SlackMessage? = null
) {
    fun hasLessThan(members: Int): Boolean =
            this.members < members

    fun staleAsOf(instant: Instant): Boolean =
            lastMessage?.timestamp?.isBefore(instant) ?: false
}

fun channelFrom(json: String): SlackChannel =
        kotlinObjectMapper()
                .apply { registerModule(JavaTimeModule()) }
                .readValue<SlackGetChannelInfoResponse>(json)
                .channel

fun channelsFrom(json: String): List<SlackChannel> =
        kotlinObjectMapper()
                .readValue<SlackGetChannelResponse>(json)
                .channels

private fun kotlinObjectMapper() = ObjectMapper().registerKotlinModule()
