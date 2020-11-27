ARG registryUrl
ARG rhelOpenJdkUrl
FROM $registryUrl$rhelOpenJdkUrl
ARG version=DEV-SNAPSHOT
COPY ./dataspace-deposit-service-$version.jar /usr/app/dataspace-deposit-service.jar
COPY ./Starter.sh /usr/app/Starter.sh

WORKDIR /usr/app
USER root
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

RUN chgrp -R 0 /usr/app/ && chmod -R g=u /usr/app/; \
    chmod +x /usr/app/Starter.sh; \
    su -c 'rm -f /etc/localtime && ln -s /usr/share/zoneinfo/Europe/Moscow /etc/localtime'
ENV RUN_DEBUG=false;
EXPOSE 8080
EXPOSE 8000

ONBUILD ADD ./ext /usr/app/ext
CMD sleep 10; \
./Starter.sh