package com.miniframework.security;

import java.lang.reflect.Method;

import com.miniframework.annotation.Authenticated;
import com.miniframework.session.Session;

import jakarta.servlet.http.HttpSession;

public final class AuthGuard {

    private AuthGuard() {
    }

    /** La méthode (ou sa classe) porte-t-elle @Authenticated ? */
    public static boolean isProtected(Method handler) {
        return handler.isAnnotationPresent(Authenticated.class)
                || handler.getDeclaringClass().isAnnotationPresent(Authenticated.class);
    }

    /** session = null s'il n'y a pas de session (request.getSession(false)) */
    public static boolean isAuthenticated(HttpSession session) {
        return session != null && session.getAttribute(Session.AUTH_USER_KEY) != null;
    }
}