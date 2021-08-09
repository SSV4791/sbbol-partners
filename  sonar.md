#Создание отчета SONAR при локальном запуске

* Сформировать файл отчета JaCoCo. Для этого необходимо собрать проект с профилем ```sonar``` и запустить тесты.

      mvn clean install -P sonar

  + Отчет о покрытии можно просмотреть ```partners-test/target/site/jacoco-aggregate/index.html```


* Запустить SONAR анализатор локально и выгрузки отчет, указав вашу ветку в ```<ветка>```.

      mvn sonar:sonar -Dsonar.host.url=https://sbt-sonarqube.sigma.sbrf.ru -Dsonar.login={user_token} -Dsonar.branch.name=<ветка>

* Отчет можно получить по адресу
  https://sbt-sonarqube.sigma.sbrf.ru/dashboard?id=ru.sberbank.pprb.sbbol.partners%3Apartners&branch=release-1.001.00,
  где ```<ветка>``` ваша ветка.
