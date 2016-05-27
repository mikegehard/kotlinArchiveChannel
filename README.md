# Kotlin/SpringBoot Archive Channels Slack Bot

## Funtionality

* Archive all empty channels on a cron schedule

## Configuration

The following environment variables are required:

* SLACK_TOKEN - you must use an API key for a regular Slack user account. You cannot use a bot user account, because bot users don't have permission to archive channels.
* SLACK_ARCHIVE_EMPTY_CHANNEL_SCHEDULE - 6 field cron pattern.
For example, `(0 0 1 * * *)` is 1am every day of the week. See the [Spring documentation](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled) for details.

The following environment variables are optional:

* SLACK_SERVER - defaults to `slack.com`
