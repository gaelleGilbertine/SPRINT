package com.miniframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Force / personnalise le nom du paramètre HTTP à lier à un argument.
 * Sans cette annotation, le framework utilise le nom Java du paramètre.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {

    /** Nom du paramètre HTTP. Vide : on garde le nom Java du paramètre. */
    String value() default "";
}
