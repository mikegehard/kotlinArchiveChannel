#!/usr/bin/env bash

./gradlew build && cf push -f pws-manifest.yml
