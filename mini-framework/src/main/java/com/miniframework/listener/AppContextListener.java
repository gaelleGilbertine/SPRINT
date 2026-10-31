package com.miniframework.listener;

import com.miniframework.routing.ControllerRegistry;
import com.miniframework.routing.ControllerScanner;
import com.miniframework.routing.RouteRegistry;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Au démarrage de l'application, scanne le package des contrôleurs (défini par
 * le context-param {@code controller-package}) et met la table de routage à
 * disposition du {@code FrontControllerServlet}, avec le registre qui garde
 * l'unique instance de chaque contrôleur (singleton).
 */
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        String basePackage = context.getInitParameter("controller-package");
        try {
            RouteRegistry registry = new ControllerScanner(basePackage).scan();
            context.setAttribute(RouteRegistry.CONTEXT_ATTRIBUTE, registry);
            // Sprint 11 : un seul registre, donc une seule instance par contrôleur, pour toute l'application
            context.setAttribute(ControllerRegistry.CONTEXT_ATTRIBUTE, new ControllerRegistry());
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Initialisation des routes impossible pour le package '" + basePackage + "'", e);
        }
    }
}