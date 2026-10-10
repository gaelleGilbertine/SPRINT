# SIGNIFICATION DES MOTS 
## Data binding : liaison des donnees
-  faire le lien entre les champs du formulaire(cote front) et les parametres recus par le controller(cote back)

# UTILISATION
## import java.lang.reflect.Parameter;
- accorde la possibilite de recuperer des parametres d'une methode ou d'un constructeur

## import java.lang.reflect.Modifier;
- Field représente un attribut d'une classe. Il permet d'obtenir des informations sur cet attribut et, sous certaines conditions, de lire ou de modifier sa valeur.

## import java.lang.reflect.Field;
- Modifier permet d'identifier les modificateurs d'un attribut, d'une méthode ou d'une classe : public, private, static, final, etc.

## request.setCharacterEncoding("UTF-8");
- permet de recuperer des donnees envoyer par le formulaire
- ➡️ request = la requête envoyée par le navigateur
  ➡️ setCharacterEncoding = définir comment lire les caractères
  ➡️ "UTF-8" = encodage utilisé
- Avant de récupérer les paramètres texte envoyés par le formulaire, on indique à la requête qu'elle doit les lire en UTF-8.

## Object[] args = construireArguments(m, request);
- c'est un tableau d'objt java appeler args
- object[] car la methode peut etre de differente type
    + exemple : maMethode(String nom, int age, boolean actif)
- construireArguments(m, request)
    + m : la methode
    + request : les donnees envoyer par le formulaire
- prepare les argument a donees a la methode 
 
## Object resultat = m.invoke(instance, args);
- m.invoke : pour executer dynamiquement la methode represente par m 
- appele la methode avec ces arguments 