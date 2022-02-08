/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.myexcel.utils;

import com.example.myexcel.core.annotation.ExcelColumn;
import com.example.myexcel.core.cache.WeakCache;
import com.example.myexcel.core.reflect.ClassFieldContainer;
import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhouhong
 * @version 1.0
 */
@UtilityClass
public final class ReflectUtil {

    private static final WeakCache<Class<?>, Map<Integer, Field>> FIELD_CACHE = new WeakCache<>();

    private static final WeakCache<Class<?>, Map<String, Field>> TITLE_FIELD_CACHE = new WeakCache<>();

    /**
     * 获取指定类的所有字段，包含父类字段，其中
     *
     * @param clazz 类
     * @return 类的所有字段
     */
    public static ClassFieldContainer getAllFieldsOfClass(Class<?> clazz) {
        ClassFieldContainer container = new ClassFieldContainer();
        getAllFieldsOfClass(clazz, container);
        return container;
    }

    public static Map<Integer, Field> getFieldMapOfExcelColumn(Class<?> dataType) {
        if (dataType == Map.class) {
            return Collections.emptyMap();
        }
        Map<Integer, Field> fieldMap = FIELD_CACHE.get(dataType);
        if (fieldMap != null) {
            return fieldMap;
        }
        ClassFieldContainer classFieldContainer = ReflectUtil.getAllFieldsOfClass(dataType);
        List<Field> fields = classFieldContainer.getFieldsByAnnotation(ExcelColumn.class);
        if (fields.isEmpty()) {
            throw new IllegalStateException("There is no field with @ExcelColumn");
        }
        fieldMap = Maps.newHashMapWithExpectedSize(fields.size());
        int c = 0;//如果指定local，则此处只是自增key实现，无其他意义
        for (Field field : fields) {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            int index = excelColumn.index();
            int locate = excelColumn.locate();
            if (locate >= 0) {
                field.setAccessible(true);
                fieldMap.put(c++, field);
            } else {
                if (index < 0) {
                    continue;
                }
                Field f = fieldMap.get(index);
                if (Objects.nonNull(f)) {
                    throw new IllegalStateException("Index cannot be repeated. Please check it.");
                }
                field.setAccessible(true);
                fieldMap.put(index, field);
            }
        }
        FIELD_CACHE.cache(dataType, fieldMap);
        return fieldMap;
    }

    public static Map<String, Field> getFieldMapOfTitleExcelColumn(Class<?> dataType) {
        if (dataType == Map.class) {
            return Collections.emptyMap();
        }
        Map<String, Field> fieldMap = TITLE_FIELD_CACHE.get(dataType);
        if (fieldMap != null) {
            return fieldMap;
        }
        ClassFieldContainer classFieldContainer = ReflectUtil.getAllFieldsOfClass(dataType);
        List<Field> fields = classFieldContainer.getFieldsByAnnotation(ExcelColumn.class);
        if (fields.isEmpty()) {
            throw new IllegalStateException("There is no field with @ExcelColumn");
        }
        fieldMap = Maps.newHashMapWithExpectedSize(fields.size());
        for (Field field : fields) {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            String title = excelColumn.title();
            if (title.isEmpty()) {
                continue;
            }
            Field f = fieldMap.get(title);
            if (f != null) {
                throw new IllegalStateException("Title cannot be repeated. Please check it.");
            }
            field.setAccessible(true);
            fieldMap.put(title, field);
        }
        TITLE_FIELD_CACHE.cache(dataType, fieldMap);
        return fieldMap;
    }

    /**
     * 根据对象以及指定字段，获取字段的值
     *
     * @param o     对象
     * @param field 指定字段
     * @return 字段值
     */
    public static Object getFieldValue(Object o, Field field) {
        if (o == null || field == null) {
            return null;
        }
        try {
            return field.get(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void getAllFieldsOfClass(Class<?> clazz, ClassFieldContainer container) {
        container.setClazz(clazz);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            container.getDeclaredFields().add(field);
            container.getFieldMap().put(field.getName(), field);
        }
        if (clazz.getSuperclass() != null) {
            ClassFieldContainer parentContainer = new ClassFieldContainer();
            container.setParent(parentContainer);
            getAllFieldsOfClass(clazz.getSuperclass(), parentContainer);
        }
    }

    public static boolean isNumber(Class clazz) {
        return clazz == Double.class || clazz == double.class
                || clazz == Float.class || clazz == float.class
                || clazz == Long.class || clazz == long.class
                || clazz == Integer.class || clazz == int.class
                || clazz == Short.class || clazz == short.class
                || clazz == Byte.class || clazz == byte.class
                || clazz == BigDecimal.class || clazz == BigInteger.class;
    }

    public static boolean isBool(Class clazz) {
        return clazz == boolean.class || clazz == Boolean.class;
    }

    public static int sortFields(Field field1, Field field2) {
        ExcelColumn excelColumn1 = field1.getAnnotation(ExcelColumn.class);
        ExcelColumn excelColumn2 = field2.getAnnotation(ExcelColumn.class);
        if (excelColumn1 == null && excelColumn2 == null) {
            return 0;
        }
        int defaultOrder = 0;
        int order1 = defaultOrder;
        if (excelColumn1 != null) {
            order1 = excelColumn1.index();
        }
        int order2 = defaultOrder;
        if (excelColumn2 != null) {
            order2 = excelColumn2.index();
        }
        if (order1 == order2) {
            return 0;
        }
        return order1 > order2 ? 1 : -1;
    }

    public static boolean isFieldSelected(List<Class<?>> selectedGroupList, Field field) {
        if (selectedGroupList.isEmpty()) {
            return true;
        }
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        if (excelColumn == null) {
            return false;
        }
        Class<?>[] groupArr = excelColumn.groups();
        if (groupArr.length == 0) {
            return false;
        }
        List<Class<?>> reservedGroupList = Arrays.stream(groupArr).collect(Collectors.toList());
        return reservedGroupList.stream().anyMatch(selectedGroupList::contains);
    }
}
