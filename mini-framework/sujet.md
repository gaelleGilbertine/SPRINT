
[ok] sprint 6 
 refa manao requette tsy manao requette dispacher fa json

[ok] sprint 7 (binding)
- creer un formulaire dans front
- avoir une fonction dans controller
- la fonction recoit les parametres du formulaire

[ok] sprint 7 Bis
- la meme chose que 7 mais recois un objet cette fois ci 
- utiliser la recursion
- afaka mandray objet mitovy maro (arrive a reconnaitre d'ou vient l'objet)
- afaka mandray tsy objet sy objet  

[ok] sprint 8
- creation d'une classe MapFile :
   + Attributs
      * nom fichier
      * type
      * rectoCIN : bytes[]
      * versoCIN : bytes[]
- on verifie si l'objet passe dans formulaire contient l'attribut : MapFile

[ok] sprint 9
la session

[ok] srint 10
- faire une autentification
-  si la personne est connecter  pourra executer une fonction 

[] sprint 10 bis
- s'autentifier a partir de role
- si un utilisateur possede un role il a acces a la liste des employer par exemple

[] verifier le singleton




 DEPLOYEMENT: 
 1. /opt/Tomcat/bin/version.sh | grep "Server version"
 2. /opt/Tomcat/bin/shutdown.sh
 3. cd ~/Documents/L3/"Les Sprint"/mini-framework && mvn clean install
    cd ~/Documents/L3/"Les Sprint"/app-web && mvn clean package
 4. unzip -l ~/Documents/L3/"Les Sprint"/app-web/target/app-web.war | grep jsp

 ou cp target/app-web.war /opt/Tomcat/webapps/

 5. rm -rf /opt/Tomcat/webapps/app-web
    rm -f /opt/Tomcat/webapps/app-web.war
    cp ~/Documents/L3/"Les Sprint"/app-web/target/app-web.war /opt/Tomcat/webapps/         (Si tu as Permission denied, ajoute sudo)
 6. /opt/Tomcat/bin/startup.sh
 7. ls /opt/Tomcat/webapps/app-web/
    tail -30 /opt/Tomcat/logs/catalina.out

CHANGEMENT DE VERSION JAVA:
sudo update-alternatives --config javac

test Get : curl -i "http://localhost:8080/app-web/personne/save?nom=Rakoto&age=25&prenom=Jean"
test Post: curl -i -X POST \
  -d "nom=Rakoto" -d "age=25" -d "prenom=Jean" \
  http://localhost:8080/app-web/personne/save

test recursion : curl -s "http://localhost:8080/app-web/api/personne?nom=Rakoto&age=25&prenom=Jean&adresse.rue=Lot12&adresse.ville=Tana&adresse.codePostal=101"



Artiste:
- nom
- age
- genre {
   - libelle      ca s'affiche pas !!!!!!!!!!!!
}



cd "Les Sprint/app-web"
mvn clean package


/opt/Tomcat/bin/shutdown.sh
rm -rf /opt/Tomcat/webapps/app-web
rm -f  /opt/Tomcat/webapps/app-web.war
cp target/app-web.war /opt/Tomcat/webapps/
/opt/Tomcat/bin/startup.sh


