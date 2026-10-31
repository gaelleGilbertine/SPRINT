package com.miniframework.routing;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registre des contrôleurs : garantit <strong>une seule instance par classe de contrôleur</strong>
 * pour toute l'application (pattern singleton, géré par le framework).
 *
 * <p>L'instance est créée à la première requête qui en a besoin, puis réutilisée par toutes
 * les requêtes suivantes. {@link ConcurrentHashMap#computeIfAbsent} est atomique : même si
 * deux requêtes arrivent en même temps sur un contrôleur pas encore créé, le constructeur
 * n'est appelé qu'une seule fois.</p>
 *
 * <p><strong>Conséquence :</strong> plusieurs requêtes (donc plusieurs threads) partagent la même
 * instance. Un contrôleur ne doit pas stocker d'état propre à une requête dans un champ ; les
 * données de la requête passent par les paramètres de méthode, la {@code Session} ou le
 * {@code ModelAndView}. Un champ partagé doit être thread-safe (final, {@code AtomicInteger}…).</p>
 */
public class ControllerRegistry {

    /** Nom de l'attribut placé dans le {@code ServletContext}. */
    public static final String CONTEXT_ATTRIBUTE = "com.miniframework.controllers";

    private final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    /**
     * @return l'unique instance du contrôleur (créée si besoin via son constructeur sans argument)
     * @throws IllegalStateException si le contrôleur n'a pas de constructeur public sans argument
     */
    public Object get(Class<?> controllerType) {
        return instances.computeIfAbsent(controllerType, ControllerRegistry::instantiate);
    }

    /** Nombre de contrôleurs déjà instanciés (utile pour les tests). */
    public int size() {
        return instances.size();
    }

    private static Object instantiate(Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Impossible d'instancier le contrôleur " + type.getName()
                            + " (constructeur sans argument requis)", e);
        }
    }
}