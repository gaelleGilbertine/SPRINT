package com.miniframework.security;

import java.lang.reflect.Method;
import java.util.Set;

import com.miniframework.annotation.Authenticated;
import com.miniframework.session.Session;

import jakarta.servlet.http.HttpSession;

public final class AuthGuard {

    /** Résultat du contrôle d'accès à un handler. */
    public enum Decision {
        /** Aucune restriction, ou restriction satisfaite. */
        ALLOWED,
        /** Route protégée et personne non connectée → 401. */
        UNAUTHENTICATED,
        /** Personne connectée mais sans aucun des rôles exigés → 403. */
        FORBIDDEN
    }

    private AuthGuard() {
    }

    /**
     * L'annotation qui s'applique au handler : celle de la méthode si elle existe
     * (elle remplace celle de la classe), sinon celle de la classe, sinon {@code null}.
     */
    public static Authenticated requirement(Method handler) {
        Authenticated onMethod = handler.getAnnotation(Authenticated.class);
        return onMethod != null ? onMethod : handler.getDeclaringClass().getAnnotation(Authenticated.class);
    }

    /** La méthode (ou sa classe) porte-t-elle @Authenticated ? */
    public static boolean isProtected(Method handler) {
        return requirement(handler) != null;
    }

    /** Rôles exigés par le handler (tableau vide : être connecté suffit). */
    public static String[] requiredRoles(Method handler) {
        Authenticated requirement = requirement(handler);
        return requirement == null ? new String[0] : requirement.roles();
    }

    /** session = null s'il n'y a pas de session (request.getSession(false)) */
    public static boolean isAuthenticated(HttpSession session) {
        return session != null && session.getAttribute(Session.AUTH_USER_KEY) != null;
    }

    /** La personne possède-t-elle au moins un des rôles donnés ? Aucun rôle exigé → oui. */
    public static boolean hasAnyRole(HttpSession session, String[] required) {
        if (required.length == 0) {
            return true;
        }
        Set<String> owned = Session.rolesOf(session);
        for (String role : required) {
            if (owned.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /** Décide si la session peut exécuter le handler. */
    public static Decision check(Method handler, HttpSession session) {
        if (!isProtected(handler)) {
            return Decision.ALLOWED;
        }
        if (!isAuthenticated(session)) {
            return Decision.UNAUTHENTICATED;
        }
        return hasAnyRole(session, requiredRoles(handler)) ? Decision.ALLOWED : Decision.FORBIDDEN;
    }
}