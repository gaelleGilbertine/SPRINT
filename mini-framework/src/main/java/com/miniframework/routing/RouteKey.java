package com.miniframework.routing;

import com.miniframework.http.HttpMethod;

/**
 * Clé unique d'une route : une URL associée à une méthode HTTP.
 * Immuable (record) : sert de clé de {@code Map} sans risque de mutation.
 */
public record RouteKey(String url, HttpMethod method) {
}
