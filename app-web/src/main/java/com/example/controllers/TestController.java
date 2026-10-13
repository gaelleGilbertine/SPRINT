package com.example.controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.example.models.Personne;

import annotation.Annotation;
import annotation.JsonAnnotation;
import annotation.RequestParam;
import annotation.UrlAnnotation;
import model.HTTPmethode;
import model.ModelAndView;

@Annotation
public class TestController {

    // =====================================================================
    //  SPRINT 6 : JSON et HTML
    // =====================================================================

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

    // SORTIE HTML : sans @JsonAnnotation, forward vers bienvenue.jsp
    @UrlAnnotation("/page/hello")
    public ModelAndView helloPage() {
        ModelAndView mv = new ModelAndView("bienvenue.jsp");
        mv.addObject("titre", "Demo du framework");
        mv.addObject("message", "Bonjour");
        mv.addObject("status", "ok");
        return mv;
    }

    // =====================================================================
    //  SPRINT 7 : paramètres simples
    // =====================================================================

    @UrlAnnotation(value = "/personne/save", httpmethode = HTTPmethode.POST)
    public ModelAndView savePost(String nom, int age, String prenom) {
        return traiter(nom, age, prenom);
    }

    @UrlAnnotation("/personne/save")
    public ModelAndView saveGet(String nom, int age, String prenom) {
        return traiter(nom, age, prenom);
    }

    private ModelAndView traiter(String nom, int age, String prenom) {
        ModelAndView mv = new ModelAndView("resultat.jsp");
        mv.addObject("nom", nom);
        mv.addObject("age", String.valueOf(age));
        mv.addObject("prenom", prenom);
        return mv;
    }

    // =====================================================================
    //  SPRINT 7 BIS : un objet
    // =====================================================================

    @UrlAnnotation(value = "/personne/objet", httpmethode = HTTPmethode.POST)
    public ModelAndView objetPost(Personne p) {
        return traiterObjet(p);
    }

    @UrlAnnotation("/personne/objet")
    public ModelAndView objetGet(Personne p) {
        return traiterObjet(p);
    }

    private ModelAndView traiterObjet(Personne p) {
        System.out.println("Objet reçu : " + p.getClass().getName());
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

    // Preuve : l'objet est renvoyé en JSON (récursion visible : adresse imbriquée)
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

    // =====================================================================
    //  SPRINT 7 BIS : plusieurs objets du même type (sans @RequestParam)
    //  Formulaire : p1.nom, p2.nom ...
    // =====================================================================

    @UrlAnnotation("/api/comparer")
    @JsonAnnotation
    public HashMap<String, Object> comparerGet(Personne p1, Personne p2) {
        return multi(p1, p2);
    }

    @UrlAnnotation(value = "/api/comparer", httpmethode = HTTPmethode.POST)
    @JsonAnnotation
    public HashMap<String, Object> comparerPost(Personne p1, Personne p2) {
        return multi(p1, p2);
    }

    // =====================================================================
    //  SPRINT 7 BIS : type simple + objet (sans @RequestParam)
    //  Formulaire : titre, quantite, p.nom ...
    // =====================================================================

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

    // Conflit de noms : "nom" simple vs "p.nom" de l'objet
    @UrlAnnotation(value = "/api/conflit", httpmethode = HTTPmethode.POST)
    @JsonAnnotation
    public HashMap<String, Object> conflitPost(String nom, Personne p) {
        HashMap<String, Object> r = new LinkedHashMap<>();
        r.put("nomSimple", nom);
        r.put("personne", p);
        return r;
    }

    // =====================================================================
    //  SPRINT 7 BIS AVEC @RequestParam
    // =====================================================================

    // ----- CAS 1 : récursion (Personne contient Adresse) -----
    // Formulaire : pers.nom, pers.age, pers.adresse.ville ...
    @UrlAnnotation("/api/rp/recursion")
    @JsonAnnotation
    public Personne rpRecursionGet(@RequestParam("pers") Personne p) {
        return p;
    }

    @UrlAnnotation(value = "/api/rp/recursion", httpmethode = HTTPmethode.POST)
    @JsonAnnotation
    public Personne rpRecursionPost(@RequestParam("pers") Personne p) {
        return p;
    }

    // ----- CAS 2 : plusieurs objets du même type -----
    // Formulaire : a.nom, a.adresse.ville, b.nom, b.adresse.ville ...
    @UrlAnnotation("/api/rp/multi")
    @JsonAnnotation
    public HashMap<String, Object> rpMultiGet(@RequestParam("a") Personne p1,
                                              @RequestParam("b") Personne p2) {
        return multi(p1, p2);
    }

    @UrlAnnotation(value = "/api/rp/multi", httpmethode = HTTPmethode.POST)
    @JsonAnnotation
    public HashMap<String, Object> rpMultiPost(@RequestParam("a") Personne p1,
                                               @RequestParam("b") Personne p2) {
        return multi(p1, p2);
    }

    // ----- CAS 3 : type simple + objet -----
    // Formulaire : t, q, pers.nom, pers.adresse.ville ...
    @UrlAnnotation("/api/rp/mixte")
    @JsonAnnotation
    public HashMap<String, Object> rpMixteGet(@RequestParam("t") String titre,
                                              @RequestParam("q") int quantite,
                                              @RequestParam("pers") Personne p) {
        return mixte(titre, quantite, p);
    }

    @UrlAnnotation(value = "/api/rp/mixte", httpmethode = HTTPmethode.POST)
    @JsonAnnotation
    public HashMap<String, Object> rpMixtePost(@RequestParam("t") String titre,
                                               @RequestParam("q") int quantite,
                                               @RequestParam("pers") Personne p) {
        return mixte(titre, quantite, p);
    }

    // =====================================================================
    //  Méthodes utilitaires (partagées)
    // =====================================================================

    private HashMap<String, Object> multi(Personne p1, Personne p2) {
        HashMap<String, Object> r = new LinkedHashMap<>();
        r.put("p1", p1);
        r.put("p2", p2);
        r.put("memeInstance", p1 == p2); // false = deux objets distincts
        return r;
    }

    private HashMap<String, Object> mixte(String titre, int quantite, Personne p) {
        HashMap<String, Object> r = new LinkedHashMap<>();
        r.put("titre", titre);
        r.put("quantite", quantite);
        r.put("personne", p);
        return r;
    }
}