package controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import annotation.JsonAnnotation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.HTTPmethode;
import model.MethodeInfo;
import model.ModelAndView;
import model.UrlInfo;

import java.lang.reflect.Parameter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FrontControllerServlet extends HttpServlet {
    private HashMap<UrlInfo, MethodeInfo> mapping = new HashMap<>();
    private String prefixe = "";
    private String suffixe = "";
    private final Gson gson = new Gson();

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        this.mapping = (HashMap<UrlInfo, MethodeInfo>) getServletContext().getAttribute("mapping");
        this.prefixe = (String) getServletContext().getInitParameter("view-prefix");
        this.suffixe = (String) getServletContext().getInitParameter("view-suffix");
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String contextPath = request.getContextPath();
        String route = request.getRequestURI().substring(contextPath.length());
        if (route.equals("")) {
            route = "/";
        }

        UrlInfo recherche = new UrlInfo();
        recherche.setUrl(route);
        recherche.setAction(HTTPmethode.valueOf(request.getMethod()));

        MethodeInfo method = mapping.get(recherche);

        // Route inconnue : affichage HTML de debug
        if (method == null) {
            response.setContentType("text/html;charset=UTF-8");
            for (Map.Entry<UrlInfo, MethodeInfo> entry : mapping.entrySet()) {
                UrlInfo url = entry.getKey();
                MethodeInfo m = entry.getValue();
                response.getWriter().println("<p>URL : " + url.getUrl()
                        + " ,  Méthode : " + m.getMethode() + "  , Action :" + url.getAction());
            }
            return;
        }

        try {
            Method m = method.getMethode();
            Object instance = m.getDeclaringClass().getDeclaredConstructor().newInstance();
            Object[] args = construireArguments(m, request);
            Object resultat = m.invoke(instance, args);

            // Cas 1 : @JsonAnnotation -> JSON direct, sans dispatcher
            if (m.isAnnotationPresent(JsonAnnotation.class)) {
                response.setContentType("application/json;charset=UTF-8");
                Object data = (resultat instanceof ModelAndView)
                        ? ((ModelAndView) resultat).getList()
                        : resultat;
                response.getWriter().print(gson.toJson(data));
                return;
            }

            // Cas 2 : comportement d'avant (HTML + forward)
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h2>URL trouvée</h2>");
            response.getWriter().println("<p>Route : " + route + "</p>");
            response.getWriter().println("<p>Méthode appelée : " + m + "</p>");
            if (resultat != null) {
                response.getWriter().println("<p>Résultat : " + resultat + "</p>");
            }
            if (resultat instanceof ModelAndView) {
                ModelAndView mv = (ModelAndView) resultat;
                String urlSuivant = prefixe + mv.getUrlSuivant() + suffixe;
                request.setAttribute("prefixe", prefixe);
                for (Map.Entry<String, String> entry : mv.getList().entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                request.getRequestDispatcher(urlSuivant).forward(request, response);
            }

        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'invocation de la méthode", e);
        }
    }

    private Object[] construireArguments(Method m, HttpServletRequest request) throws Exception {
    Parameter[] params = m.getParameters();
    Object[] args = new Object[params.length];
    for (int i = 0; i < params.length; i++) {
        Class<?> type = params[i].getType();
        if (estSimple(type)) {
            // Sprint 7 : paramètre simple
            args[i] = convertir(request.getParameter(params[i].getName()), type);
        } else {
            // Sprint 7 bis : objet
            args[i] = construireObjet(type, params[i].getName(), request);
        }
    }
    return args;
}

private boolean estSimple(Class<?> type) {
    return type.isPrimitive() || type == String.class
            || Number.class.isAssignableFrom(type) || type == Boolean.class;
}

private Object construireObjet(Class<?> type, String nomParam, HttpServletRequest request) throws Exception {
    Object obj = type.getDeclaredConstructor().newInstance();
    for (Field f : type.getDeclaredFields()) {
        if (Modifier.isStatic(f.getModifiers())) continue;
        // accepte "p.nom" (avec le nom du paramètre) ou simplement "nom"
        String valeur = request.getParameter(nomParam + "." + f.getName());
        if (valeur == null) valeur = request.getParameter(f.getName());
        if (valeur == null) continue;
        f.setAccessible(true);
        f.set(obj, convertir(valeur, f.getType()));
    }
    return obj;
}

private Object convertir(String valeur, Class<?> type) {
    if (type == String.class) return valeur;
    if (valeur == null || valeur.isEmpty()) {
        return type.isPrimitive() ? (type == boolean.class ? (Object) false : (Object) 0) : null;
    }
    if (type == int.class || type == Integer.class) return Integer.parseInt(valeur);
    if (type == long.class || type == Long.class) return Long.parseLong(valeur);
    if (type == double.class || type == Double.class) return Double.parseDouble(valeur);
    if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(valeur);
    return valeur;
}
}