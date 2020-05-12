#!/bin/sh

CLASSPATH=bin
CLASSPATH=$CLASSPATH:lib/jetty-all-uber.jar

java \
  -Djava.util.logging.config.file=scripts/logging.properties \
  -cp $CLASSPATH \
  relay.RelayMain $@
