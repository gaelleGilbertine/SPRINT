# Signification des mots

## Data binding : liaison des données
- Faire le lien entre les champs d'un formulaire (côté front) et les paramètres
  reçus par le contrôleur (côté back).
- Implémenté par `com.miniframework.binding.DataBinder`.

# Réflexion Java (utilisée par le framework)

## import java.lang.reflect.Parameter;
- Représente un paramètre d'une méthode ou d'un constructeur.
- `parameter.getType()` donne son type, `parameter.getName()` son nom.
- Utilisé dans `DataBinder.bind(...)` : `Parameter[] parameters = method.getParameters();`

## import java.lang.reflect.Field;
- Représente un attribut (champ) d'une classe.
- Permet d'obtenir des informations sur cet attribut et, sous certaines
  conditions, de lire ou d'écrire sa valeur (`field.set(instance, valeur)`).
- Utilisé dans `DataBinder.bindObject(...)` pour remplir l'objet depuis la requête.

## import java.lang.reflect.Modifier;
- Permet d'identifier les modificateurs d'un attribut, d'une méthode ou d'une
  classe : public, private, static, final, etc.
- Exemple : `if (Modifier.isStatic(field.getModifiers())) continue;`
  (on ignore les champs statiques lors du binding).

## import java.lang.reflect.Method;
- Représente une méthode : `method.invoke(instance, args)` l'exécute dynamiquement.

# Points clés du framework

## request.setCharacterEncoding("UTF-8");
- Permet de récupérer correctement les données envoyées par le formulaire.
- ➡️ request = la requête envoyée par le navigateur
- ➡️ setCharacterEncoding = définir comment lire les caractères
- ➡️ "UTF-8" = encodage utilisé
- Avant de récupérer les paramètres texte, on indique à la requête qu'elle doit
  les lire en UTF-8.

## Object[] arguments = new DataBinder(request).bind(handler);
- C'est un tableau d'objets Java appelé `arguments`.
- `Object[]` car la méthode peut attendre des types différents :
  `maMethode(String nom, int age, boolean actif)`.
- `bind(handler)` : prépare les arguments à partir des paramètres de la requête.

## Object resultat = handler.invoke(controller, arguments);
- `invoke` exécute dynamiquement la méthode du contrôleur (représentée par
  `handler`) avec les arguments préparés, puis renvoie son résultat.

## Class<?> type = parameters[i].getType();
- Récupère le type attendu du paramètre. On le passe à `isSimple(type)` pour
  savoir comment le lier.

## Comment sait-on « type simple » ou « objet » ?
- `DataBinder.isSimple(type)` renvoie vrai pour un type primitif, `String`,
  `Boolean` ou un `Number`.
- Sinon, c'est un objet à construire par réflexion, de façon **récursive** pour
  les champs imbriqués (par exemple `adresse.ville`).
- Un seul paramètre objet par méthode peut être lié **sans préfixe** ; dès qu'il
  y a plusieurs objets, `@RequestParam` impose le préfixe (`pers.nom`,
  `a.nom`, `b.nom`, ...).

# Structure du projet

Voir le `README.md` à la racine pour l'arborescence complète. En résumé :

- `com.miniframework.annotation` : `@Controller`, `@Url`, `@JsonResponse`, `@RequestParam`
- `com.miniframework.routing` : `RouteKey`, `RouteRegistry`, `ControllerScanner`
- `com.miniframework.binding` : `DataBinder`, `TypeConverter`
- `com.miniframework.view` / `json` : `ViewResolver`, `JsonRenderer`
- `com.miniframework.mvc` : `FrontControllerServlet`, `ModelAndView`
