#!/bin/sh

if [ "$RUN_DEBUG" == "true" ];
then
  DEBUG_CONFIG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000"
fi

export LOGGING_CONFIG="-Dlogging.config=/deployments/config/logback/logback.xml"
export CONFIG_LOCATIONS="-Dspring.config.location=file:/deployments/config/application.properties,file:/deployments/credentials/main_db/secret.properties,file:/deployments/credentials/si_db/secret.properties,file:/deployments/config/app_journal/appJournal.properties"

exec java $DEBUG_CONFIG $CONFIG_LOCATIONS $LOGGING_CONFIG -jar partners.jar
