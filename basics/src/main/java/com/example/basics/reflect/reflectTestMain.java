package com.example.basics.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class reflectTestMain {
    public static void main(String[] args) {
//        Map<String, String> map = new HashMap<>();
//        map.put("蔡徐鸡","唱跳rap篮球");
//        System.out.println(map);
//        Map<String, String> linkedHashMap = new LinkedHashMap<>();
//        linkedHashMap.put("蔡徐鸡","唱跳rap篮球");
//        System.out.println(linkedHashMap);

        System.out.println(Map("java.util.HashMap"));

    }


    public static   Map<String, String>  getMap(String param) {
        Map<String, String> map = null;
        if (param.equals("HashMap")) {
            map = new HashMap<>();
        } else if (param.equals("LinkedHashMap")) {
            map = new LinkedHashMap<>();
        }
        return map;
    }

    public static  Map<String, String> Map(String className) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor();
            return (Map<String, String>) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
