package com.miniframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.miniframework.http.HttpMethod;

/**
 * Associe une URL et une méthode HTTP à une méthode de contrôleur.
 *
 * <p>Exemple : {@code @Url(value = "/personne/save", method = HttpMethod.POST)}</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Url {

    /** Chemin relatif au contexte de l'application (commence par {@code /}). */
    String value();

    /** Méthode HTTP acceptée. {@code GET} par défaut. */
    HttpMethod method() default HttpMethod.GET;
}
