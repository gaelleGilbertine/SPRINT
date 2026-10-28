package com.miniframework.session;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.servlet.http.HttpSession;

public class Session {

    private final HttpSession httpSession;

    public Session(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public Object get(String key) {
        return httpSession.getAttribute(key);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(httpSession.getAttribute(key));
    }

    public void set(String key, Object value) {
        httpSession.setAttribute(key, value);
    }

    public void remove(String key) {
        httpSession.removeAttribute(key);
    }

    public boolean contains(String key) {
        return httpSession.getAttribute(key) != null;
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(
                new LinkedHashSet<>(Collections.list(httpSession.getAttributeNames())));
    }

    public String getId() {
        return httpSession.getId();
    }

    public void invalidate() {
        httpSession.invalidate();
    }
}