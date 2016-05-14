// Notice no package declaration
// Experimenting with only exposing a small public interface

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import io.damo.kspec.Spec
import io.github.mikegehard.slack.SlackHost
import io.github.mikegehard.slack.archiveEmptyChannels
import org.mockserver.client.server.MockServerClient
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.Parameter
import org.mockserver.socket.PortFactory
import org.mockserver.verify.VerificationTimes

class ArchiveSpec : Spec({
    describe("archiving empty channels") {
        test {
            val server = "localhost"
            val port = PortFactory.findFreePort()
            val mockServer = startClientAndServer(port)
            val client = MockServerClient(server, port)
            val slackToken = "some-long-token"

            val getChannelsRequest = HttpRequest().apply {
                withMethod("GET")
                withPath("/api/channels.list")
                withQueryStringParameters(Parameter("token", slackToken))
                withQueryStringParameters(Parameter("exclude_archived", "1"))
            }

            val channelWithMembersId = "withMembersId"
            val channelWithoutMembersId = "withoutMembersId"

            val getChannelResponse = HttpResponse().apply {
                withBody(getChannelJsonFor(channelWithMembersId, channelWithoutMembersId))
            }

            client.`when`(getChannelsRequest).respond(getChannelResponse)

            archiveEmptyChannels(SlackHost(server, port, slackToken))

            client.verify(archiveRequestFor(channelWithoutMembersId, slackToken))
            client.verify(archiveRequestFor(channelWithMembersId, slackToken), VerificationTimes.exactly(0))

            mockServer.stop()
        }
    }
})

private fun archiveRequestFor(channelId: String, token: String): HttpRequest {
    val factory = JsonNodeFactory(false);
    val body = factory.objectNode();
    body.put("channel", channelId)
    body.put("token", token)

    return HttpRequest().apply {
        withMethod("POST")
        withPath("/api/channels.archive")
        withHeader("Content-Type", "application/json; charset=utf-8")
        withBody(body.toString())
    }
}

private fun getChannelJsonFor(channelWithMembersId: String, channelWithoutMembersId: String): String {
    return """
{
    "ok": true,
    "channels": [
        {
            "id": "$channelWithMembersId",
            "num_members": 6
        },
        {
            "id": "$channelWithoutMembersId",
            "num_members": 0
        }
    ]
}
            """
}
