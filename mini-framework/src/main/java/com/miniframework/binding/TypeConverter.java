package com.miniframework.binding;

/**
 * Convertit une valeur texte (issue de la requête HTTP) vers le type Java
 * attendu par un paramètre ou un champ.
 */
public class TypeConverter {

    /**
     * @param value valeur brute (peut être {@code null} ou vide)
     * @param type  type cible
     * @return la valeur convertie, ou la valeur par défaut du type primitif
     */
    public Object convert(String value, Class<?> type) {
        if (type == String.class) {
            return value;
        }
        if (value == null || value.isEmpty()) {
            return defaultValue(type);
        }
        if (type == int.class || type == Integer.class) {
            return Integer.valueOf(value);
        }
        if (type == long.class || type == Long.class) {
            return Long.valueOf(value);
        }
        if (type == double.class || type == Double.class) {
            return Double.valueOf(value);
        }
        if (type == float.class || type == Float.class) {
            return Float.valueOf(value);
        }
        if (type == short.class || type == Short.class) {
            return Short.valueOf(value);
        }
        if (type == byte.class || type == Byte.class) {
            return Byte.valueOf(value);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.valueOf(value);
        }
        if (type == char.class || type == Character.class) {
            return value.charAt(0);
        }
        return value;
    }

    private Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return Boolean.FALSE;
        }
        if (type == char.class) {
            return (char) 0;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == double.class) {
            return 0d;
        }
        if (type == float.class) {
            return 0f;
        }
        return null;
    }
}
