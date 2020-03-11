# au niveau du fichier docker compose, les fichiers de config .properties sont copiés sous /config
# /configGen contient un fichier genere par l'entrypoint et contenant les secrets sous forme de properties
CLASSPATH="$CLASSPATH:/config/:/configGen/"