package io.github.mikegehard.slack

import io.damo.kspec.Spec
import io.github.mikegehard.slack.timeExtensions.daysAgo
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class SlackChannelSpec : Spec({
    val oneDayAgo = 1L.daysAgo()
    val twoDaysAgo = 2L.daysAgo()

    test("hasAtLeast") {
        val now = Instant.now()

        assertTrue(SlackChannel("abc123", 0, SlackMessage(timestamp = now)).hasLessThan(1))

        assertFalse(SlackChannel("abc123", 1, SlackMessage(timestamp = now)).hasLessThan(1))
    }

    test("staleAsOf") {
        assertFalse(SlackChannel("abc123", 0, SlackMessage(timestamp = oneDayAgo)).staleAsOf(oneDayAgo))

        assertTrue(SlackChannel("abc123", 0, SlackMessage(timestamp = twoDaysAgo)).staleAsOf(oneDayAgo))

        assertFalse(SlackChannel(id = "abc123", members = 0).staleAsOf(oneDayAgo))
    }

    test("parseFromJson") {
        val jsonWithNanos = """
{
    "ok": true,
    "channel": {
        "id": "abc123",
        "latest": {
            "type": "message",
            "user": "U0ENFT3JT",
            "text": "travel would be no fun",
            "ts": "1472262509.12345"
        },
        "unread_count": 0
    }
}
"""

        val expectedMessageWithNanos = SlackMessage(timestamp = Instant.parse("2016-08-27T01:48:29.123450Z"))

        assertEquals(expectedMessageWithNanos, parseFromJson(jsonWithNanos).lastMessage)

        val jsonWithoutNanos = """
{
    "ok": true,
    "channel": {
        "id": "abc123",
        "latest": {
            "type": "message",
            "user": "U0ENFT3JT",
            "text": "travel would be no fun",
            "ts": "1472262509"
        },
        "unread_count": 0
    }
}
"""

        val expectedMessageWithoutNanos = SlackMessage(timestamp = Instant.parse("2016-08-27T01:48:29Z"))

        assertEquals(expectedMessageWithoutNanos, parseFromJson(jsonWithoutNanos).lastMessage)
    }
})
