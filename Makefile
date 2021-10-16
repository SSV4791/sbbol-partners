resolveConflict:
	./gradlew resolveAndLockAll --write-locks

run:
	./gradlew bootRun

runDev:
	./gradlew bootRun --args='--spring.profiles.active=dev'

ping:
	curl localhost:8080/actuator/health
