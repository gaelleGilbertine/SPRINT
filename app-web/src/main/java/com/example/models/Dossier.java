package com.example.models;

/**
 * Objet lié depuis le formulaire du sprint 8 : il contient l'attribut
 * {@link MapFile} que le framework doit détecter.
 */
public class Dossier {

    private String nom;
    private String prenom;
    private MapFile fichier;

    public Dossier() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public MapFile getFichier() { return fichier; }
    public void setFichier(MapFile fichier) { this.fichier = fichier; }
}
