package com.example.controllers;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import com.example.models.Utilisateur;
import com.miniframework.annotation.Authenticated;
import com.miniframework.annotation.Controller;
import com.miniframework.annotation.JsonResponse;
import com.miniframework.annotation.Url;
import com.miniframework.http.HttpMethod;
import com.miniframework.mvc.ModelAndView;
import com.miniframework.session.Session;

@Controller
public class SecretController {

    @Url("/secret/page")
    @Authenticated
    public ModelAndView page(Session session) {
        return new ModelAndView("secret.jsp")
                .addObject("utilisateur", session.getUser(Utilisateur.class));
    }

    @Url(value = "/secret/executer", method = HttpMethod.POST)
    @Authenticated
    @JsonResponse
    public Map<String, Object> executer(int nombre, Session session) {
        Utilisateur utilisateur = session.getUser(Utilisateur.class);

        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("executePar", utilisateur.getLogin());
        resultat.put("nombre", nombre);
        resultat.put("carre", (long) nombre * nombre);
        resultat.put("date", LocalDateTime.now().toString());
        return resultat;
    }
}