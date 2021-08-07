##Описание
Данный проект является демонстрационным примером типового Backend-приложения с использованием компонентов Платформы:
* DataSpace
* Мониторинг
* Журналирование
* Аудит
* Meta
* SPAS

Анализ и проектирование рассмотрены в [примере](https://sbtatlas.sigma.sbrf.ru/wiki/pages/viewpage.action?pageId=3313997514)

## Концепция DataSpace
Основная задача DataSpace – предоставить возможность сформировать модель данных и далее максимально абстрагировать потребителя от уровня физического хранения, обеспечив базовые прикладные сервисы для работы с данными на изменение (UnitOfWork) и чтение/поиски (Graph/Grasp) (см. описание в Руководстве разработчика).

Писать приложение с использованием DataSpace можно на любом языке!

Данные сервисы потребителю предоставляет серверный компонент **DataSpace Core** посредством протокола [JSON-RPС 2.0 over HTTP](https://ru.wikipedia.org/wiki/JSON-RPC) ([спецификация](https://www.jsonrpc.org/specification)).

Для удобства работы с сервисами на клиентской стороне возможна генерация соответствующего Java SDK.

![img](documentation/images/image2020-8-28_13-23-32.png)

## Процесс создания типовой фабрики c использованием DataSpace

Процесс создания типовой фабрики с использованием DataSpace состоит из следующих шагов:
1. [Описание модели](https://sbtatlas.sigma.sbrf.ru/wiki/pages/viewpage.action?pageId=2072811788).
2. [Разработка бизнес-логики](https://sbtatlas.sigma.sbrf.ru/wiki/pages/viewpage.action?pageId=2211152290).
3. [Настройка мониторинга](https://sbtatlas.sigma.sbrf.ru/wiki/pages/viewpage.action?pageId=3121022643).
4. [Настройка журналирования](https://sbtatlas.sigma.sbrf.ru/wiki/pages/viewpage.action?pageId=3121022640).
5. [Настройка аудита](https://sbtatlas.sigma.sbrf.ru/wiki/pages/viewpage.action?pageId=3121022639).
6. [Развертывание в OpenShift](https://sbtatlas.sigma.sbrf.ru/wiki/display/SPD/DataSpace-Core+PipelineV4).

##Структура проекта
Проект состоит из следующих артефактов

|Имя артефакта|Описание|
|---|---|
|dataspace-deposit|API и имплементации (см. описание API ниже)|
|deposit-model-jpa|Сгенерированные по [model.xml](https://sbtatlas.sigma.sbrf.ru/stash/projects/PPRBAC/repos/dataspace-client/browse/model/src/main/resources/model/model.xml) JPA классы|
|deposit-model-sdk|Сгенерированный по [model.xml](https://sbtatlas.sigma.sbrf.ru/stash/projects/PPRBAC/repos/dataspace-client/browse/model/src/main/resources/model/model.xml) SDK|

## Сборка проекта
Для сборки проекта подключите [settings.xml](https://sbtatlas.sigma.sbrf.ru/stash/projects/PPRBAC/repos/dataspace-client/browse/config/settings.xml) в настройках maven

##Локальный запуск и вызов сервисов
Для локального тестирования приложения вам необходимо:
1. Запустить локально dataspace-core, который автоматически поднимет встроенную БД, создаст необходимые структуры данных в соответствии с описанием модели, а также инициалицирует данные. После генерации артефактов модели [deposit-model-jpa](https://sbtatlas.sigma.sbrf.ru/stash/projects/PPRBAC/repos/dataspace-client/browse/deposit-model-jpa) это можно осуществить посредством [скрипта](https://sbtatlas.sigma.sbrf.ru/stash/projects/PPRBAC/repos/dataspace-client/browse/documentation/local-run.sh)
    >Скрипт осуществляет запуск SpringBoot-приложения DataSpace Core, расширяемого classpath'ом артефактов, сгенерированных в артефакте deposit-model-jpa, за счет JVM-параметра:
    >-Dloader.path=./deposit-model-jpa/target

    #####Внимание
    Так как скрипт содержит специфичный для Linux синтаксис, то запуск необходимо осуществлять в Bash-среде. Для Windows, к примеру, можно воспользоваться Git Bash-эмулятором.
    Строка запуска: ./local-run.sh

2. Запустить приложение dataspace-deposit. Для этого необходимо запустить SpringBoot-приложение [Runner](https://sbtatlas.sigma.sbrf.ru/stash/projects/PPRBAC/repos/dataspace-client/browse/dataspace-deposit/dataspace-deposit-service/src/main/java/sbp/sbt/dataspace/deposit/Runner.java)

##Настройки приложения
Настройки для локального запуска приложения находятся в файле [application.properties](https://sbtatlas.sigma.sbrf.ru/stash/projects/PPRBAC/repos/dataspace-client/browse/dataspace-deposit/dataspace-deposit-service/src/main/resources/application.properties)

Если необходимо поменять настройки подключения к БД для тестов (например указать внешнюю БД для прогона тестов), то это можно сделать в файле [application-h2-liquibase.properties](https://sbtatlas.sigma.sbrf.ru/stash/projects/PPRBAC/repos/dataspace-client/browse/dataspace-deposit/dataspace-deposit-service/src/test/resources/application-h2-liquibase.properties)

##Описание API
В проекте реализованы следующие сервисы
  1. Открытие депозита (OpenDepositService.execute)
  2. Капитализация депозита (CapitalizeDepositService.execute)
  3. Получение списка открытых депозитов клиента (SearchDepositService.searchOpenedDeposit)
  4. Получение списка ставок по депозитам (SearchDepositService.searchDepositRates)

Описание данных API [загружено](https://meta.sigma.sbrf.ru/index.html?session_state=3c9ba986-3142-4eaa-9f9e-bd4ce99f55a8&code=d974d178-fb36-4aa1-a7f4-0d02298ba9cb.3c9ba986-3142-4eaa-9f9e-bd4ce99f55a8.2ff942cd-826d-4ee4-a13d-fb5cbe640814#/search/API/?q=dataspace) в META

####Примеры вызова API
>Параметры вызова лежат в каталоге documentation
>При проблеме с кодировкой русских букв в terminal в IDEA выполните chcp 65001

* Открытие депозита
    ```curl -d "@opendeposit.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:8081/opendeposit```
* Капитализация депозита
    ```curl -d "@capitilazedeposit.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:8081/capitalizedeposit```
* Поиск текущих счетов клиента (вызов возможен только при локальном запуске приложения!)
    ```curl -d "@searchaccount.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:8081/accountservice```

* Получение списка открытых депозитов по клиенту
    ```curl -d "@searchdeposit.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:8081/searchdeposit```

* Тестовый вызов json-rpc сервиса /packet на локальной машине командой curl:
```curl -d "@packet-request.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:8080/packet```

* Тестовый вызов json-rpc сервиса /search на локальной машине командой curl:
```curl -d "@search-request.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:8080/search```

* Регистрация метамодели capitalizedDeposit в ТС Аудит в сигме
```curl -d "@metamodel-capitalized-deposit.json" -H "Content-Type: application/json" -X POST http://demo.sigma.audit-http-proxy.apps.dev-gen.sigma.sbrf.ru```

* Регистрация метамодели openDeposit в ТС Аудит в сигме
```curl -d "@metamodel-open-deposit.json" -H "Content-Type: application/json" -X POST http://demo.sigma.audit-http-proxy.apps.dev-gen.sigma.sbrf.ru```

##  Тестирование
* [Sonar](sonar.md)
