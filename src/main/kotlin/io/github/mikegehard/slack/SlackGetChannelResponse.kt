package io.github.mikegehard.slack

internal data class SlackGetChannelResponse(val ok: Boolean, val channels: List<SlackChannel>)