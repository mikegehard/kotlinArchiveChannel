package io.github.mikegehard.slack.timeExtensions

import java.time.Duration
import java.time.Instant


fun Long.daysAgo(): Instant = Instant.now().minus(Duration.ofDays(this))
