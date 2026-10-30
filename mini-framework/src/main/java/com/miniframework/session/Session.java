package com.miniframework.session;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class Session {

    public static final String AUTH_USER_KEY = "auth.user";
    public static final String REQUESTED_URL_KEY = "auth.requestedUrl";

    private final HttpSession httpSession;
    private final HttpServletRequest request; // null si créée à partir d'une HttpSession

    public Session(HttpSession httpSession) {
        this.httpSession = httpSession;
        this.request = null;
    }

    public Session(HttpServletRequest request) {
        this.request = request;
        this.httpSession = request.getSession();
    }

    // ---------- Sprint 9 (inchangé) ----------

    public Object get(String key) { return httpSession.getAttribute(key); }

    public <T> T get(String key, Class<T> type) { return type.cast(httpSession.getAttribute(key)); }

    public void set(String key, Object value) { httpSession.setAttribute(key, value); }

    public void remove(String key) { httpSession.removeAttribute(key); }

    public boolean contains(String key) { return httpSession.getAttribute(key) != null; }

    public Set<String> keys() {
        return Collections.unmodifiableSet(
                new LinkedHashSet<>(Collections.list(httpSession.getAttributeNames())));
    }

    public String getId() { return httpSession.getId(); }

    public void invalidate() { httpSession.invalidate(); }

    // ---------- Sprint 10 : authentification (nouveau) ----------

    /** Marque la personne comme connectée et renouvelle l'id de session (anti-fixation). */
    public void login(Object user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur à connecter est null");
        }
        if (request != null) {
            request.changeSessionId();
        }
        httpSession.setAttribute(AUTH_USER_KEY, user);
    }

    public void logout() { httpSession.invalidate(); }

    public boolean isLoggedIn() { return httpSession.getAttribute(AUTH_USER_KEY) != null; }

    public Object getUser() { return httpSession.getAttribute(AUTH_USER_KEY); }

    public <T> T getUser(Class<T> type) { return type.cast(httpSession.getAttribute(AUTH_USER_KEY)); }

    /** Récupère puis oublie la page protégée demandée avant la connexion. */
    public String takeRequestedUrl() {
        Object url = httpSession.getAttribute(REQUESTED_URL_KEY);
        httpSession.removeAttribute(REQUESTED_URL_KEY);
        return url instanceof String s ? s : null;
    }
}