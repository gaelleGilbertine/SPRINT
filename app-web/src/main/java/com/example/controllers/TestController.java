package com.example.controllers;

import com.example.models.Personne;

import annotation.Annotation;
import annotation.JsonAnnotation;
import annotation.UrlAnnotation;
import java.util.HashMap;
import model.ModelAndView;
import java.util.LinkedHashMap;
import model.HTTPmethode;

@Annotation
public class TestController {

    // SORTIE JSON : avec @JsonAnnotation
    @UrlAnnotation("/api/hello")
    @JsonAnnotation
    public HashMap<String, String> helloJson() {
        HashMap<String, String> data = new HashMap<>();
        data.put("titre", "Demo du framework");
        data.put("message", "Bonjour");
        data.put("status", "ok");
        return data;
    }

    // SORTIE HTML : sans @JsonAnnotation,forward vers bienvenue.jsp
    @UrlAnnotation("/page/hello")
    public ModelAndView helloPage() {
        ModelAndView mv = new ModelAndView("bienvenue.jsp");
        mv.addObject("titre", "Demo du framework");
        mv.addObject("message", "Bonjour");
        mv.addObject("status", "ok");
        return mv;
    }

   @UrlAnnotation(value = "/personne/save", httpmethode = HTTPmethode.POST)
   @JsonAnnotation
public HashMap<String, String>savePost(String nom, int age, String prenom) {
    return traiter(nom, age, prenom);
}

@UrlAnnotation("/personne/save")
@JsonAnnotation
public HashMap<String, String> saveGet(String nom, int age, String prenom) {
    return traiter(nom, age, prenom);
}

private HashMap<String, String> traiter(String nom, int age, String prenom) {
    HashMap<String, String> data = new HashMap<>();
    data.put("nom", nom);
    data.put("age", String.valueOf(age));
    data.put("prenom", prenom);
    return data;
}

@UrlAnnotation(value = "/personne/objet", httpmethode = HTTPmethode.POST)
public ModelAndView objetPost(Personne p) {
    return traiterObjet(p);
}

@UrlAnnotation("/personne/objet")
public ModelAndView objetGet(Personne p) {
    return traiterObjet(p);
}

private ModelAndView traiterObjet(Personne p) {
    ModelAndView mv = new ModelAndView("resultat.jsp");
    mv.addObject("nom", p.getNom());
    mv.addObject("age", String.valueOf(p.getAge()));
    mv.addObject("prenom", p.getPrenom());
    // Données du niveau 2 (objet imbriqué)
    mv.addObject("rue", p.getAdresse().getRue());
    mv.addObject("ville", p.getAdresse().getVille());
    mv.addObject("codePostal", String.valueOf(p.getAdresse().getCodePostal()));
    return mv;
}

@UrlAnnotation("/api/personne")
@JsonAnnotation
public Personne personneJsonGet(Personne p) {
    return p;
}

@UrlAnnotation(value = "/api/personne", httpmethode = HTTPmethode.POST)
@JsonAnnotation
public Personne personneJsonPost(Personne p) {
    return p;
}

@UrlAnnotation("/api/comparer")
@JsonAnnotation
public HashMap<String, Object> comparerGet(Personne p1, Personne p2) {
    return comparer(p1, p2);
}

@UrlAnnotation(value = "/api/comparer", httpmethode = HTTPmethode.POST)
@JsonAnnotation
public HashMap<String, Object> comparerPost(Personne p1, Personne p2) {
    return comparer(p1, p2);
}

private HashMap<String, Object> comparer(Personne p1, Personne p2) {
    HashMap<String, Object> r = new LinkedHashMap<>();
    r.put("p1", p1);
    r.put("p2", p2);
    r.put("memeInstance", p1 == p2);   // false = deux objets distincts
    return r;
}

// Type simple + objet
@UrlAnnotation("/api/mixte")
@JsonAnnotation
public HashMap<String, Object> mixteGet(String titre, int quantite, Personne p) {
    return mixte(titre, quantite, p);
}

@UrlAnnotation(value = "/api/mixte", httpmethode = HTTPmethode.POST)
@JsonAnnotation
public HashMap<String, Object> mixtePost(String titre, int quantite, Personne p) {
    return mixte(titre, quantite, p);
}

private HashMap<String, Object> mixte(String titre, int quantite, Personne p) {
    HashMap<String, Object> r = new LinkedHashMap<>();
    r.put("titre", titre);
    r.put("quantite", quantite);
    r.put("personne", p);
    return r;
}

// Cas du conflit de noms
@UrlAnnotation(value = "/api/conflit", httpmethode = HTTPmethode.POST)
@JsonAnnotation
public HashMap<String, Object> conflitPost(String nom, Personne p) {
    HashMap<String, Object> r = new LinkedHashMap<>();
    r.put("nomSimple", nom);
    r.put("personne", p);
    return r;
}
}