package io.github.mikegehard.slack

import io.damo.kspec.Spec
import org.assertj.core.api.Assertions.assertThat


class SlackHostSpec: Spec({
    test("port") {
        assertThat(SlackHost("abc123", false, "token").port).isEqualTo(80)
        assertThat(SlackHost("abc123:123", false, "token").port).isEqualTo(123)
        assertThat(SlackHost("abc123:abc", false, "token").port).isEqualTo(80)
        assertThat(SlackHost("abc123", true, "token").port).isEqualTo(443)
        assertThat(SlackHost("abc123:123", true, "token").port).isEqualTo(123)
        assertThat(SlackHost("abc123:abc", true, "token").port).isEqualTo(443)
    }
})