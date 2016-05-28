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
    val client = OkHttpClient();
    val url = host.apiUrl.newBuilder().apply {
        addQueryParameter("channel", channel.id)
        addQueryParameter("token", host.token)
        addPathSegment("channels.archive")
    }

    val request = Request.Builder()
            .url(url.build())

    // need to log some sort of message if this fails so I can see why??
    client.execute(request.build())
}

private fun getActiveChannels(host: SlackHost): List<SlackChannel> {
    val client = OkHttpClient();

    val url = host.apiUrl.newBuilder().apply {
        addPathSegment("channels.list")
        addQueryParameter("exclude_archived", "1")
        addQueryParameter("token", host.token)
    }

    val request = Request.Builder()
            .url(url.build())

    // what happens if this fails??

    return channelsFrom(client.execute(request.build()))
}

private fun channelsFrom(response: Response): List<SlackChannel> {
    val mapper = ObjectMapper().registerKotlinModule()
    val content = response.body().string()
    return mapper.readValue<SlackGetChannelResponse>(content).channels
}

private fun OkHttpClient.execute(request: Request): Response = newCall(request).execute()