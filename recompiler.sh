#!/usr/bin/env bash
#
# recompiler.sh — Recompile le projet "Les Sprint" (mini-framework + app-web).
#
# Usage :
#   ./recompiler.sh            # recompile seulement (mvn clean install)
#   ./recompiler.sh --deploy   # recompile puis déploie le war sur Tomcat
#   ./recompiler.sh -h         # affiche cette aide
#
# Variables d'environnement optionnelles :
#   TOMCAT_HOME   répertoire de Tomcat (par défaut : /opt/Tomcat)
#   JAVA_HOME     JDK à utiliser (détecté automatiquement sinon)
#
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WAR="$PROJECT_DIR/app-web/target/app-web.war"
TOMCAT_HOME="${TOMCAT_HOME:-/opt/Tomcat}"

usage() {
    sed -n '2,13p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
    exit 0
}

case "${1:-}" in
    -h|--help) usage ;;
esac

# ---------------------------------------------------------------------------
# 1. Sélection d'un JDK 17+ (le projet cible Java 17)
# ---------------------------------------------------------------------------
est_jdk17() {
    [ -x "$1/bin/javac" ] && "$1/bin/javac" -version 2>&1 | grep -qE 'javac (17|21)\.'
}

if [ -n "${JAVA_HOME:-}" ] && est_jdk17 "$JAVA_HOME"; then
    :
else
    for candidat in \
        /usr/lib/jvm/java-17-openjdk-amd64 \
        /usr/lib/jvm/openjdk-17 \
        /usr/lib/jvm/java-1.17.0-openjdk-amd64 \
        /usr/lib/jvm/java-21-openjdk-amd64 \
        /usr/lib/jvm/openjdk-21; do
        if est_jdk17 "$candidat"; then
            export JAVA_HOME="$candidat"
            break
        fi
    done
fi

if [ -z "${JAVA_HOME:-}" ] || ! est_jdk17 "$JAVA_HOME"; then
    echo "ERREUR : aucun JDK 17+ trouvé. Installe-en un ou définis JAVA_HOME." >&2
    exit 1
fi

echo ">> JDK : $JAVA_HOME ($("$JAVA_HOME/bin/javac" -version 2>&1))"

# ---------------------------------------------------------------------------
# 2. Compilation (hors-ligne d'abord, repli en ligne si nécessaire)
# ---------------------------------------------------------------------------
cd "$PROJECT_DIR"

echo ">> Compilation du projet..."
if mvn -q -o clean install; then
    :
else
    echo ">> Dépendances manquantes hors-ligne, nouvel essai avec accès réseau..."
    mvn -q clean install
fi

echo ">> Build OK."
echo "   mini-framework : $(ls -1 "$PROJECT_DIR"/mini-framework/target/*.jar 2>/dev/null | head -1 || echo 'n/a')"
echo "   app-web        : $WAR"

# ---------------------------------------------------------------------------
# 3. Déploiement Tomcat (optionnel)
# ---------------------------------------------------------------------------
if [ "${1:-}" = "--deploy" ] || [ "${1:-}" = "-d" ]; then
    if [ ! -d "$TOMCAT_HOME" ]; then
        echo "ERREUR : Tomcat introuvable dans '$TOMCAT_HOME' (définis TOMCAT_HOME)." >&2
        exit 1
    fi
    if [ ! -f "$WAR" ]; then
        echo "ERREUR : war introuvable : $WAR" >&2
        exit 1
    fi

    echo ">> Arrêt de Tomcat..."
    "$TOMCAT_HOME/bin/shutdown.sh" >/dev/null 2>&1 || true
    sleep 4

    echo ">> Copie du war..."
    rm -rf "$TOMCAT_HOME/webapps/app-web" "$TOMCAT_HOME/webapps/app-web.war"
    cp "$WAR" "$TOMCAT_HOME/webapps/"

    echo ">> Démarrage de Tomcat..."
    "$TOMCAT_HOME/bin/startup.sh" >/dev/null 2>&1 || true
    sleep 8

    echo ">> Vérification :"
    for url in \
        "/app-web/" \
        "/app-web/page/hello" \
        "/app-web/api/hello" \
        "/app-web/assets/css/style.css"; do
        printf "   %-32s -> %s\n" "$url" \
            "$(curl -s -o /dev/null -w '%{http_code}' "http://localhost:8080$url")"
    done
fi

echo ">> Terminé."
