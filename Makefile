resolveConflict:
	./gradlew resolveAndLockAll --write-locks

run:
	./gradlew bootRun

runDev:
	./gradlew bootRun --args='--spring.profiles.active=dev'

runDevPG:
	./gradlew bootRun --args='--spring.profiles.active=dev-postgres'

runDevPGDebug:
	./gradlew bootRun --args='--spring.profiles.active=dev-postgres' --debug-jvm

ping:
	curl localhost:8080/actuator/health
