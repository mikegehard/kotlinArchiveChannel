package io.github.mikegehard.slack

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.*

fun archiveEmptyChannels(host: SlackHost) {
    getActiveChannels(host).filter { it.empty }.forEach {
        archive(host, it)
    }
}

private fun archive(host: SlackHost, channel: SlackChannel) {
    val client = OkHttpClient();

    val factory = JsonNodeFactory(false);
    val bodyContents = factory.objectNode();
    bodyContents.put("channel", channel.id)
    bodyContents.put("token", host.token)

    val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyContents.toString());

    val url = apiUrlFor(host).addPathSegment("channels.archive")

    val request = Request.Builder()
            .url(url.build())
            .post(body)

    client.execute(request.build())
}

private fun getActiveChannels(host: SlackHost): List<SlackChannel> {
    val client = OkHttpClient();

    val url = apiUrlFor(host).apply {
        addPathSegment("channels.list")
        addQueryParameter("exclude_archived", "1")
        addQueryParameter("token", host.token)
    }

    val request = Request.Builder()
            .url(url.build())

    return channelsFrom(client.execute(request.build()))
}

private fun apiUrlFor(host: SlackHost): HttpUrl.Builder = HttpUrl.Builder()
        .apply {
            scheme("http")
            port(host.port)
            host(host.server)
            addPathSegment("api")
        }

private fun channelsFrom(response: Response): List<SlackChannel> {
    val mapper = ObjectMapper().registerKotlinModule()
    val content = response.body().string()
    return mapper.readValue<SlackGetChannelResponse>(content).channels
}

private fun OkHttpClient.execute(request: Request): Response = newCall(request).execute()