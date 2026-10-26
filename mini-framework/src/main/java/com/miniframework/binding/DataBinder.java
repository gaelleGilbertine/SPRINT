package com.miniframework.binding;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import com.miniframework.annotation.RequestParam;
import com.miniframework.session.Session;  

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

    /** La requête est-elle {@code multipart/form-data} (envoi de fichiers) ? */
    private final boolean multipart;

    public DataBinder(HttpServletRequest request) {
        this.request = request;
        String contentType = request.getContentType();
        this.multipart = contentType != null
                && contentType.regionMatches(true, 0, "multipart/", 0, "multipart/".length());
    }

public Object[] bind(Method method) throws Exception {
    Parameter[] parameters = method.getParameters();
    Object[] arguments = new Object[parameters.length];

    boolean allowNoPrefix = countObjects(parameters) == 1;
    Set<String> reservedNames = collectSimpleParameterNames(parameters);

    for (int i = 0; i < parameters.length; i++) {
        Class<?> type = parameters[i].getType();

        // Session : fournie par le framework, pas lue dans les paramètres HTTP
        if (type == Session.class) {
            arguments[i] = new Session(request.getSession());
            continue;
        }

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
                String value = readParameter(parameterName + "." + fieldPath);
                if (value == null && allowNoPrefix && !reservedNames.contains(fieldPath)) {
                    value = readParameter(fieldPath);
                }
                if (value != null) {
                    field.set(instance, converter.convert(value, field.getType()));
                }
            } else if (field.getType() == byte[].class) {
                // Sprint 8 : champ binaire lu dans les <input type="file">
                byte[] contenu = readPart(parameterName + "." + fieldPath);
                if (contenu == null && allowNoPrefix) {
                    contenu = readPart(fieldPath);
                }
                if (contenu != null) {
                    field.set(instance, contenu);
                }
            } else if (!field.getType().getName().startsWith("java.")) {
                field.set(instance, bindObject(field.getType(), parameterName, fieldPath,
                        allowNoPrefix, reservedNames));
            }
        }
        return instance;
    }

    /**
     * Lit un paramètre texte : d'abord dans les paramètres de la requête, puis,
     * pour une requête {@code multipart/form-data}, dans la pièce jointe portant
     * le même nom (certains conteneurs n'exposent pas les champs texte d'un
     * multipart via {@code getParameter}).
     *
     * @return la valeur, ou {@code null} si le paramètre est absent
     */
    private String readParameter(String name) {
        String value = request.getParameter(name);
        if (value == null && multipart) {
            try {
                Part part = request.getPart(name);
                if (part != null && part.getSize() > 0) {
                    value = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return value;
    }

    /**
     * Contenu d'un {@code <input type="file">}, ou {@code null} si le champ est
     * absent de la requête ou si aucun fichier n'a été sélectionné.
     */
    private byte[] readPart(String name) {
        if (!multipart) {
            return null;
        }
        try {
            Part part = request.getPart(name);
            if (part == null) {
                return null;
            }
            String nomFichier = part.getSubmittedFileName();
            if (nomFichier == null || nomFichier.isEmpty() || part.getSize() == 0) {
                return null;
            }
            return part.getInputStream().readAllBytes();
        } catch (Exception e) {
            return null;
        }
    }

 private long countObjects(Parameter[] parameters) {
    long count = 0;
    for (Parameter parameter : parameters) {
        Class<?> type = parameter.getType();
        if (!isSimple(type) && type != Session.class) {
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
