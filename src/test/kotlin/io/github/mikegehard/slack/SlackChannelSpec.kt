package io.github.mikegehard.slack

import io.damo.kspec.Spec
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class SlackChannelSpec: Spec({
    test("empty") {
        assertTrue(SlackChannel("abc123", 0).empty)

        assertFalse(SlackChannel("abc123", 1).empty)
    }
})