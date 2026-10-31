package com.example.controllers;

import java.util.List;

import com.example.models.Employe;
import com.miniframework.annotation.Authenticated;
import com.miniframework.annotation.Controller;
import com.miniframework.annotation.JsonResponse;
import com.miniframework.annotation.Url;
import com.miniframework.mvc.ModelAndView;

/**
 * Sprint 10 bis : la liste des employés n'est accessible qu'aux rôles ADMIN et RH.
 * Un compte connecté sans ces rôles (ex. EMPLOYE) reçoit un 403 ; une personne non
 * connectée reçoit un 401.
 */
@Controller
public class EmployeController {

    private static final List<Employe> EMPLOYES = List.of(
            new Employe(1, "Rakoto Jean", "Développeur", "Informatique"),
            new Employe(2, "Rasoa Hanitra", "Responsable RH", "Ressources humaines"),
            new Employe(3, "Andry Rabe", "Comptable", "Finance"),
            new Employe(4, "Miora Ravelo", "Chef de projet", "Informatique"));

    @Url("/employes")
    @Authenticated(roles = {"ADMIN", "RH"})
    public ModelAndView liste() {
        return new ModelAndView("employes.jsp").addObject("employes", EMPLOYES);
    }

    @Url("/api/employes")
    @Authenticated(roles = {"ADMIN", "RH"})
    @JsonResponse
    public List<Employe> listeJson() {
        return EMPLOYES;
    }
}