package com.example.controllers;

import annotation.Annotation;
import annotation.JsonAnnotation;
import annotation.UrlAnnotation;
import java.util.HashMap;
import model.ModelAndView;

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
}