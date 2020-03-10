#!/bin/bash

BDD_PASSWORD=$(cat /run/secrets/db_password)
echo "bdd.pwd = $BDD_PASSWORD" > /configGen/pwd.properties

catalina.sh run