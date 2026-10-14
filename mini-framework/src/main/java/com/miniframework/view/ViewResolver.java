package com.miniframework.view;

/**
 * Résout le chemin réel d'une vue JSP à partir de son nom logique,
 * en appliquant un préfixe et un suffixe configurables (voir {@code web.xml}).
 */
public class ViewResolver {

    private final String prefix;
    private final String suffix;

    public ViewResolver(String prefix, String suffix) {
        this.prefix = prefix == null ? "" : prefix;
        this.suffix = suffix == null ? "" : suffix;
    }

    public String resolve(String view) {
        return prefix + view + suffix;
    }
}
