#!/bin/bash

export NODE_HOSTNAME=$(echo "$NODE_HOSTNAME"|sed 's/\"//g'|sed 's/ //g')
echo "NODE_HOSTNAME: $NODE_HOSTNAME"

export JAVA_OPTS="${JAVA_OPTS} ${PROXY_JAVA_OPTS} -Duser.language=fr -Duser.country=FR -Duser.timezone=Europe/Paris -Dglowroot.agent.id=megalis::esb::${NODE_HOSTNAME} -javaagent:/glowroot/glowroot.jar"
echo "JAVA_OPTS: ${JAVA_OPTS}"

catalina.sh run
