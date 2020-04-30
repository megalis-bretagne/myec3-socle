#!/bin/bash

export NODE_HOSTNAME=$(echo "$NODE_HOSTNAME"|sed 's/\"//g'|sed 's/ //g')
echo "NODE_HOSTNAME: $NODE_HOSTNAME"

#Parametre gestion de mémoire par la limitation de ram donnée par docker (spécifique java8)
JAVA_OPTS="$JAVA_OPTS_ADD -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true -Dcom.sun.xml.ws.transport.http.HttpAdapter.dump=true -Dcom.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump=true -Dcom.sun.xml.internal.ws.transport.http.HttpAdapter.dump=true"
#glowroot
JAVA_OPTS="$JAVA_OPTS -Dglowroot.agent.id=megalis::synchro::${NODE_HOSTNAME} -javaagent:/glowroot/glowroot.jar"

export JAVA_OPTS="$JAVA_OPTS -Duser.timezone=Europe/Paris "
echo "JAVA_OPTS: ${JAVA_OPTS}"

BDD_PASSWORD=$(cat /run/secrets/db_password)
echo "bdd.pwd = $BDD_PASSWORD" > /configGen/pwd.properties

export JPDA_ADDRESS=8000
export JPDA_TRANSPORT=dt_socket

catalina.sh run