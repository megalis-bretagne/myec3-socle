#!/bin/bash

ENV NODE_HOSTNAME
EXPORT NODE_HOSTNAME="$NODE_HOSTNAME"|cut -d'"' -f2
echo "NODE_HOSTNAME: $NODE_HOSTNAME"

JAVA_OPTS="$JAVA_OPTS -Dglowroot.agent.id=megalis::webapp::${NODE_HOSTNAME}::${HOSTNAME} -javaagent:/glowroot/glowroot.jar"
echo "JAVA_OPTS: ${JAVA_OPTS}"

BDD_PASSWORD=$(cat /run/secrets/db_password)
echo "bdd.pwd = $BDD_PASSWORD" > /configGen/pwd.properties

catalina.sh run
