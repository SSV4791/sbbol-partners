if [ "$RUN_DEBUG" == "true" ];
then
  java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 ${JVM_OPTION} -cp runner.jar -Dlogging.config=/deployments/config/logback/logback.xml -Dspring.config.location=file:/deployments/config/application.properties org.springframework.boot.loader.PropertiesLauncher
else
  java ${JVM_OPTION} -cp runner.jar  -Dlogging.config=/deployments/config/logback/logback.xml -Dspring.config.location=file:/deployments/config/application.properties org.springframework.boot.loader.PropertiesLauncher
fi