#!/bin/bash
echo "ARGS: $@" >> /tmp/g.args
./gradlew.real "$@" 2>&1 | tee -a /tmp/g.out
