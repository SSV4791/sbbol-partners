/**
 * Переменные окружения для джоб jenkins'а
 */

env.GIT_PROJECT = 'CIBPPRB'
env.GIT_REPOSITORY = 'sbbol-partners'
env.GROUP_ID = 'Nexus_PROD.CI02792425_sbbol-partners'
env.ARTIFACT_ID = 'partners'
env.DOCKER_REGISTRY = 'registry.sigma.sbrf.ru'
env.DOCKER_IMAGE_REPOSITORY_PROD = 'pprb/ci00908578/ci02792425_sbbol-partners'
env.DOCKER_IMAGE_REPOSITORY_DEV = 'pprb-dev/ci00908578/ci02792425_sbbol-partners_dev'
env.BUILD_JAVA_DOCKER_IMAGE = 'registry.sigma.sbrf.ru/ci00149046/ci00405008_sbbolufs/bellsoft/liberica-openjdk-alpine:15.0.2-10-with-certs'
