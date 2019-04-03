package com.prism.springas.utils.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
    -
 */
public class CacheClass {
    private static Map<String,Object> cache = new ConcurrentHashMap<String, Object>();

    public static void setCache(String key, Object obj, long seconds){
        cache.put(key,obj);
    }

    public static Object getCache(String key){
        return cache.get(key);
    }

    public static  void removeCache(String key){
        cache.remove(key);
    }
}
