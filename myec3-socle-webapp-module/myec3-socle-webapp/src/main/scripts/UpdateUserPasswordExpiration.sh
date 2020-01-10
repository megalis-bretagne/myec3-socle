#!/bin/sh

CLASSPATH="/WEBS/ebourgogne/socle.e-bourgogne.fr/docs/WEB-INF/classes";
JAVA_ARGS="-Xms32m -Xmx640m";

for librairie in `ls -r /WEBS/ebourgogne/socle.e-bourgogne.fr/docs/WEB-INF/lib`
do
	CLASSPATH="$CLASSPATH:/WEBS/ebourgogne/socle.e-bourgogne.fr/docs/WEB-INF/lib/$librairie";
done

TASK="/usr/java/jdk1.6.0_45/bin/java $JAVA_ARGS -classpath $CLASSPATH org.myec3.socle.webapp.batch.UpdateUserPasswordExpiration $*";

#echo "${TASK}";

$TASK

 