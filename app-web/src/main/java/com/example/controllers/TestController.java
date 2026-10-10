package com.example.controllers;

import com.example.models.Personne;

import annotation.Annotation;
import annotation.JsonAnnotation;
import annotation.UrlAnnotation;
import java.util.HashMap;
import model.ModelAndView;

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
    return mv;
}
}