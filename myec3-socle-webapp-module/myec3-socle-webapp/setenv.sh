# Les variables d'environnement suivantes sont utilisees dans les fichiers keycloak.json et keycloak.properties
PATH_FIC_PARAM="/config/environment.properties"
export BASE_URL_SSO=$(grep "keycloak.baseUrl=" $PATH_FIC_PARAM 2>/dev/null | sed 's/^[^=]*=//')
export SECRET_CLIENT_SOCLE=$(cat /run/secrets/keycloak_secret_client_socle)

# au niveau du fichier docker compose, les fichiers de config .properties sont copiés sous /config
# /configGen contient un fichier genere par l'entrypoint et contenant les secrets sous forme de properties
CLASSPATH="$CLASSPATH:/config/:/configGen/"