package com.example.controllers;

import com.example.models.Utilisateur;
import com.example.services.UserStore;
import com.miniframework.annotation.Controller;
import com.miniframework.annotation.Url;
import com.miniframework.http.HttpMethod;
import com.miniframework.mvc.ModelAndView;
import com.miniframework.session.Session;

@Controller
public class AuthController {

    private static final String PAGE_PAR_DEFAUT = "/secret/page";

    @Url("/login")
    public ModelAndView loginPage(Session session) {
        if (session.isLoggedIn()) {
            return new ModelAndView("redirect:" + PAGE_PAR_DEFAUT);
        }
        return new ModelAndView("login.jsp");
    }

    @Url(value = "/login", method = HttpMethod.POST)
    public ModelAndView login(String login, String motDePasse, Session session) {
        Utilisateur utilisateur = UserStore.authentifier(login == null ? null : login.trim(), motDePasse);

        if (utilisateur == null) {
            return new ModelAndView("login.jsp")
                    .addObject("erreur", "Identifiant ou mot de passe incorrect.");
        }

        String destination = session.takeRequestedUrl();
        session.login(utilisateur, utilisateur.getRoles());
        return new ModelAndView("redirect:" + (destination != null ? destination : PAGE_PAR_DEFAUT));
    }

    @Url(value = "/logout", method = HttpMethod.POST)
    public ModelAndView logout(Session session) {
        session.logout();
        return new ModelAndView("redirect:/login");
    }
}