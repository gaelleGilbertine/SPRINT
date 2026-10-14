# mini-framework

Mini-framework MVC maison (Jakarta Servlet 6.0). Il fournit :

- un **front controller** unique (`com.miniframework.mvc.FrontControllerServlet`) ;
- un **scan de contrôleurs** au démarrage (`com.miniframework.routing.ControllerScanner`) ;
- le **data binding** des paramètres HTTP vers les arguments (`com.miniframework.binding.DataBinder`) ;
- le rendu soit d'une **vue JSP** (`ViewResolver`), soit de **JSON** (`JsonRenderer`).

## Utilisation dans une application

1. Déclarer le listener et la servlet dans `WEB-INF/web.xml` :

```xml
<context-param>
    <param-name>controller-package</param-name>
    <param-value>com.example.controllers</param-value>
</context-param>
<context-param>
    <param-name>view-prefix</param-name>
    <param-value>/WEB-INF/views/</param-value>
</context-param>
<context-param>
    <param-name>view-suffix</param-name>
    <param-value></param-value>
</context-param>

<listener>
    <listener-class>com.miniframework.listener.AppContextListener</listener-class>
</listener>
<servlet>
    <servlet-name>front</servlet-name>
    <servlet-class>com.miniframework.mvc.FrontControllerServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>front</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

2. Écrire un contrôleur :

```java
@Controller
public class MonController {

    @Url("/api/hello")
    @JsonResponse
    public Map<String, String> hello() {
        return Map.of("message", "Bonjour");
    }

    @Url("/page/hello")
    public ModelAndView page() {
        return new ModelAndView("bienvenue.jsp").addObject("message", "Bonjour");
    }
}
```

> Le nom des paramètres Java est disponible au runtime grâce au flag de
> compilation `-parameters` (déjà activé dans le POM de l'application).
