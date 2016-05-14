package io.github.mikegehard.configuration

import io.github.mikegehard.slack.SlackHost
import io.github.mikegehard.slack.archiveEmptyChannels
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
open class TimerConfiguration {
    companion object {
        val logger = LoggerFactory.getLogger(TimerConfiguration::class.java)
    }

    @Scheduled(fixedDelay = 5000)
    fun runArchiveEmptyChannels() {
        val server = "slack.com"
        val port = 80
        val token = "some-slack-token"

        logger.info("********** Archiving empty channels! ************")
        archiveEmptyChannels(SlackHost(server, port, token))
    }
}
