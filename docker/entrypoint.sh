#!/bin/sh

# Ожидание готовности контейнера истио
echo Waiting for Istio
while ! curl http://127.0.0.1:15021/healthz/ready ;
do
    echo -n .
    sleep 1
done
echo Istio OK

# Ожидание инжекта секретов
if [ -z $1 ]; then
    waiting_secrets=/tmp/secman/config/waitingSecrets.txt
else
    waiting_secrets=$1
fi
checked_files=`cat $waiting_secrets`
files_count=0
for file in $checked_files ; do
    files_count=$(( files_count + 1 ))
done
exists_files_count=0
time_counter=0
while [ $exists_files_count != $files_count ]; do
    exists_files_count=0
    for file in $checked_files ; do
        if [ -f $file ]; then
            exists_files_count=$(( exists_files_count + 1 ))
        fi
    done
    sleep 1
    time_counter=$(( time_counter + 1 ))
    echo "Waiting $time_counter s."
done

# Определение кастомных енвов
if [ -e /vault/secrets/environments ]; then
    custom_envs=`cat /vault/secrets/environments/*`
else
    custom_envs=''
fi

if [ "$RUN_DEBUG" == "true" ];
then
  export DEBUG_CONFIG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000"
fi

# Запуск приложения
env $custom_envs sh -c '
exec java $DEBUG_CONFIG -jar partners.jar
'
