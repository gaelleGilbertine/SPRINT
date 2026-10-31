package com.miniframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Réserve une méthode de contrôleur (ou tout un contrôleur) aux personnes connectées.
 *
 * <ul>
 *   <li>{@code @Authenticated} : il suffit d'être connecté (sprint 10) ;</li>
 *   <li>{@code @Authenticated(roles = "ADMIN")} : il faut en plus posséder le rôle
 *       {@code ADMIN} (sprint 10 bis) ;</li>
 *   <li>{@code @Authenticated(roles = {"ADMIN", "RH"})} : il faut posséder
 *       <strong>au moins un</strong> des rôles listés.</li>
 * </ul>
 *
 * <p>Si l'annotation est présente à la fois sur la classe et sur la méthode, celle de la
 * <strong>méthode</strong> remplace entièrement celle de la classe.</p>
 *
 * <p>Réponses en cas de refus : {@code 401} si la personne n'est pas connectée,
 * {@code 403} si elle est connectée mais n'a aucun des rôles exigés.</p>
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {

    /**
     * Rôles autorisés (au moins un). Vide par défaut : toute personne connectée est acceptée.
     * La comparaison est sensible à la casse.
     */
    String[] roles() default {};
}