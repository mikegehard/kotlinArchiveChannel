package io.github.mikegehard.slack

import io.github.mikegehard.slack.timeExtensions.daysAgo
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

fun archiveChannels(host: SlackHost, minimumMembers: Int, archiveMessage: String?) =
        getActiveChannels(host)
                .filter { it.hasLessThan(minimumMembers) }
                .forEach { archive(host, it, archiveMessage) }

fun archiveStaleChannels(host: SlackHost, daysSinceLastMessage: Long, archiveMessage: String?) =
        getActiveChannels(host)
                .withAdditionalInformation(host)
                .filter { it.staleAsOf(daysSinceLastMessage.daysAgo()) }
                .forEach { archive(host, it, archiveMessage) }

private fun archive(host: SlackHost, channel: SlackChannel, message: String?) {
    message?.let { postChat(host, channel, it) }
    archive(host, channel)
}

private fun List<SlackChannel>.withAdditionalInformation(host: SlackHost): List<SlackChannel> =
        this.map { getChannelInfo(host, it) }

private fun getChannelInfo(host: SlackHost, channel: SlackChannel): SlackChannel {
    val url = host.url {
        addQueryParameter("channel", channel.id)
        addPathSegment("channels.info")
    }

    val request = Request.Builder().url(url).build()

    return channelFrom(execute(request).body().string())
}

private fun postChat(host: SlackHost, channel: SlackChannel, message: String) {
    val url = host.url {
        addQueryParameter("channel", channel.id)
        addQueryParameter("text", message)
        addPathSegment("chat.postMessage")
    }

    val request = Request.Builder().url(url).build()

    // need to log some sort of message if this fails so I can see why??
    execute(request)
}

private fun archive(host: SlackHost, channel: SlackChannel) {
    val url = host.url {
        addQueryParameter("channel", channel.id)
        addPathSegment("channels.archive")
    }

    val request = Request.Builder().url(url).build()

    // need to log some sort of message if this fails so I can see why??
    execute(request)
}

private fun getActiveChannels(host: SlackHost): List<SlackChannel> {
    val url = host.url {
        addPathSegment("channels.list")
        addQueryParameter("exclude_archived", "1")
    }

    val request = Request.Builder().url(url).build()

    // what happens if this fails??

    return channelsFrom(execute(request).body().string())
}

private fun execute(request: Request): Response = OkHttpClient().newCall(request).execute()
