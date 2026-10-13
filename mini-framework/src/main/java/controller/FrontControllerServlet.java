package controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import annotation.JsonAnnotation;
import annotation.RequestParam;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.HTTPmethode;
import model.MethodeInfo;
import model.ModelAndView;
import model.UrlInfo;

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

            // Cas 2 : HTML + forward
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

    // =====================================================================
    //  BINDING (sprint 7 / 7 bis)
    // =====================================================================

    /**
     * Nom à chercher dans la requête pour un paramètre :
     * - la valeur de @RequestParam si elle est présente,
     * - sinon le nom de la variable Java (nécessite le flag -parameters).
     */
    private String nomParametre(Parameter p) {
        RequestParam rp = p.getAnnotation(RequestParam.class);
        if (rp != null && !rp.value().isEmpty()) {
            return rp.value();
        }
        return p.getName();
    }

    private Object[] construireArguments(Method m, HttpServletRequest request) throws Exception {
        Parameter[] params = m.getParameters();
        Object[] args = new Object[params.length];

        // Combien de paramètres sont des objets ?
        long nbObjets = Arrays.stream(params).filter(p -> !estSimple(p.getType())).count();
        // Un champ sans préfixe n'est permis que s'il n'y a qu'UN seul objet
        boolean sansPrefixeAutorise = (nbObjets == 1);

        // Noms déjà pris par les paramètres simples (réservés)
        Set<String> nomsReserves = new HashSet<>();
        for (Parameter p : params) {
            if (estSimple(p.getType())) {
                nomsReserves.add(nomParametre(p));
            }
        }

        for (int i = 0; i < params.length; i++) {
            Class<?> type = params[i].getType();
            String nom = nomParametre(params[i]);

            if (estSimple(type)) {
                // Type simple : lu directement par son nom
                args[i] = convertir(request.getParameter(nom), type);
            } else {
                // Objet : un paramètre annoté @RequestParam exige le préfixe
                boolean sansPrefixe = sansPrefixeAutorise
                        && params[i].getAnnotation(RequestParam.class) == null;
                args[i] = construireObjet(type, nom, "", sansPrefixe, nomsReserves, request);
            }
        }
        return args;
    }

    private boolean estSimple(Class<?> type) {
        return type.isPrimitive() || type == String.class
                || Number.class.isAssignableFrom(type) || type == Boolean.class;
    }

    /**
     * Construit un objet par réflexion. RÉCURSIF : si un champ est lui-même
     * un objet, la méthode s'appelle pour ce champ.
     *
     * @param nomParam  préfixe (nom du paramètre ou valeur de @RequestParam)
     * @param chemin    chemin du champ dans l'objet ("" puis "adresse", ...)
     */
    private Object construireObjet(Class<?> type, String nomParam, String chemin,
                                   boolean sansPrefixeAutorise, Set<String> nomsReserves,
                                   HttpServletRequest request) throws Exception {
        // Trace de debug (à retirer après la démo)
        System.out.println("RECURSION -> construireObjet(" + type.getSimpleName()
                + ", parametre='" + nomParam + "', chemin='" + chemin + "')");

        Object obj = type.getDeclaredConstructor().newInstance();

        for (Field f : type.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;

            String cheminChamp = chemin.isEmpty() ? f.getName() : chemin + "." + f.getName();
            f.setAccessible(true);

            if (estSimple(f.getType())) {
                // Champ simple : "pers.nom", puis "nom" seulement si autorisé
                String valeur = request.getParameter(nomParam + "." + cheminChamp);
                if (valeur == null && sansPrefixeAutorise && !nomsReserves.contains(cheminChamp)) {
                    valeur = request.getParameter(cheminChamp);
                }
                if (valeur == null) continue;
                f.set(obj, convertir(valeur, f.getType()));

            } else if (!f.getType().getName().startsWith("java.")) {
                // Champ objet : RÉCURSION
                Object enfant = construireObjet(f.getType(), nomParam, cheminChamp,
                                                sansPrefixeAutorise, nomsReserves, request);
                f.set(obj, enfant);
            }
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