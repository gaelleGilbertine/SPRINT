package com.example.models;

public class Adresse {
    private String rue;
    private String ville;
    private int codePostal;

    public Adresse() {}

    public String getRue() { return rue; }
    public void setRue(String rue) { this.rue = rue; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public int getCodePostal() { return codePostal; }
    public void setCodePostal(int codePostal) { this.codePostal = codePostal; }
}