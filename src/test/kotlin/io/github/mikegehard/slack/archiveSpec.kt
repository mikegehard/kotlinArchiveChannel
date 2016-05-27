// Notice no package declaration
// Experimenting with only exposing a small public interface

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

            val channelWithMembersId = "withMembersId"
            val channelWithoutMembersId = "withoutMembersId"

            client.`when`(getChannelsRequestFor(slackToken)).respond(getChannelResponseFor(channelWithMembersId, channelWithoutMembersId))

            archiveEmptyChannels(SlackHost("$server:$port", false, slackToken))

            client.verify(archiveRequestFor(channelWithoutMembersId, slackToken))
            client.verify(archiveRequestFor(channelWithMembersId, slackToken), VerificationTimes.exactly(0))

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

private fun getChannelResponseFor(channelWithMembersId: String, channelWithoutMembersId: String) = HttpResponse().apply {
    withBody(getChannelJsonFor(channelWithMembersId, channelWithoutMembersId))
}

private fun archiveRequestFor(channelId: String, token: String): HttpRequest = HttpRequest().apply {
    withMethod("GET")
    withPath("/api/channels.archive")
    withQueryStringParameters(Parameter("token", token))
    withQueryStringParameters(Parameter("channel", channelId))
}

private fun getChannelJsonFor(channelWithMembersId: String, channelWithoutMembersId: String): String {
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
