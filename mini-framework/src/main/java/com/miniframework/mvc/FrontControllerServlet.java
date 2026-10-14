package com.miniframework.mvc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import com.miniframework.annotation.JsonResponse;
import com.miniframework.binding.DataBinder;
import com.miniframework.http.HttpMethod;
import com.miniframework.json.JsonRenderer;
import com.miniframework.routing.RouteRegistry;
import com.miniframework.view.ViewResolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet unique (front controller) qui reçoit toutes les requêtes, retrouve la
 * méthode de contrôleur correspondante, lie les paramètres, l'invoque puis
 * produit la réponse (vue JSP ou JSON).
 */
public class FrontControllerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private RouteRegistry routes;
    private ViewResolver viewResolver;
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
        for (Map.Entry<String, Object> entry : modelAndView.getModel().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        String view = viewResolver.resolve(modelAndView.getView());
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
