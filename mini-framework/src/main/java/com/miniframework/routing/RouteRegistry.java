package com.miniframework.routing;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.miniframework.http.HttpMethod;

/**
 * Table de routage : associe une {@link RouteKey} à la méthode de contrôleur
 * qui doit la traiter.
 */
public class RouteRegistry {

    /** Nom de l'attribut placé dans le {@code ServletContext}. */
    public static final String CONTEXT_ATTRIBUTE = "com.miniframework.routes";

    private final Map<RouteKey, Method> routes = new HashMap<>();

    /**
     * Enregistre une route.
     *
     * @throws IllegalStateException si la même URL + méthode HTTP est déjà mappée
     */
    public void register(String url, HttpMethod method, Method handler) {
        RouteKey key = new RouteKey(url, method);
        Method previous = routes.putIfAbsent(key, handler);
        if (previous != null) {
            throw new IllegalStateException("Route en double : " + method + " " + url);
        }
    }

    /** @return la méthode associée à la route, ou {@code null} si inconnue */
    public Method resolve(String url, HttpMethod method) {
        return routes.get(new RouteKey(url, method));
    }
}
