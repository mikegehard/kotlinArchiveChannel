# Kotlin/SpringBoot Archive Channels Slack Bot

## Functionality

* Archive all empty channels on a cron schedule

## Development

1. Clone repo.
1. `./gradlew build` to make sure tests are green.
1. Code.

## Configuration

The following environment variables are required:

* SLACK_TOKEN - you must use an [API key](https://api.slack.com/docs/oauth-test-tokens) for a regular Slack user account. You cannot use a bot user account, because bot users don't have permission to archive channels.
* SLACK_ARCHIVE_EMPTY_CHANNEL_SCHEDULE - 6 field cron pattern.
For example, `0 0 1 * * *` is 1am every day of the week. See the [Spring documentation](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled) for details.

The following environment variables are optional:

* SLACK_SERVER - defaults to `slack.com`

## Deployment to Cloud Foundry

See `scripts/deploy.sh`.