package io.github.mikegehard.slack

import io.damo.kspec.Spec
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class SlackChannelSpec: Spec({
    test("hasAtLeast") {
        assertTrue(SlackChannel("abc123", 0).hasLessThan(1))

        assertFalse(SlackChannel("abc123", 1).hasLessThan(1))
    }
})