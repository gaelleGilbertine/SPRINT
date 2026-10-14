package com.miniframework.mvc;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Résultat d'une méthode de contrôleur : le nom logique de la vue à afficher
 * et les données (modèle) à transmettre à cette vue.
 */
public class ModelAndView {

    private String view;
    private final Map<String, Object> model = new LinkedHashMap<>();

    public ModelAndView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    /** Ajoute une donnée au modèle et renvoie {@code this} (chaînage). */
    public ModelAndView addObject(String key, Object value) {
        model.put(key, value);
        return this;
    }
}
