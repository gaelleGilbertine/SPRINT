# Les Sprint — mini-framework MVC

Un mini-framework web MVC écrit en Java (Servlets / Jakarta EE), accompagné d'une
application de démonstration. Le projet est découpé en deux modules Maven :

| Module | Rôle |
| ------ | ---- |
| `mini-framework` | Le framework : routage, data binding, rendu (JSP ou JSON). |
| `app-web` | L'application de démonstration (contrôleur, modèles, JSP). |

## Prérequis

- JDK 17+
- Maven 3.8+
- Apache Tomcat 10+ (Jakarta EE 10 / Servlet 6.0)

## Structure

```
Les Sprint/
├── pom.xml                     # POM agrégateur (build des deux modules)
├── mini-framework/             # Module framework (jar)
│   └── src/main/java/com/miniframework/
│       ├── annotation/         # @Controller, @Url, @JsonResponse, @RequestParam
│       ├── http/               # HttpMethod
│       ├── routing/            # RouteKey, RouteRegistry, ControllerScanner
│       ├── binding/            # DataBinder, TypeConverter
│       ├── view/               # ViewResolver
│       ├── json/               # JsonRenderer
│       ├── mvc/                # FrontControllerServlet, ModelAndView
│       └── listener/           # AppContextListener
└── app-web/                    # Module application (war)
    └── src/main/
        ├── java/com/example/
        │   ├── controllers/    # DemoController
        │   └── models/         # Personne, Adresse
        └── webapp/
            ├── index.jsp       # Page d'accueil
            ├── formulaire*.jsp # Formulaires de démonstration
            ├── assets/css/     # Feuille de style
            └── WEB-INF/
                ├── web.xml
                ├── jspf/       # Fragments (header / footer)
                └── views/      # Vues JSP (accessibles uniquement par forward)
```

## Build

Depuis la racine (construit le framework puis l'application) :

```bash
mvn clean install
```

Ou module par module :

```bash
cd mini-framework && mvn clean install
cd ../app-web     && mvn clean package
```

Le livrable de déploiement est `app-web/target/app-web.war`.

## Déploiement (Tomcat)

```bash
rm -rf /opt/Tomcat/webapps/app-web /opt/Tomcat/webapps/app-web.war
cp app-web/target/app-web.war /opt/Tomcat/webapps/
/opt/Tomcat/bin/shutdown.sh
/opt/Tomcat/bin/startup.sh
tail -f /opt/Tomcat/logs/catalina.out
```

## Points d'entrée de la démo

| URL | Description |
| --- | ----------- |
| `/app-web/` | Page d'accueil |
| `/app-web/page/hello` | Réponse HTML (forward vers `WEB-INF/views/bienvenue.jsp`) |
| `/app-web/api/hello` | Réponse JSON (`@JsonResponse`) |
| `/app-web/personne/save` | Binding de paramètres simples (GET / POST) |
| `/app-web/personne/objet` | Binding d'un objet avec champ imbriqué |
| `/app-web/api/comparer` | Binding de plusieurs objets du même type |
| `/app-web/api/mixte` | Binding d'un type simple + d'un objet |
| `/app-web/api/rp/*` | Variantes avec `@RequestParam` |

## Annotations

| Annotation | Cible | Rôle |
| ---------- | ----- | ---- |
| `@Controller` | classe | Marque un contrôleur à scanner au démarrage. |
| `@Url(value, method)` | méthode | Associe une URL et une méthode HTTP à un handler. |
| `@JsonResponse` | méthode | La valeur de retour est sérialisée en JSON. |
| `@RequestParam(value)` | paramètre | Force/personnalise le nom du paramètre HTTP lié. |

> Note : lors de la restructuration, les anciennes annotations ont été renommées
> pour plus de clarté : `@Annotation` → `@Controller`, `@UrlAnnotation` → `@Url`,
> `@JsonAnnotation` → `@JsonResponse`, et l'enum `HTTPmethode` → `HttpMethod`.
