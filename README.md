# Kotlin/SpringBoot Archive Channels Slack Bot

## Functionality

* Archive all channels that do not have a minimum number of members
 on a cron schedule. Optionally you can post a message to the channel
 before it is archived.

## Development

1. Clone repo.
1. `./gradlew build` to make sure tests are green.
1. Code.

## Configuration

The following environment variables are required:

* SLACK_TOKEN - you must use an [API key](https://api.slack.com/docs/oauth-test-tokens) for a regular Slack user account. You cannot use a bot user account, because bot users don't have permission to archive channels.
* SLACK_ARCHIVE_EMPTY_CHANNEL_SCHEDULE - 6 field cron pattern.
For example, `0 0 1 * * ?` is 1am every day of the week. See [Quartz documentation](http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06.html#TutorialLesson6-CronExpressions) for examples of cron schedule strings.

The following environment variables are optional:

* SLACK_SERVER - defaults to `slack.com`
* SLACK_MINIMUM_NUMBER_OF_MEMBERS - Threshold for archiving channels. Defaults to 1, which will only archive empty channels.
* SLACK_ARCHIVE_MESSAGE - message to send to any archived channels. See [Slack message builder](https://api.slack.com/docs/formatting/builder) for help crafting a message.

## Deployment to Cloud Foundry

**NOTE: You need a version of the CF CLI that allows the `-u`
flag to allow the manifest to turn the healthcheck off. Manifest tested with
`cf version 6.18.1+a1103f0-2016-05-24.`**

For the first time:

```
$ cf push -f cf-manifest.yml -p <pathToJar> --no-start
$ cf set-env archive-slack-channels SLACK_ARCHIVE_EMPTY_CHANNEL_SCHEDULE <someCronSchedule>
$ cf set-env archive-slack-channels SLACK_TOKEN <someSlackToken>
$ cf start archive-slack-channels
```

The rest of the time:
```
$ cf push -f cf-manifest.yml -p <pathToJar>
```
