package com.example.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.example.models.Personne;
import com.miniframework.annotation.Controller;
import com.miniframework.annotation.JsonResponse;
import com.miniframework.annotation.RequestParam;
import com.miniframework.annotation.Url;
import com.miniframework.http.HttpMethod;
import com.miniframework.mvc.ModelAndView;

/**
 * Contrôleur de démonstration du mini-framework.
 *
 * <p>Il illustre les sprints 6 et 7 : sortie HTML vs JSON, binding de
 * paramètres simples, puis binding d'objets (imbriqués, multiples et mixtes).</p>
 */
@Controller
public class DemoController {

    // =====================================================================
    //  Sprint 6 : sortie JSON et sortie HTML
    // =====================================================================

    @Url("/api/hello")
    @JsonResponse
    public Map<String, String> helloJson() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("titre", "Demo du framework");
        data.put("message", "Bonjour");
        data.put("status", "ok");
        return data;
    }

    @Url("/page/hello")
    public ModelAndView helloPage() {
        return new ModelAndView("bienvenue.jsp")
                .addObject("titre", "Demo du framework")
                .addObject("message", "Bonjour")
                .addObject("status", "ok");
    }

    // =====================================================================
    //  Sprint 7 : paramètres simples
    // =====================================================================

    @Url(value = "/personne/save", method = HttpMethod.POST)
    public ModelAndView savePost(String nom, int age, String prenom) {
        return resultatSimple(nom, age, prenom);
    }

    @Url("/personne/save")
    public ModelAndView saveGet(String nom, int age, String prenom) {
        return resultatSimple(nom, age, prenom);
    }

    private ModelAndView resultatSimple(String nom, int age, String prenom) {
        return new ModelAndView("resultat.jsp")
                .addObject("nom", nom)
                .addObject("age", age)
                .addObject("prenom", prenom);
    }

    // =====================================================================
    //  Sprint 7 bis : un objet (avec champ imbriqué)
    // =====================================================================

    @Url(value = "/personne/objet", method = HttpMethod.POST)
    public ModelAndView objetPost(Personne personne) {
        return resultatObjet(personne);
    }

    @Url("/personne/objet")
    public ModelAndView objetGet(Personne personne) {
        return resultatObjet(personne);
    }

    private ModelAndView resultatObjet(Personne personne) {
        ModelAndView mv = new ModelAndView("done.jsp")
                .addObject("nom", personne.getNom())
                .addObject("age", personne.getAge())
                .addObject("prenom", personne.getPrenom());

        if (personne.getAdresse() != null) {
            mv.addObject("rue", personne.getAdresse().getRue())
              .addObject("ville", personne.getAdresse().getVille())
              .addObject("codePostal", personne.getAdresse().getCodePostal());
        }
        return mv;
    }

    @Url("/api/personne")
    @JsonResponse
    public Personne personneJsonGet(Personne personne) {
        return personne;
    }

    @Url(value = "/api/personne", method = HttpMethod.POST)
    @JsonResponse
    public Personne personneJsonPost(Personne personne) {
        return personne;
    }

    // =====================================================================
    //  Sprint 7 bis : plusieurs objets du même type (sans @RequestParam)
    //  Formulaire : p1.nom, p2.nom ...
    // =====================================================================

    @Url("/api/comparer")
    @JsonResponse
    public Map<String, Object> comparerGet(Personne p1, Personne p2) {
        return comparer(p1, p2);
    }

    @Url(value = "/api/comparer", method = HttpMethod.POST)
    @JsonResponse
    public Map<String, Object> comparerPost(Personne p1, Personne p2) {
        return comparer(p1, p2);
    }

    // =====================================================================
    //  Sprint 7 bis : type simple + objet (sans @RequestParam)
    //  Formulaire : titre, quantite, p.nom ...
    // =====================================================================

    @Url("/api/mixte")
    @JsonResponse
    public Map<String, Object> mixteGet(String titre, int quantite, Personne p) {
        return mixte(titre, quantite, p);
    }

    @Url(value = "/api/mixte", method = HttpMethod.POST)
    @JsonResponse
    public Map<String, Object> mixtePost(String titre, int quantite, Personne p) {
        return mixte(titre, quantite, p);
    }

    // Conflit de noms : "nom" simple vs "p.nom" de l'objet
    @Url(value = "/api/conflit", method = HttpMethod.POST)
    @JsonResponse
    public Map<String, Object> conflitPost(String nom, Personne p) {
        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("nomSimple", nom);
        resultat.put("personne", p);
        return resultat;
    }

    // =====================================================================
    //  Sprint 7 bis avec @RequestParam (préfixes explicites)
    // =====================================================================

    // Cas 1 : récursion (Personne contient Adresse)
    // Formulaire : pers.nom, pers.age, pers.adresse.ville ...
    @Url("/api/rp/recursion")
    @JsonResponse
    public Personne rpRecursionGet(@RequestParam("pers") Personne personne) {
        return personne;
    }

    @Url(value = "/api/rp/recursion", method = HttpMethod.POST)
    @JsonResponse
    public Personne rpRecursionPost(@RequestParam("pers") Personne personne) {
        return personne;
    }

    // Cas 2 : plusieurs objets du même type
    // Formulaire : a.nom, a.adresse.ville, b.nom, b.adresse.ville ...
    @Url("/api/rp/multi")
    @JsonResponse
    public Map<String, Object> rpMultiGet(@RequestParam("a") Personne p1,
                                          @RequestParam("b") Personne p2) {
        return comparer(p1, p2);
    }

    @Url(value = "/api/rp/multi", method = HttpMethod.POST)
    @JsonResponse
    public Map<String, Object> rpMultiPost(@RequestParam("a") Personne p1,
                                           @RequestParam("b") Personne p2) {
        return comparer(p1, p2);
    }

    // Cas 3 : type simple + objet
    // Formulaire : t, q, pers.nom, pers.adresse.ville ...
    @Url("/api/rp/mixte")
    @JsonResponse
    public Map<String, Object> rpMixteGet(@RequestParam("t") String titre,
                                          @RequestParam("q") int quantite,
                                          @RequestParam("pers") Personne personne) {
        return mixte(titre, quantite, personne);
    }

    @Url(value = "/api/rp/mixte", method = HttpMethod.POST)
    @JsonResponse
    public Map<String, Object> rpMixtePost(@RequestParam("t") String titre,
                                           @RequestParam("q") int quantite,
                                           @RequestParam("pers") Personne personne) {
        return mixte(titre, quantite, personne);
    }

    // =====================================================================
    //  Méthodes utilitaires (partagées)
    // =====================================================================

    private Map<String, Object> comparer(Personne p1, Personne p2) {
        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("p1", p1);
        resultat.put("p2", p2);
        resultat.put("memeInstance", p1 == p2); // false = deux objets distincts
        return resultat;
    }

    private Map<String, Object> mixte(String titre, int quantite, Personne personne) {
        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("titre", titre);
        resultat.put("quantite", quantite);
        resultat.put("personne", personne);
        return resultat;
    }
}
