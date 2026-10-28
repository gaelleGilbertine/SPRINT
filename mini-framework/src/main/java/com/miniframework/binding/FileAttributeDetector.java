package com.miniframework.binding;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Vérifie si un objet lié ({@code DataBinder}) contient un attribut de type
 * fichier, c'est-à-dire un champ dont le type déclare au moins un
 * {@code byte[]} — la classe {@code MapFile} d'une application par exemple.
 *
 * <p>La détection est réflexive : le framework ne connaît pas les classes de
 * l'application, il remonte simplement l'objet champ par champ (récursivement,
 * avec protection contre les boucles) et compare les types déclarés.</p>
 *
 * <p>Elle porte sur la <em>structure</em> de l'objet : la présence de
 * l'attribut est confirmée même si aucun fichier n'a été envoyé. Le contenu
 * réellement chargé est à juger côté application (taille des octets).</p>
 */
public final class FileAttributeDetector {

    /** Profondeur maximale de récursion (sécurité contre les objets profonds). */
    private static final int PROFONDEUR_MAX = 8;

    private FileAttributeDetector() {
    }

    /**
     * @param target objet lié depuis un formulaire (jamais {@code null})
     * @return {@code true} si l'objet contient, à lui-même ou dans l'un de ses
     *         champs imbriqués, un attribut de type fichier
     */
    public static boolean containsFileAttribute(Object target) {
        return cherche(target, Collections.newSetFromMap(new IdentityHashMap<>()), 0);
    }

    private static boolean cherche(Object cible, Set<Object> visites, int profondeur) {
        if (cible == null || profondeur > PROFONDEUR_MAX) {
            return false;
        }
        if (declareFichier(cible.getClass())) {
            return true;
        }

        for (Field champ : cible.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(champ.getModifiers())) {
                continue;
            }
            Class<?> type = champ.getType();

            // Le champ est lui-même de type "fichier" (MapFile) : détection.
            if (declareFichier(type)) {
                return true;
            }
            // Champs simples ou types JDK : sans intérêt pour la recherche.
            if (type.isPrimitive() || type.getName().startsWith("java.")) {
                continue;
            }

            Object valeur = lire(champ, cible);
            if (valeur == null || !visites.add(valeur)) {
                continue;
            }
            if (cherche(valeur, visites, profondeur + 1)) {
                return true;
            }
        }
        return false;
    }

    /** Vrai si la classe déclare directement au moins un champ {@code byte[]}. */
    private static boolean declareFichier(Class<?> type) {
        for (Field champ : type.getDeclaredFields()) {
            if (champ.getType() == byte[].class) {
                return true;
            }
        }
        return false;
    }

    private static Object lire(Field champ, Object cible) {
        try {
            champ.setAccessible(true);
            return champ.get(cible);
        } catch (RuntimeException | IllegalAccessException e) {
            return null;
        }
    }
}
