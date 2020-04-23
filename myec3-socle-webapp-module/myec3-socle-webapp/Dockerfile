#on part d'une image tomcat:9.0.30-jdk8-openjdk
FROM tomcat:8.5.51-jdk8-openjdk

#mise a jour des packets, installation de curl, suppression du cache de apt
RUN apt-get update -y && \
apt-get upgrade -y && \
apt-get install -y curl && \
apt-get install -y nano && \
apt-get clean

#ajout agent glowroot
RUN cd /  && \
wget https://github.com/glowroot/glowroot/releases/download/v0.13.5/glowroot-0.13.5-dist.zip && \
unzip glowroot-0.13.5-dist.zip && \
echo "collector.address=http://glowroot:8181" > /glowroot/glowroot.properties && \
echo "multi.dir=true" >> /glowroot/glowroot.properties

#recupération de nexus de l'artifact
# TODO : récupérer la version du pom.xml
ADD "http://nexus.sib.fr:8081/nexus/service/local/artifact/maven/redirect?r=sib-group&g=org.myec3.socle&a=myec3-socle-webapp&v=5.6.2-MEGALIS-SNAPSHOT&p=war" /usr/local/tomcat/webapps/ROOT.war
RUN unzip /usr/local/tomcat/webapps/ROOT.war
RUN rm /usr/local/tomcat/webapps/ROOT.war
#livrairies de l'adapter keycloak a copier directement dans le repertoire lib du tomcat
COPY tomcat/* /usr/local/tomcat/lib/

COPY server.xml /usr/local/tomcat/conf/
COPY setenv.sh /usr/local/tomcat/bin/
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod u+x /usr/local/bin/docker-entrypoint.sh
RUN mkdir /configGen

CMD ["docker-entrypoint.sh"]

#HEALTHCHECK --interval=1m --timeout=5m --start-period=2m --retries=3 CMD curl -f http://localhost:8080/health || exit
