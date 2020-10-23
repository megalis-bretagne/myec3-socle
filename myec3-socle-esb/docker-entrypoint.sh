#!/bin/bash

export NODE_HOSTNAME=$(echo "$NODE_HOSTNAME"|sed 's/\"//g'|sed 's/ //g')
echo "NODE_HOSTNAME: $NODE_HOSTNAME"

#Parametre gestion de mémoire par la limitation de ram donnée par docker (spécifique java8)
JAVA_OPTS="$JAVA_OPTS_ADD -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"
#glowroot
JAVA_OPTS="$JAVA_OPTS -Dglowroot.agent.id=megalis::esb::${NODE_HOSTNAME} -javaagent:/glowroot/glowroot.jar"

export JAVA_OPTS="$JAVA_OPTS -Duser.timezone=Europe/Paris "

echo "JAVA_OPTS: ${JAVA_OPTS}"

catalina.sh run
