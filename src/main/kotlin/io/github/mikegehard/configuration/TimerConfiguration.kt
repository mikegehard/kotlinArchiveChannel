package io.github.mikegehard.configuration

import io.github.mikegehard.slack.SlackHost
import io.github.mikegehard.slack.archiveEmptyChannels
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

    @Value("\${slack.server}")
    lateinit var server:String

    @Value("\${slack.token}")
    lateinit var token:String

    @Scheduled(cron = "\${slack.archive.empty.channel.schedule}")
    fun runArchiveEmptyChannels() {
        logger.info("********** Archiving empty channels! ************")
        archiveEmptyChannels(SlackHost(server, true, token))
    }
}
