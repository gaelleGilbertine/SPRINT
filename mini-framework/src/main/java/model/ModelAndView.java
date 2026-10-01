package model;

import java.util.HashMap;

public class ModelAndView {
    private String urlSuivant;
    private HashMap<String, String> list = new HashMap<>();

    public ModelAndView() {
    }

    public ModelAndView(String urlSuivant) {
        this.urlSuivant = urlSuivant;
    }

    public String getUrlSuivant() {
        return urlSuivant;
    }

    public void setUrlSuivant(String urlSuivant) {
        this.urlSuivant = urlSuivant;
    }

    public HashMap<String, String> getList() {
        return list;
    }

    public void setList(HashMap<String, String> list) {
        this.list = list;
    }

    // Ajoute une donnée à envoyer à la vue
    public void addObject(String cle, String valeur) {
        list.put(cle, valeur);
    }
}