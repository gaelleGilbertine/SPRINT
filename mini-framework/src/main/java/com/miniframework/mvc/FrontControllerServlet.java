package com.miniframework.mvc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import com.miniframework.annotation.JsonResponse;
import com.miniframework.binding.DataBinder;
import com.miniframework.http.HttpMethod;
import com.miniframework.json.JsonRenderer;
import com.miniframework.routing.RouteRegistry;
import com.miniframework.security.AuthGuard;
import com.miniframework.session.Session;
import com.miniframework.view.ViewResolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet unique (front controller) qui reçoit toutes les requêtes, retrouve la
 * méthode de contrôleur correspondante, lie les paramètres, l'invoque puis
 * produit la réponse (vue JSP ou JSON).
 *
 * <p>{@code @MultipartConfig} est indispensable (sprint 8) : sans lui,
 * {@code request.getPart()} — utilisé par {@code DataBinder} pour lire les
 * {@code byte[]} d'une {@code MapFile} — lève une {@link IllegalStateException}.</p>
 */
@MultipartConfig(
        maxFileSize = 10L * 1024 * 1024,      // 10 Mo par fichier
        maxRequestSize = 25L * 1024 * 1024,   // 25 Mo par requête
        fileSizeThreshold = 1024 * 1024       // au-delà de 1 Mo : stocké sur disque
)
public class FrontControllerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Préfixe d'un nom de vue pour demander une redirection HTTP plutôt qu'un forward JSP. */
    public static final String REDIRECT_PREFIX = "redirect:";
    private static final String DEFAULT_LOGIN_URL = "/login";

    private RouteRegistry routes;
    private ViewResolver viewResolver;
    private String loginUrl;
    private String accessDeniedView;
    private String forbiddenView;
    private final JsonRenderer jsonRenderer = new JsonRenderer();

    @Override
    public void init() throws ServletException {
        Object registry = getServletContext().getAttribute(RouteRegistry.CONTEXT_ATTRIBUTE);
        if (!(registry instanceof RouteRegistry)) {
            throw new ServletException(
                    "Table de routage introuvable. Vérifiez le context-param 'controller-package' "
                            + "et le listener AppContextListener.");
        }
        this.routes = (RouteRegistry) registry;
        this.viewResolver = new ViewResolver(
                getServletContext().getInitParameter("view-prefix"),
                getServletContext().getInitParameter("view-suffix"));
        String configuredLoginUrl = getServletContext().getInitParameter("login-url");
        this.loginUrl = (configuredLoginUrl == null || configuredLoginUrl.isBlank())
                ? DEFAULT_LOGIN_URL
                : configuredLoginUrl;
        String configuredDeniedView = getServletContext().getInitParameter("access-denied-view");
        this.accessDeniedView = (configuredDeniedView == null || configuredDeniedView.isBlank())
                ? null
                : configuredDeniedView;
        String configuredForbiddenView = getServletContext().getInitParameter("forbidden-view");
        this.forbiddenView = (configuredForbiddenView == null || configuredForbiddenView.isBlank())
                ? null
                : configuredForbiddenView;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String route = requestedRoute(request);
        HttpMethod httpMethod = toHttpMethod(request.getMethod());
        if (httpMethod == null) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Méthode HTTP non supportée : " + request.getMethod());
            return;
        }

        Method handler = routes.resolve(route, httpMethod);
        if (handler == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Aucune route pour " + httpMethod + " " + route);
            return;
        }

        // Sprint 10 / 10 bis : une méthode @Authenticated n'est exécutée que si la personne
        // est connectée et, si des rôles sont exigés, possède au moins l'un d'eux.
        // getSession(false) : on ne crée pas de session juste pour constater qu'il n'y en a pas.
        if (AuthGuard.isProtected(handler)) {
            switch (AuthGuard.check(handler, request.getSession(false))) {
                case UNAUTHENTICATED -> {
                    denyAccess(request, response, handler, httpMethod, route);
                    return;
                }
                case FORBIDDEN -> {
                    denyForbidden(request, response, handler);
                    return;
                }
                case ALLOWED -> {
                    // évite qu'après déconnexion le bouton « Retour » réaffiche la page protégée
                    response.setHeader("Cache-Control", "no-store");
                }
            }
        }

        try {
            Object controller = handler.getDeclaringClass().getDeclaredConstructor().newInstance();
            Object[] arguments = new DataBinder(request).bind(handler);
            Object result = handler.invoke(controller, arguments);

            if (handler.isAnnotationPresent(JsonResponse.class)) {
                renderJson(response, result);
            } else {
                renderView(request, response, result);
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors du traitement de " + httpMethod + " " + route, e);
        }
    }

    /**
     * Refuse l'accès à une route protégée : 401 + JSON pour une API, sinon
     * redirection vers la page de connexion.
     */
    private void denyAccess(HttpServletRequest request, HttpServletResponse response,
                            Method handler, HttpMethod httpMethod, String route)
            throws ServletException, IOException {
        if (handler.isAnnotationPresent(JsonResponse.class)) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Authentification requise");
            error.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(jsonRenderer.toJson(error));
            return;
        }
        if (httpMethod == HttpMethod.GET) {
            String query = request.getQueryString();
            request.getSession().setAttribute(Session.REQUESTED_URL_KEY,
                    query == null ? route : route + "?" + query);
        }

        String message = "Vous devez être connecté pour accéder à cette page.";
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (accessDeniedView == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
            return;
        }
        request.setAttribute("message", message);
        request.setAttribute("loginUrl", request.getContextPath() + loginUrl);
        request.getRequestDispatcher(viewResolver.resolve(accessDeniedView)).forward(request, response);
    }

    /**
     * Personne connectée mais sans le rôle exigé : 403 + JSON pour une API, sinon la vue
     * {@code forbidden-view} (context-param) ou, à défaut, la page d'erreur 403 du conteneur.
     * On ne mémorise pas l'URL demandée : se reconnecter avec le même compte ne changerait rien.
     */
    private void denyForbidden(HttpServletRequest request, HttpServletResponse response, Method handler)
            throws ServletException, IOException {
        String message = "Accès refusé : vous n'avez pas le rôle requis pour cette page.";
        if (handler.isAnnotationPresent(JsonResponse.class)) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Accès interdit : rôle insuffisant");
            error.put("status", HttpServletResponse.SC_FORBIDDEN);
            error.put("rolesRequis", AuthGuard.requiredRoles(handler));
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(jsonRenderer.toJson(error));
            return;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        if (forbiddenView == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
            return;
        }
        request.setAttribute("message", message);
        request.setAttribute("rolesRequis", AuthGuard.requiredRoles(handler));
        request.getRequestDispatcher(viewResolver.resolve(forbiddenView)).forward(request, response);
    }

    private void renderJson(HttpServletResponse response, Object result) throws IOException {
        Object data = (result instanceof ModelAndView modelAndView)
                ? modelAndView.getModel()
                : result;
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(jsonRenderer.toJson(data));
    }

    private void renderView(HttpServletRequest request, HttpServletResponse response, Object result)
            throws ServletException, IOException {
        if (!(result instanceof ModelAndView modelAndView)) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Le contrôleur n'a retourné aucune vue à afficher.");
            return;
        }
        String viewName = modelAndView.getView();
        if (viewName != null && viewName.startsWith(REDIRECT_PREFIX)) {
            // Chemin relatif au contexte : "redirect:/login" -> /app-web/login
            response.sendRedirect(request.getContextPath() + viewName.substring(REDIRECT_PREFIX.length()));
            return;
        }

        for (Map.Entry<String, Object> entry : modelAndView.getModel().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        String view = viewResolver.resolve(viewName);
        request.getRequestDispatcher(view).forward(request, response);
    }

    /** Chemin demandé, relatif au contexte (par exemple {@code /personne/save}). */
    private String requestedRoute(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String route = request.getRequestURI().substring(contextPath.length());
        return route.isEmpty() ? "/" : route;
    }

    private HttpMethod toHttpMethod(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}