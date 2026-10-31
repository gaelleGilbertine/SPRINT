package com.miniframework.session;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class Session {

    public static final String AUTH_USER_KEY = "auth.user";
    public static final String AUTH_ROLES_KEY = "auth.roles";
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

    /** Marque la personne comme connectée, sans aucun rôle. */
    public void login(Object user) {
        login(user, Collections.<String>emptySet());
    }

    /** Marque la personne comme connectée avec les rôles donnés (sprint 10 bis). */
    public void login(Object user, String... roles) {
        login(user, roles == null ? Collections.<String>emptySet() : java.util.Arrays.asList(roles));
    }

    /**
     * Marque la personne comme connectée avec les rôles donnés et renouvelle l'id de
     * session (anti-fixation). Les rôles null ou vides sont ignorés.
     */
    public void login(Object user, Collection<String> roles) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur à connecter est null");
        }
        Set<String> cleaned = new LinkedHashSet<>();
        if (roles != null) {
            for (String role : roles) {
                if (role != null && !role.isBlank()) {
                    cleaned.add(role.trim());
                }
            }
        }
        if (request != null) {
            request.changeSessionId();
        }
        httpSession.setAttribute(AUTH_USER_KEY, user);
        httpSession.setAttribute(AUTH_ROLES_KEY, Collections.unmodifiableSet(cleaned));
    }

    public void logout() { httpSession.invalidate(); }

    public boolean isLoggedIn() { return httpSession.getAttribute(AUTH_USER_KEY) != null; }

    public Object getUser() { return httpSession.getAttribute(AUTH_USER_KEY); }

    public <T> T getUser(Class<T> type) { return type.cast(httpSession.getAttribute(AUTH_USER_KEY)); }

    // ---------- Sprint 10 bis : rôles ----------

    /** Rôles de la personne connectée (ensemble vide si personne n'est connecté). */
    public Set<String> getRoles() {
        return rolesOf(httpSession);
    }

    public boolean hasRole(String role) { return getRoles().contains(role); }

    /** Vrai si la personne possède au moins un des rôles donnés. */
    public boolean hasAnyRole(String... roles) {
        Set<String> owned = getRoles();
        for (String role : roles) {
            if (owned.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /** Rôles stockés dans une session HTTP (jamais null). */
    @SuppressWarnings("unchecked")
    public static Set<String> rolesOf(HttpSession httpSession) {
        if (httpSession == null || httpSession.getAttribute(AUTH_USER_KEY) == null) {
            return Collections.emptySet();
        }
        Object roles = httpSession.getAttribute(AUTH_ROLES_KEY);
        return roles instanceof Set<?> set ? (Set<String>) set : Collections.<String>emptySet();
    }

    /** Récupère puis oublie la page protégée demandée avant la connexion. */
    public String takeRequestedUrl() {
        Object url = httpSession.getAttribute(REQUESTED_URL_KEY);
        httpSession.removeAttribute(REQUESTED_URL_KEY);
        return url instanceof String s ? s : null;
    }
}