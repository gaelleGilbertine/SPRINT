package com.example.services;

import java.util.HashMap;
import java.util.Map;

import com.example.models.Utilisateur;
import com.miniframework.security.PasswordHasher;

public final class UserStore {

    private record Compte(Utilisateur utilisateur, String empreinte) {
    }

    private static final Map<String, Compte> COMPTES = new HashMap<>();

    // Vérifiée quand le login est inconnu, pour garder un temps de réponse identique
    private static final String EMPREINTE_FACTICE = PasswordHasher.hash("sans-importance");

    static {
        ajouter("admin", "Administrateur", "admin123");
        ajouter("rakoto", "Rakoto Jean", "rakoto123");
    }

    private UserStore() {
    }

    private static void ajouter(String login, String nom, String motDePasse) {
        COMPTES.put(login, new Compte(new Utilisateur(login, nom), PasswordHasher.hash(motDePasse)));
    }

    public static Utilisateur authentifier(String login, String motDePasse) {
        if (login == null || motDePasse == null) {
            return null;
        }
        Compte compte = COMPTES.get(login);
        if (compte == null) {
            PasswordHasher.verify(motDePasse, EMPREINTE_FACTICE);
            return null;
        }
        return PasswordHasher.verify(motDePasse, compte.empreinte()) ? compte.utilisateur() : null;
    }
}