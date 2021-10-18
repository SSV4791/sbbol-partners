/**
 * Переменные окружения для джоб jenkins'а
 */

env.GIT_PROJECT = 'CIBPPRB'
env.GIT_REPOSITORY = 'sbbol-partners'
env.GROUP_ID = 'Nexus_PROD.CI02792425_sbbol-partners'
env.ARTIFACT_ID = 'partners'
env.DOCKER_REGISTRY = 'registry.sigma.sbrf.ru'
env.BASE_IMAGE_NAME = 'pprb/ci00908578/ci02792425_sbbol-partners'
env.BUILD_JAVA_DOCKER_IMAGE = 'registry.sigma.sbrf.ru/ci00149046/ci00405008_sbbolufs/bellsoft/liberica-openjdk-alpine:15.0.1-9-with-certs'
env.SONAR_TOKEN = credentials('sonar-token-partners')
env.SONAR_PROJECT = 'ru.sberbank.pprb.sbbol.partners:partners'
