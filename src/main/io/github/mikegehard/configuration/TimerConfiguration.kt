package io.github.mikegehard.configuration

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
    fun doSomething() {
        logger.info("Doing something every 5 seconds!")
    }
}
