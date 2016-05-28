package io.github.mikegehard.slack

import io.damo.kspec.Spec
import org.assertj.core.api.Assertions.assertThat


class SlackHostSpec : Spec({
    test("apiUrl") {
        describe("for an insecure host") {
            assertThat(SlackHost("host.com", false, "slack-token").apiUrl.toString()).isEqualTo("http://host.com/api?token=slack-token")
            assertThat(SlackHost("host.com:123", false, "slack-token").apiUrl.toString()).isEqualTo("http://host.com:123/api?token=slack-token")
            assertThat(SlackHost("host.com:abc", false, "slack-token").apiUrl.toString()).isEqualTo("http://host.com/api?token=slack-token")
        }
        describe("for a secure host") {
            assertThat(SlackHost("host.com", true, "slack-token").apiUrl.toString()).isEqualTo("https://host.com/api?token=slack-token")
            assertThat(SlackHost("host.com:123", true, "slack-token").apiUrl.toString()).isEqualTo("https://host.com:123/api?token=slack-token")
            assertThat(SlackHost("host.com:abc", true, "slack-token").apiUrl.toString()).isEqualTo("https://host.com/api?token=slack-token")
        }
    }
})