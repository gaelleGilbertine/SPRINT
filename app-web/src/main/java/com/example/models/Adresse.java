package com.example.models;

public class Adresse {
    private String nomRue;
    private String ville;
    private int codePostal;
    private Rue rue;

    public Adresse() {}

    public String getNomRue() { return nomRue; }
    public void setNomRue(String nomRue) { this.nomRue = nomRue; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public int getCodePostal() { return codePostal; }
    public void setCodePostal(int codePostal) { this.codePostal = codePostal; }

    public Rue getRue() { return rue; }
    public void setRue(Rue rue) { this.rue = rue; }
}
