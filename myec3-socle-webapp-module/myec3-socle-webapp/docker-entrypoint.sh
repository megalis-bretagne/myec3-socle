#!/bin/bash

NODE_HOSTNAME=$1
BDD_PASSWORD=$(cat /run/secrets/db_password)
echo "bdd.pwd = $BDD_PASSWORD" > /configGen/pwd.properties

JAVA_OPTS="$JAVA_OPTS -Dglowroot.agent.id=megalis::webapp::${NODE_HOSTNAME}::${HOSTNAME} -javaagent:/glowroot/glowroot.jar"

catalina.sh run
