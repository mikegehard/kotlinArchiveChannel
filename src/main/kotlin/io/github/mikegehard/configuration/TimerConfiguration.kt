package io.github.mikegehard.configuration

import io.github.mikegehard.slack.SlackHost
import io.github.mikegehard.slack.archiveChannels
import io.github.mikegehard.slack.archiveStaleChannels
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
open class TimerConfiguration {
    companion object {
        val logger = LoggerFactory.getLogger(TimerConfiguration::class.java)
    }

    @Value("\${slack.server:slack.com}")
    lateinit var server: String

    @Value("\${slack.token}")
    lateinit var token: String

    @Value("\${slack.minimum.number.of.members:1}")
    lateinit var minimumNumberOfMembers: String

    @Value("\${slack.daysSinceLastMessage:30}")
    lateinit var daysSinceLastMessage: String

    @Value("\${slack.archive.message:#{null}}")
    var archiveMessage: String? = null

    @Scheduled(cron = "\${slack.archive.empty.channel.schedule}")
    fun runArchiveEmptyChannels() {
        logger.info("********** Archiving empty channels! ************")
        archiveChannels(
                SlackHost(server, true, token),
                minimumNumberOfMembers.toInt(),
                archiveMessage
        )
    }

    @Scheduled(cron = "\${slack.archive.empty.channel.schedule}")
    fun runArchiveStaleChannels() {
        logger.info("********** Archiving stale channels! ************")
        archiveStaleChannels(
                SlackHost(server, true, token),
                daysSinceLastMessage.toLong(),
                archiveMessage
        )
    }
}
