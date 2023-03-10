Фабрика Партнеры
======

## Детальная информация

https://confluence.sberbank.ru/pages/viewpage.action?pageId=4121926530

## Quick Start Guide

### Необходимое ПО

- **JDK** (Сборка и локальный запуск проекта поддерживаются на Liberica OpenJDK 15 с последними исправлениями
  безопасности)
- **IDEA** (Рекомендуется версия IntelliJ IDEA не ниже 2021.2)
- **Git**

### Настройки git

Указать автора по умолчанию. Важно, чтобы name и email совпадали с учетной записью Active Directory

```
git config --global user.name "Пупкин Василий Васильевич"
git config --global user.email sbt.vasya@sberbank.ru
```

### Клонирование репозитория

```
git clone https://stash.sigma.sbrf.ru/scm/cibpprb/sbbol-partners.git
git checkout develop
```

### Установка сертификатов

Для сборки проекта необходимо установить корневой сертификат и сертификат удостоверяющего центра. Скачать их можно
тут: https://cert.sberbank.ru/pfx/

Для установки надо перейти в папку ~/.gradle (~ это домашняя дирректория) и выполнить команды

```
keytool -import -trustcacerts -alias sber-root -file путь_до/root.crt -keystore cacerts -storepass changeit
keytool -import -trustcacerts -alias sber-ca2 -file путь_до/ca2.crt -keystore cacerts -storepass changeit
```

В файле `~/.gradle/gradle.properties` (если файла нет необходимо его создать) добавить две строки

```
systemProp.javax.net.ssl.trustStore=/Users/<ваша пользовательская директория, например, a16689666>/.gradle/cacerts
systemProp.javax.net.ssl.trustStorePassword=changeit
```

### Аутентификация в NEXUS

Необходимо добавить креды для gradle. Необходимо залогиниться в https://nexus-ci.sigma.sbrf.ru/ и в управлении
аккаунтом (manage account) получить User Token для своей учетной записи. После этого указать в ~
/.gradle/gradle.properties:

```
systemProp.gradle.wrapperUser=<Your user token name code>
systemProp.gradle.wrapperPassword=<Your user token pass code>
tokenName=<Your user token name code>
tokenPassword=<Your user token pass code>
```

### Сборка проекта

`./gradlew build`.

### Сборка проекта с отключенным кешом и без запуска тестов

`./gradlew clean build --no-build-cache -x test`

### Запуск приложения локально

`./gradlew bootRun`, `make run`

### Запуск приложения локально под профилем dev

`./gradlew bootRun --args='--spring.profiles.active=dev'`, `make runDev`

### Проверить что приложение работает

`curl localhost:8080/actuator/health`, `make ping`

### Обновление зависимостей

`./gradlew resolveAndLockAll --write-locks`, `make resolveConflict`

### Создание отчёта SONAR при локальном запуске

`./gradlew sonar`

### Публикация API в META

`./gradlew clean build reverseAndPublish -x test`

### Генерация sql патча

`./gradlew newpatch -Ppatchname=newpatch`

Для изменения директории создания патча использовать с флагом

`./gradlew newpatch -Ppatchname=newpatch -Preleaseversion='02.000.00'` или изменить версию в gradle.properties

### Переопределение конфигурации

`./gradlew createCustom` создание каталога custom в котором можно переопределить конфигурацию для локального использования

### Запуск тестов

В тестах используется фреймворк testcontainers, который поднимает в Docker контейнеры с БД Postgres.

В случае использования удаленного Docker (на виртуалке из ДИ), необходимо в домашнюю директорию добавить файл
`.testcontainers.properties` (если файл уже есть, необходимо заменить) со следующим содержимым:

```
docker.client.strategy=org.testcontainers.dockerclient.EnvironmentAndSystemPropertyClientProviderStrategy
docker.tls.verify=1
# указать IP виртуальной машины, полученной из ДИ
docker.host=tcp\://{url}\:2376
# указать полный путь к папке с сертификатами, полученными из ДИ
docker.cert.path=/Users/{login}/.docker/tls
```

Для настройки Docker-Client необходимо воспользоваться инструкцией
https://confluence.sberbank.ru/pages/viewpage.action?pageId=9293898048
