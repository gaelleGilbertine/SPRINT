package com.example.models;

public class Artiste {
    private String nom;
    private int age;
    private Genre genre;
   

    public Artiste() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

}