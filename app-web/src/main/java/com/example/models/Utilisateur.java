package com.example.models;

public class Utilisateur {

    private final String login;
    private final String nom;

    public Utilisateur(String login, String nom) {
        this.login = login;
        this.nom = nom;
    }

    public String getLogin() { return login; }
    public String getNom() { return nom; }
}