package com.example.controllers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.miniframework.annotation.Controller;
import com.miniframework.annotation.JsonResponse;
import com.miniframework.annotation.Url;

/**
 * Sprint 11 : démonstration du singleton. Les contrôleurs n'ont qu'une instance pour toute
 * l'application : {@code instance} reste identique d'une requête à l'autre et {@code appels}
 * augmente de 1 à chaque appel. Le compteur est un {@link AtomicInteger} car plusieurs
 * requêtes simultanées partagent cette instance.
 */
@Controller
public class SingletonController {

    private final AtomicInteger appels = new AtomicInteger();

    @Url("/api/singleton")
    @JsonResponse
    public Map<String, Object> verifier() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("instance", Integer.toHexString(System.identityHashCode(this)));
        result.put("thread", Thread.currentThread().getName());
        result.put("appels", appels.incrementAndGet());
        return result;
    }
}