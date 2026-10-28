package com.miniframework.routing;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.miniframework.annotation.Controller;
import com.miniframework.annotation.Url;

/**
 * Parcourt un package à la recherche des classes annotées {@link Controller}
 * et enregistre leurs méthodes annotées {@link Url} dans un {@link RouteRegistry}.
 */
public class ControllerScanner {

    private static final String CLASS_SUFFIX = ".class";

    private final String basePackage;

    public ControllerScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    public RouteRegistry scan() throws IOException, ClassNotFoundException {
        RouteRegistry registry = new RouteRegistry();

        for (Class<?> candidate : findClasses()) {
            if (!candidate.isAnnotationPresent(Controller.class)) {
                continue;
            }
            for (Method method : candidate.getDeclaredMethods()) {
                Url url = method.getAnnotation(Url.class);
                if (url != null) {
                    registry.register(url.value(), url.method(), method);
                }
            }
        }
        return registry;
    }

    private List<Class<?>> findClasses() throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        String packagePath = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packagePath);

        while (resources.hasMoreElements()) {
            File directory = new File(resources.nextElement().getFile());
            if (!directory.exists()) {
                continue;
            }
            File[] files = directory.listFiles();
            if (files == null) {
                continue;
            }
            for (File file : files) {
                if (file.getName().endsWith(CLASS_SUFFIX)) {
                    String simpleName = file.getName()
                            .substring(0, file.getName().length() - CLASS_SUFFIX.length());
                    classes.add(Class.forName(basePackage + "." + simpleName));
                }
            }
        }
        return classes;
    }
}
