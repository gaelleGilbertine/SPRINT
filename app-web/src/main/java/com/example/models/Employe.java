package com.example.models;

public class Employe {

    private final int id;
    private final String nom;
    private final String poste;
    private final String service;

    public Employe(int id, String nom, String poste, String service) {
        this.id = id;
        this.nom = nom;
        this.poste = poste;
        this.service = service;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPoste() { return poste; }
    public String getService() { return service; }
}