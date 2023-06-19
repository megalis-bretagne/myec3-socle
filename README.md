# Projet MyEC3

## Somaire
* [Installation environnement développment en local](#installation-environnement-développement-en-local)
  * [Pré-requis](#pré-requis)
  * [Outils](#outils)
  * [Configuration](#configuration)
    * [Définition de variables d'environnement](#définition-de-variables-denvironnement)
    * [Préparation de Tomcat pour le déploiement](#préparation-de-tomcat-pour-le-déploiement)
    * [Configuration IntelliJ](#configuration-intellij)
    * [Base de données (env d'intégration)](#base-de-données-env-dintégration)
    * [Base de données (option alternative : BDD locale)](#base-de-données-option-alternative--bdd-locale)
  * [Déploiement du projet](#déploiement-du-projet)
    * [Avec Intellij Ultimate](#avec-intellij-ultimate)
    * [Avec Intellij Community](#avec-intellij-community)
* [Règles de développement](#règles-de-développement)
* [Présentation du CI](#présentation-du-ci)

## Installation environnement développement en local

### Pré-requis

* Installer JDK 1.8
* Installer [Tomcat 9.0.30](https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.30/bin/apache-tomcat-9.0.30.zip) (extraire l'archive dans un répertoire)
* Installer [Maven 3.6](https://archive.apache.org/dist/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.zip) (extraire l'archive dans un répertoire)

⚠ Le projet doit absolument utiliser un jdk 8 et pas uniquement être configuré en java 1.8.  
⚠ Il est important également de respecter la version de Maven : à partir de la version 3.8.1, Maven impose que la
connexion aux repos utilise https, or le Nexus du SIB est accessible en http seulement.

### Outils

Il est conseiller d'utiliser la version Ultimate d'IntelliJ pour réaliser le déploiement des modules directement dans Tomcat.  
Sinon, le déploiement devra se faire manuellement (possibilité d'automatiser via un script) dans Tomcat.

### Configuration

#### Définition de variables d'environnement

* JAVA_TOOL_OPTIONS = -Dfile.encoding=UTF8

Pour permettre l'utilisation de Maven en ligne de commande :

* JAVA_HOME = le répertoire d'installation du jdk 8
* MAVEN_HOME = le répertoire d'installation de maven
* PATH : ajouter %JAVA_HOME%\bin et %MAVEN_HOME%\bin

#### Configuration IntelliJ

Dans les "settings", sous **Build, execution, deployment** > **Build tools** > **Maven**, pour le champ "Maven home path", parcourir
et sélectionner le dossier d'installation de Maven.

Créer un fichier settings.xml pour la configuration proxy du SIB.

#### Préparation de Tomcat pour le déploiement

* Copier les jars du dossier `myec3-socle-webapp-module\myec3-socle-webapp\tomcat` dans le dossier lib de tomcat  
  (*TODO* ce dossier sera supprimé, les librairies externes ne doivent pas être dans les sources du projet)

#### Base de données (env d'intégration)

1. Dans le dossier `myec3-socle-synchro-module/myec3-socle-synchro/src/main/config/local/db/local`,
  créer un fichier `db.properties` en recopiant le contenu de `db.properties.template`
2. Editer le fichier pour renseigner l'adresse de la BDD (à récupérer depuis [Teampass] dans Supports_Megalis / Integ / connexion bdd (service mariadb))
  ```
  bdd.host=10.241.0.73 #adresse de l'intégration
  bdd.port=60006
  bdd.user=<voir sur Teampass>
  bdd.pwd=<voir sur Teampass>
  bdd.dump.rep=/dump
  bdd.socle.schema=em_socle
  bdd.synchro.schema=em_synchro
  bdd.keycloak.schema=keycloak
  ```

:warning: **Attention** : le mot de passe est surchargé par le fichier pwd.properties.

3. Réitérer l'opération dans le dossier `myec3-socle-webapp-module/myec3-scole-webapp/src/main/config/local/db/local`

#### Base de données (option alternative : BDD locale)

Il est également possible d'utiliser une base de données locale. Pour cela, installer MariaDB.

1. Créer les bases __em_socle__ et __em_synchro__.
   ```
   mysql -u root -p
   CREATE DATABASE em_socle;
   CREATE DATABASE em_synchro;
   CREATE DATABASE keycloak;
   ```
2. Faire un import dump de l'intégration pour initialiser les données.
3. Reprendre les étapes de [la section précédente](#base-de-données-env-dintégration) en adaptant les valeurs des
propriétés host/port/user/pwd.

### Déploiement du projet

#### Avec Intellij Ultimate

1. Build en local
   ```
   mvn clean install -P dblocal
   ```

2. Sous IntelliJ Ultimate éditer une nouvelle configuration  
  ![EDIT_CONFIG](doc/edit_config_tomcat.PNG)  

   * Dans l'onglet deployment, sélectionner à minima les modules 
     * myec3-socle-esb:war exploded
     * myec3-socle-webapp:war exploded

     ![DEPLOYMENT](doc/edit_deployment.PNG)
  
   * Dans l'onglet Startup/Connection ajouter les trois variables d'environnement ci-dessous

     | Name                | Value                                      |
     |---------------------|--------------------------------------------|
     | JAVA_OPTS           | `-Dspring.profiles.active=dblocal -Xmx1g`  |
     | BASE_URL_SSO        | `https://sso-preprod.megalis.bretagne.bzh` |
     | SECRET_CLIENT_SOCLE | `f39d1f33-6403-4887-a0d0-d569ecdfd188`     |

     ![CONFIG](doc/edit_startup.PNG)  
     (Vous pouvez le faire également pour le mode debug)

3. Exécuter le tomcat server depuis intelliJ et attendre le bon déploiement des wars  
   ![DEPLOY_OK](doc/deployment_ok.PNG)
4. Se rendre sur la page http://localhost:8080/myec3.  
   Une redirection est effectué vers le Keycloak de pré-prod.  
   Se connecter avec les identifiants récupérés depuis [Teampass] dans Supports_Megalis / Pre-Prod / Utilisateur super admin.

![MYEC3](doc/localhost_myec3.PNG)

#### Avec IntelliJ Community

1. Build en local
   ```
   mvn clean package
   ```
2. Copier les fichiers war depuis le dossier target vers {chemin d'installation tomcat}/apache-tomcat-9.0.30/webapp
3. Créer le fichier setenv.bat dans le {chemin d'installation tomcat}/apache-tomcat-9.0.30/bin
4. Dans le fichier setenv.bat ci dessous, renseigner les variables d'environnement comme suit :
   ```
   set JAVA_OPTS=-Dspring.profiles.active=dblocal -Xmx1g -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
   set BASE_URL_SSO=https://sso-preprod.megalis.bretagne.bzh
   set SECRET_CLIENT_SOCLE=f39d1f33-6403-4887-a0d0-d569ecdfd188
   ```

5. Ouvrir la ligne de commande et faire cd {chemin d'installation tomcat}/bin
6. Démarrer tomcat sur windows : `.\catalina.bat run` ou sur Linux : `./catalina.sh`
7. Se rendre sur la page http://localhost:8080/myec3

## Règles de développement

### Développement de feature/fix non urgent
Pour traiter un ticket JIRA, suivre le process suivant :
* Se mettre sur develop
  ```  
  git pull
  ```  
* Tirer une branche avec le nom feature/MEGALIS-[ID-JIRA]-description
* Mettre le ticket JIRA à "en cours" 
* Réaliser le dev sur cette branche, tester en local.
* Une fois le dev terminé, faire une merge request de la branche vers develop
* Passer le ticket JIRA à "Test/review"
* une autre personne va lire la MR et déployer en intégration pour valider la correction

### Développement de hotfix urgent
Pour traiter un ticket JIRA urgent (incident en prod), suivre le process suivant :
* Se mettre sur master
  ```  
  git pull
  ``` 
* Tirer une branche avec le nom hotfix/MEGALIS-[ID-JIRA]-description
* Mettre le ticket JIRA à "en cours"
* Réaliser le dev sur cette branche, tester en local.
* Une fois le dev terminé, faire une merge request de la branche vers master ET develop
* Passer le ticket JIRA à "Test/review"
* Une autre personne doit lire la MR puis déployer rapidement en prod


### Bonnes pratiques GIT
Nommage des branches :
* feature/MEGALIS-[ID-JIRA]-description
* hotfix/MEGALIS-[ID-JIRA]-description

Message de commit :
* [FEAT] MEGALIS-[ID-JIRA] : description détailler du dev
* [FIX] MEGALIS-[ID-JIRA]: description détaillé du fix

:warning: **ATTENTION** : chaque commit doit être tracable par le numéro du ticket JIRA. La description doit être claire et assez détaillé.
Eviter les commentaires "oups", "fix" etc...

### Présentation du CI

[voir le PowerPoint](doc/GitFlowMyEC3.ppt)

[Teampass]: https://teampass.sib.fr