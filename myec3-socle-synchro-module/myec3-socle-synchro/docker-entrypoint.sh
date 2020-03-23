#!/bin/bash

export NODE_HOSTNAME=$(echo "$NODE_HOSTNAME"|sed 's/\"//g'|sed 's/ //g')
echo "NODE_HOSTNAME: $NODE_HOSTNAME"

export JAVA_OPTS="${JAVA_OPTS} ${PROXY_JAVA_OPTS} -Duser.timezone=Europe/Paris -Dglowroot.agent.id=megalis::synchro::${NODE_HOSTNAME} -javaagent:/glowroot/glowroot.jar"
echo "JAVA_OPTS: ${JAVA_OPTS}"

BDD_PASSWORD=$(cat /run/secrets/db_password)
echo "bdd.pwd = $BDD_PASSWORD" > /configGen/pwd.properties

catalina.sh run