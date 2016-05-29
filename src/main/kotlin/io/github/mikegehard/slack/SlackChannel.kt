package io.github.mikegehard.slack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class SlackChannel(
        val id: String,
        @JsonProperty("num_members") val members: Int
) {
    fun hasLessThan(members: Int): Boolean {
        return this.members < members
    }

}