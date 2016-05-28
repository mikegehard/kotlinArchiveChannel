package io.github.mikegehard.slack

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

fun archiveEmptyChannels(host: SlackHost) {
    getActiveChannels(host).filter { it.empty }.forEach {
        archive(host, it)
    }
}

private fun archive(host: SlackHost, channel: SlackChannel) {
    val url = host.apiUrl.newBuilder().apply {
        addQueryParameter("channel", channel.id)
        addPathSegment("channels.archive")
    }.build()

    val request = Request.Builder().url(url).build()

    // need to log some sort of message if this fails so I can see why??
    execute(request)
}

private fun getActiveChannels(host: SlackHost): List<SlackChannel> {
    fun channelsFrom(response: Response): List<SlackChannel> {
        val mapper = ObjectMapper().registerKotlinModule()
        val content = response.body().string()
        return mapper.readValue<SlackGetChannelResponse>(content).channels
    }

    val url = host.apiUrl.newBuilder().apply {
        addPathSegment("channels.list")
        addQueryParameter("exclude_archived", "1")
    }.build()

    val request = Request.Builder().url(url).build()

    // what happens if this fails??

    return channelsFrom(execute(request))
}

private fun execute(request: Request): Response = OkHttpClient().newCall(request).execute()
