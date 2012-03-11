/*
Copyright (c) 2011, Chin Huang
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.github.pukkaone.mullet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default scope implementation which resolves variable names to values by
 * reading from a data object. Given a variable name <var>key</var>, the
 * following mechanisms are tried in this order:
 * <ul>
 * <li>If the variable name is {@code "."}, then return the object.
 * <li>If the object is a {@link Map}, then call {@link Map#get} to get the
 * value using the string <var>key</var> as the key.
 * <li>If the object has a method named <var>key</var> with non-void return
 * type, then call the method.
 * <li>If the object has a method named {@code get}<var>key</var> with the first
 * letter of <var>key</var> capitalized and non-void return type, then call the
 * method.
 * <li>If the object has a field named <var>key</var>, then return the field
 * value.
 * </ul>
 * If the value acquired from the previous steps is an object implementing
 * {@link Callable}, then the return value from invoking it will be used.
 */
public class DefaultScope implements Scope {

    protected static final String CURRENT_OBJECT_NAME = ".";

    /**
     * (data class, variable name) combination used to find variable fetchers
     * from cache
     */
    protected static class Key {
        public final Class<?> dataClass;
        public final String name;

        public Key (Class<?> dataClass, String name) {
            this.dataClass = dataClass;
            this.name = name;
        }

        @Override
        public int hashCode() {
            return dataClass.hashCode() * 31 + name.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            Key key = (Key) other;
            return dataClass == key.dataClass && name == key.name;
        }
    }

    protected static interface VariableFetcher {
        Object get(Object data, String name) throws Exception;
    }

    protected static final VariableFetcher THIS_FETCHER = new VariableFetcher() {
        public Object get(Object data, String name) throws Exception {
            return data;
        }
    };

    protected static final VariableFetcher MAP_FETCHER = new VariableFetcher() {
        public Object get(Object data, String name) throws Exception {
            Map<?,?> map = (Map<?,?>) data;
            Object value = map.get(name);
            if (value == null && !map.containsKey(name)) {
                // The get method returned null because the key was not found.
                value = NOT_FOUND;
            }
            return value;
        }
    };

    protected static Map<Key, VariableFetcher> fetcherCache =
            new ConcurrentHashMap<Key, VariableFetcher>();

    private Object data;

    public DefaultScope(Object data) {
        this.data = data;
    }

    protected static VariableFetcher createFetcher(Key key) {
        if (CURRENT_OBJECT_NAME == key.name) {
            return THIS_FETCHER;
        }

        if (Map.class.isAssignableFrom(key.dataClass)) {
            return MAP_FETCHER;
        }

        final Method method = getMethod(key.dataClass, key.name);
        if (method != null) {
            return new VariableFetcher() {
                public Object get(Object data, String name) throws Exception {
                    return method.invoke(data);
                }
            };
        }

        final Field field = getField(key.dataClass, key.name);
        if (field != null) {
            return new VariableFetcher() {
                public Object get(Object data, String name) throws Exception {
                    return field.get(data);
                }
            };
        }

        return null;
    }

    protected static Method getMethod(Class<?> clazz, String name) {
        Method method;
        try {
            method = clazz.getDeclaredMethod(name);
            if (!method.getReturnType().equals(void.class)) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            }
        } catch (Exception e) {
            // fall through
        }

        try {
            String getter =
                    "get" +
                    Character.toUpperCase(name.charAt(0)) + name.substring(1);
            method = clazz.getDeclaredMethod(getter);
            if (!method.getReturnType().equals(void.class)) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            }
        } catch (Exception e) {
            // fall through
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class && superClass != null) {
            return getMethod(superClass, name);
        }
        return null;
    }

    protected static Field getField(Class<?> clazz, String name) {
        Field field;
        try {
            field = clazz.getDeclaredField(name);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field;
        } catch (Exception e) {
            // fall through
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class && superClass != null) {
            return getField(superClass, name);
        }
        return null;
    }

    protected VariableFetcher getFetcher(Key key) {
        VariableFetcher fetcher = fetcherCache.get(key);
        if (fetcher == null) {
            fetcher = createFetcher(key);
            if (fetcher != null) {
                fetcherCache.put(key, fetcher);
            }
        }
        return fetcher;
    }

    private Object getValueImpl(String name) {
        Key key = new Key(data.getClass(), name);
        VariableFetcher fetcher = getFetcher(key);
        if (fetcher == null) {
            return NOT_FOUND;
        }

        try {
            return fetcher.get(data, name);
        } catch (Exception e) {
            // The method or field was changed out from under us.
            // Update the cache and try again
            fetcher = createFetcher(key);
            if (fetcher != null) {
                fetcherCache.put(key, fetcher);
            }
        }

        if (fetcher == null) {
            return NOT_FOUND;
        }

        try {
            return fetcher.get(data, name);
        } catch (Exception e) {
            throw new TemplateException(
                    "Failure fetching variable '" + name + "'", e);
        }
    }

    public Object getVariableValue(String name) {
        Object value = getValueImpl(name);
        if (value instanceof Callable<?>) {
            try {
                value = ((Callable<?>) value).call();
            } catch (Exception e) {
                throw new TemplateException("call", e);
            }
        }
        return value;
    }
}
