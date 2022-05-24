resolveConflict:
	./gradlew resolveAndLockAll --write-locks

run:
	./gradlew bootRun

runDev:
	./gradlew bootRun --args='--spring.profiles.active=dev'

runDevDebug:
	./gradlew bootRun --args='--spring.profiles.active=dev' --debug-jvm

buildNoCache:
	./gradlew clean build --no-build-cache

ping:
	curl localhost:8080/sbbol-partners/actuator/health
