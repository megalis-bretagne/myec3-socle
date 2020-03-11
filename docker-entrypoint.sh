#!/bin/bash

ENV NODE_HOSTNAME
export NODE_HOSTNAME=$(echo "$NODE_HOSTNAME"|sed 's/\"//g'|sed 's/ //g')
echo "NODE_HOSTNAME: $NODE_HOSTNAME"

JAVA_OPTS="$JAVA_OPTS -Dglowroot.agent.id=megalis::esb::${NODE_HOSTNAME} -javaagent:/glowroot/glowroot.jar"
echo "JAVA_OPTS: ${JAVA_OPTS}"

catalina.sh run
