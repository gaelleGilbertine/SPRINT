package com.example.models;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Utilisateur {

    private final String login;
    private final String nom;
    private final Set<String> roles;

    public Utilisateur(String login, String nom, String... roles) {
        this.login = login;
        this.nom = nom;
        this.roles = Collections.unmodifiableSet(new LinkedHashSet<>(java.util.Arrays.asList(roles)));
    }

    public String getLogin() { return login; }
    public String getNom() { return nom; }

    /** Rôles de l'utilisateur (ex. ADMIN, RH, EMPLOYE). */
    public Set<String> getRoles() { return roles; }

    public boolean hasRole(String role) { return roles.contains(role); }
}