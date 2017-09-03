package com.camadeusa.utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static Method getMethod(Class<?> cls, String methodname, int paramlength) {
        for (Method m : cls.getDeclaredMethods()) {
            if (!m.getName().equals(methodname) || m.getParameterTypes().length != paramlength) continue;
            m.setAccessible(true);
            return m;
        }
        return null;
    }

    public static Method getMethod(Class<?> cls, String methodname, Class<?> ... params) {
        for (Method m : cls.getDeclaredMethods()) {
            if (!m.getName().equals(methodname) || !Arrays.equals(m.getParameterTypes(), params)) continue;
            m.setAccessible(true);
            return m;
        }
        return null;
    }

    public static Object getMethodReturn(Method m, Object instance, Object ... args) {
        try {
            return m.invoke(instance, args);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getMethodReturn(Method m, Object instance, Class<T> expected, Object ... args) {
        Object ret = ReflectionUtil.getMethodReturn(m, instance, args);
        if (ret == null) {
            return null;
        }
        try {
            return expected.cast(ret);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field getField(Class<?> cls, String fieldname) {
        for (Field f : cls.getDeclaredFields()) {
            if (!f.getName().equals(fieldname)) continue;
            f.setAccessible(true);
            return f;
        }
        return null;
    }

    public static boolean setFieldValue(Field f, Object instance, Object newvalue) {
        try {
            f.set(instance, newvalue);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Object getFieldValue(Field f, Object instance) {
        try {
            return f.get(instance);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getFieldValue(Field f, Object instance, Class<T> expected) {
        Object value = ReflectionUtil.getFieldValue(f, instance);
        if (value == null) {
            return null;
        }
        try {
            return expected.cast(value);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> Constructor<T> getConstructor(Class<T> cls, Class<?> ... params) {
        try {
            return cls.getDeclaredConstructor(params);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getConstructedInstance(Constructor<T> con, Object ... args) {
        try {
            con.setAccessible(true);
            return con.newInstance(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

