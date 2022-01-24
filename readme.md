Фабрика Партнеры
======
## Детальная информация
https://confluence.sberbank.ru/pages/viewpage.action?pageId=4121926530

## Quick Start Guide
### Необходимое ПО
- **JDK** (Сборка и локальный запуск проекта поддерживаются на Liberica OpenJDK 15 с последними исправлениями безопасности)
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
git clone https://sbtatlas.sigma.sbrf.ru/stashdbo/scm/cibpprb/sbbol-partners.git
git checkout develop
```

### Аутентификация в NEXUS

Для обеспечения доступа в nexus необходимо указать credentials доменной УЗ.
ВАЖНО: не использовать онлайн-сервисы для encode/decode ваших УЗ и паролей.

1. Для доступа в nexus нужно создать задачу в Jira на админов, инструкция и шаблон здесь:
https://confluence.sberbank.ru/pages/viewpage.action?pageId=849021130

2. Создание новых или обновление существующих credentials осуществляется через таску `addCredentials`
Необходимо выполнить нижеуказанные команды, заменив значения login и password.

P.S. Если в пароле содержится спецсимвол, необходимо его экранировать, указав перед спецсимволов \
(Например `super'Puper'Password` должен указываться как `super\'Puper\'Password`)
```
./gradlew -p buildSrc addCredentials --key nexusLogin --value login --no-scan
./gradlew -p buildSrc addCredentials --key nexusPassword --value password --no-scan
```

Credentials хранятся в `GRADLE_HOME/gradle.encrypted.properties`

Удалить credentials можно таской `removeCredentials` с указанием ключа.

```
./gradlew -p buildSrc removeCredentials --key nexusLogin --no-scan
./gradlew -p buildSrc removeCredentials --key nexusPassword --no-scan
```

### Сборка проекта
`./gradlew build`.

### Запуск приложения локально
`./gradlew bootRun`, `make run`

### Запуск приложения локально под профилем dev
`./gradlew bootRun --args='--spring.profiles.active=dev'`, `make runDev`

### Проверить что приложение работает
`curl localhost:8080/actuator/health`, `make ping`

### Обновление зависимостей
`./gradlew resolveAndLockAll --write-locks`, `make resolveConflict`

###Создание отчёта SONAR при локальном запуске
`./gradlew sonar`

### Генерация sql патча
`./gradlew newpatch -Ppatchname=newpatch`

Для изменения директории создания патча использовать с флагом

`./gradlew newpatch -Ppatchname=newpatch -Preleaseversion='01.000.00'` или изменить версию в gradle.properties
