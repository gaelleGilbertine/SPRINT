
[ok] sprint 6 
 refa manao requette tsy manao requette dispacher fa json

[ok] sprint 7 (binding)
- creer un formulaire dans front
- avoir une fonction dans controller
- la fonction recoit les parametres du formulaire

sprint 7 Bis
 la meme chose que 7 mais recois un objet cette fois ci 


 DEPLOYEMENT: 
 1. /opt/Tomcat/bin/version.sh | grep "Server version"
 2. /opt/Tomcat/bin/shutdown.sh
 3. cd ~/Documents/L3/"Les Sprint"/mini-framework && mvn clean install
    cd ~/Documents/L3/"Les Sprint"/app-web && mvn clean package
 4. unzip -l ~/Documents/L3/"Les Sprint"/app-web/target/app-web.war | grep jsp
 5. rm -rf /opt/Tomcat/webapps/app-web
    rm -f /opt/Tomcat/webapps/app-web.war
    cp ~/Documents/L3/"Les Sprint"/app-web/target/app-web.war /opt/Tomcat/webapps/         (Si tu as Permission denied, ajoute sudo)
 6. /opt/Tomcat/bin/startup.sh
 7. ls /opt/Tomcat/webapps/app-web/
    tail -30 /opt/Tomcat/logs/catalina.out