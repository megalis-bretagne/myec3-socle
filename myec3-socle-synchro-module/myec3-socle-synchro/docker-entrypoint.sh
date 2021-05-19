#!/bin/bash

export NODE_HOSTNAME=$(echo "$NODE_HOSTNAME"|sed 's/\"//g'|sed 's/ //g')
echo "NODE_HOSTNAME: $NODE_HOSTNAME"

#Parametre gestion de mémoire par la limitation de ram donnée par docker (spécifique java8)
JAVA_OPTS="$JAVA_OPTS_ADD -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"
#glowroot
JAVA_OPTS="$JAVA_OPTS -Dglowroot.agent.id=megalis::synchro::${NODE_HOSTNAME} -javaagent:/glowroot/glowroot.jar"

export JAVA_OPTS="$JAVA_OPTS -Duser.timezone=Europe/Paris "
echo "JAVA_OPTS: ${JAVA_OPTS}"

BDD_PASSWORD=$(cat /run/secrets/db_password)
echo "bdd.pwd = $BDD_PASSWORD" > /configGen/pwd.properties


export JPDA_ADDRESS=8000
export JPDA_TRANSPORT=dt_socket

#CLEAN TRIGGERS QUARTZ on startup
BDD_HOST=$(cat /config/db.properties | grep bdd.host | sed 's/bdd.host=//g')
BDD_USER=$(cat /config/db.properties | grep bdd.user | sed 's/bdd.user=//g')
BDD_PORT=$(cat /config/db.properties | grep bdd.port | sed 's/bdd.port=//g')
DB_SYNCHRO=$(cat /config/db.properties | grep bdd.synchro.schema | sed 's/bdd.synchro.schema=//g')

echo "Clean QRTZ BEFORE Starting"
echo "mysql --host=$BDD_HOST --port=$BDD_PORT -u $BDD_USER -D $DB_SYNCHRO"

CLEAN_QRTZ=$(mysql --host=$BDD_HOST --port=$BDD_PORT -u $BDD_USER --password=$BDD_PASSWORD -D $DB_SYNCHRO -e "DELETE FROM QRTZ_PARALLEL_TRIGGERS WHERE TRIGGER_NAME NOT IN (SELECT TRIGGER_NAME FROM QRTZ_PARALLEL_SIMPLE_TRIGGERS) AND SCHED_NAME = 'parallelScheduler'")

echo $CLEAN_QRTZ

catalina.sh run