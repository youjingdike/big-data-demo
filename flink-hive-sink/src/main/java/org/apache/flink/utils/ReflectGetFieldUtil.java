package org.apache.flink.utils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectGetFieldUtil {
    private static Map<String, Field> map = new ConcurrentHashMap();

    public static Object getField(Object obj, String className, String field) {
        String key = className + field;
        try {
            Field f;
            if (map.containsKey(key)) {
                f = map.get(key);
            } else {
                Class clazz = Class.forName(className);
                f = clazz.getDeclaredField(field);
                f.setAccessible(true);
                map.put(key, f);
            }
            return f.get(obj);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
