// Notice no package declaration
// Experimenting with only exposing a small public interface

import io.damo.kspec.Spec
import io.github.mikegehard.slack.SlackHost
import io.github.mikegehard.slack.archiveChannels
import io.github.mikegehard.slack.archiveStaleChannels
import org.mockserver.client.server.MockServerClient
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.Parameter
import org.mockserver.socket.PortFactory
import org.mockserver.verify.VerificationTimes
import java.time.Duration
import java.time.Instant

class ArchiveSpec : Spec({
    describe("archiving empty channels") {
        test {
            val server = "localhost"
            val port = PortFactory.findFreePort()
            val mockServer = startClientAndServer(port)
            val client = MockServerClient(server, port)
            val slackToken = "some-long-token"
            val slackHost = SlackHost("$server:$port", false, slackToken)

            val channelWithMinimumNumberOfMembersId = "channelWithMinimumNumberOfMembersId"
            val channelWithoutMinimumNumberOfMembersId = "channelWithoutMinimumNumberOfMembersId"
            val archivingMessage = "Archiving Channel"

            client.`when`(getChannelsRequestFor(slackToken)).respond(getChannelsResponseFor(channelWithMinimumNumberOfMembersId, channelWithoutMinimumNumberOfMembersId))

            archiveChannels(slackHost, 1, archivingMessage)

            client.verify(archiveRequestFor(channelWithoutMinimumNumberOfMembersId, slackToken))
            client.verify(sendMessageTo(channelWithoutMinimumNumberOfMembersId, archivingMessage, slackToken))
            client.verify(archiveRequestFor(channelWithMinimumNumberOfMembersId, slackToken), VerificationTimes.exactly(0))
            client.verify(sendMessageTo(channelWithMinimumNumberOfMembersId, archivingMessage, slackToken), VerificationTimes.exactly(0))

            mockServer.stop()
        }
    }

    describe("archiving stale channels") {
        test {
            val server = "localhost"
            val port = PortFactory.findFreePort()
            val mockServer = startClientAndServer(port)
            val client = MockServerClient(server, port)
            val slackToken = "some-long-token"
            val slackHost = SlackHost("$server:$port", false, slackToken)

            val staleChannel = "staleChannel"
            val freshChannel = "freshChannel"
            val archivingMessage = "Archiving Channel"

            client.`when`(getChannelsRequestFor(slackToken)).respond(getChannelsResponseFor(staleChannel, freshChannel))

            client.`when`(getChannelInfoRequestFor(slackToken, freshChannel)).respond(getChannelInfoResponseFor(freshChannel, Instant.now().minus(Duration.ofDays(1))))

            client.`when`(getChannelInfoRequestFor(slackToken, staleChannel)).respond(getChannelInfoResponseFor(staleChannel, Instant.now().minus(Duration.ofDays(3))))

            archiveStaleChannels(slackHost, 2, archivingMessage)

            client.verify(archiveRequestFor(staleChannel, slackToken))
            client.verify(sendMessageTo(staleChannel, archivingMessage, slackToken))
            client.verify(archiveRequestFor(freshChannel, slackToken), VerificationTimes.exactly(0))
            client.verify(sendMessageTo(freshChannel, archivingMessage, slackToken), VerificationTimes.exactly(0))

            mockServer.stop()
        }
    }
})

private fun getChannelsRequestFor(token: String): HttpRequest = HttpRequest().apply {
    withMethod("GET")
    withPath("/api/channels.list")
    withQueryStringParameters(Parameter("token", token))
    withQueryStringParameters(Parameter("exclude_archived", "1"))
}

private fun getChannelInfoRequestFor(token: String, channelId: String): HttpRequest = HttpRequest().apply {
    withMethod("GET")
    withPath("/api/channels.info")
    withQueryStringParameters(Parameter("token", token))
    withQueryStringParameters(Parameter("channel", channelId))
}

private fun getChannelsResponseFor(channelWithMembersId: String, channelWithoutMembersId: String) = HttpResponse().apply {
    withBody(getChannelsJsonFor(channelWithMembersId, channelWithoutMembersId))
}

private fun getChannelInfoResponseFor(channelId: String, lastMessageDate: Instant) = HttpResponse().apply {
    withBody(getChannelJsonFor(channelId, lastMessageDate))
}

private fun archiveRequestFor(channelId: String, token: String): HttpRequest = HttpRequest().apply {
    withMethod("GET")
    withPath("/api/channels.archive")
    withQueryStringParameters(Parameter("token", token))
    withQueryStringParameters(Parameter("channel", channelId))
}

private fun sendMessageTo(channelId: String, text: String, token: String): HttpRequest = HttpRequest().apply {
    withMethod("GET")
    withPath("/api/chat.postMessage")
    withQueryStringParameters(Parameter("token", token))
    withQueryStringParameters(Parameter("channel", channelId))
    withQueryStringParameter(Parameter("text", text))
}

private fun getChannelsJsonFor(channelWithMembersId: String, channelWithoutMembersId: String): String {
    // make sure you have some extra fields so that you test the annotations
    // that ignore json attributes that aren't fields in the object

    return """
{
    "ok": true,
    "channels": [
        {
            "id": "$channelWithMembersId",
            "num_members": 6,
            "name": "With members"
        },
        {
            "id": "$channelWithoutMembersId",
            "num_members": 0,
            "name": "Without members"
        }
    ]
}
            """
}

private fun getChannelJsonFor(channelId: String, lastMessageDate: Instant): String {
    // make sure you have some extra fields so that you test the annotations
    // that ignore json attributes that aren't fields in the object

    // TODO: Extract test helper because this is duplicated in SlackChannelSpec.kt
    return """
{
    "ok": true,
    "channel": {
        "id": "$channelId",
        "latest": {
            "type": "message",
            "user": "U0ENFT3JT",
            "text": "travel would be no fun",
            "ts": "${lastMessageDate.toEpochMilli()/1000}"
        },
        "unread_count": 0
    }
}
            """
}
