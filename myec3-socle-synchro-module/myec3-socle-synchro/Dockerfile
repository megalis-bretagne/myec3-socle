#on part d'une image tomcat:9.0.30-jdk8-openjdk
FROM tomcat:9.0.30-jdk8-openjdk

#mise a jour des packets, installation de curl, suppression du cache de apt
RUN apt-get update -y && \
apt-get upgrade -y && \
apt-get install -y curl && \
apt-get install -y nano && \
apt-get install -y mariadb-client && \
apt-get clean

#ajout agent glowroot
RUN cd /  && \
wget https://github.com/glowroot/glowroot/releases/download/v0.13.5/glowroot-0.13.5-dist.zip && \
unzip glowroot-0.13.5-dist.zip && \
echo "collector.address=http://glowroot:8181" > /glowroot/glowroot.properties && \
echo "multi.dir=true" >> /glowroot/glowroot.properties

#recupération de nexus de l'artifact
# TODO : récupérer la version du pom.xml
COPY target/myec3-socle-synchro-*.war /usr/local/tomcat/webapps/myec3-socle-synchro.war
#ADD "http://nexus.sib.fr:8081/nexus/service/local/artifact/maven/redirect?r=sib-group&g=org.myec3.socle&a=myec3-socle-synchro&v=5.6.2-MEGALIS-SNAPSHOT&p=war" /usr/local/tomcat/webapps/myec3-socle-synchro.war

COPY setenv.sh /usr/local/tomcat/bin/
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod u+x /usr/local/bin/docker-entrypoint.sh
RUN mkdir /configGen

CMD ["docker-entrypoint.sh", "${HOSTNAME}"]

#HEALTHCHECK --interval=1m --timeout=5m --start-period=2m --retries=3 CMD curl -f http://localhost:8080/health || exit
