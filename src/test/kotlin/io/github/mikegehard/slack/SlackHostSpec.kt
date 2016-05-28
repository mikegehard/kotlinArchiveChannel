package io.github.mikegehard.slack

import io.damo.kspec.Spec
import org.assertj.core.api.Assertions.assertThat


class SlackHostSpec : Spec({
    test("apiUrl") {
        describe("for an insecure host") {
            assertThat(SlackHost("host.com", false, "token").apiUrl.toString()).isEqualTo("http://host.com/api")
            assertThat(SlackHost("host.com:123", false, "token").apiUrl.toString()).isEqualTo("http://host.com:123/api")
            assertThat(SlackHost("host.com:abc", false, "token").apiUrl.toString()).isEqualTo("http://host.com/api")
        }
        describe("for a secure host") {
            assertThat(SlackHost("host.com", true, "token").apiUrl.toString()).isEqualTo("https://host.com/api")
            assertThat(SlackHost("host.com:123", true, "token").apiUrl.toString()).isEqualTo("https://host.com:123/api")
            assertThat(SlackHost("host.com:abc", true, "token").apiUrl.toString()).isEqualTo("https://host.com/api")
        }
    }
})