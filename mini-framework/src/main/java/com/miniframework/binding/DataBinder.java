package com.miniframework.binding;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;

import com.miniframework.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Relie les paramètres d'une requête HTTP aux arguments d'une méthode de
 * contrôleur (data binding).
 *
 * <p>Deux cas sont gérés :</p>
 * <ul>
 *   <li>types simples ({@code String}, {@code int}, ...) : lus directement ;</li>
 *   <li>objets : construits par réflexion, de façon récursive pour les champs
 *       imbriqués ({@code adresse.ville} par exemple).</li>
 * </ul>
 */
public class DataBinder {

    private final HttpServletRequest request;
    private final TypeConverter converter = new TypeConverter();

    public DataBinder(HttpServletRequest request) {
        this.request = request;
    }

    public Object[] bind(Method method) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];

        boolean allowNoPrefix = countObjects(parameters) == 1;
        Set<String> reservedNames = collectSimpleParameterNames(parameters);

        for (int i = 0; i < parameters.length; i++) {
            Class<?> type = parameters[i].getType();
            String name = resolveName(parameters[i]);

            if (isSimple(type)) {
                arguments[i] = converter.convert(request.getParameter(name), type);
            } else {
                boolean noPrefix = allowNoPrefix && parameters[i].getAnnotation(RequestParam.class) == null;
                arguments[i] = bindObject(type, name, "", noPrefix, reservedNames);
            }
        }
        return arguments;
    }

    private Object bindObject(Class<?> type, String parameterName, String path,
                              boolean allowNoPrefix, Set<String> reservedNames) throws Exception {
        Object instance = type.getDeclaredConstructor().newInstance();

        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String fieldPath = path.isEmpty() ? field.getName() : path + "." + field.getName();
            field.setAccessible(true);

            if (isSimple(field.getType())) {
                String value = request.getParameter(parameterName + "." + fieldPath);
                if (value == null && allowNoPrefix && !reservedNames.contains(fieldPath)) {
                    value = request.getParameter(fieldPath);
                }
                if (value != null) {
                    field.set(instance, converter.convert(value, field.getType()));
                }
            } else if (!field.getType().getName().startsWith("java.")) {
                field.set(instance, bindObject(field.getType(), parameterName, fieldPath,
                        allowNoPrefix, reservedNames));
            }
        }
        return instance;
    }

    private long countObjects(Parameter[] parameters) {
        long count = 0;
        for (Parameter parameter : parameters) {
            if (!isSimple(parameter.getType())) {
                count++;
            }
        }
        return count;
    }

    private Set<String> collectSimpleParameterNames(Parameter[] parameters) {
        Set<String> names = new HashSet<>();
        for (Parameter parameter : parameters) {
            if (isSimple(parameter.getType())) {
                names.add(resolveName(parameter));
            }
        }
        return names;
    }

    /** Nom HTTP du paramètre : valeur de {@link RequestParam}, sinon nom Java. */
    private String resolveName(Parameter parameter) {
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        if (requestParam != null && !requestParam.value().isEmpty()) {
            return requestParam.value();
        }
        return parameter.getName();
    }

    private boolean isSimple(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || type == Boolean.class
                || Number.class.isAssignableFrom(type);
    }
}
