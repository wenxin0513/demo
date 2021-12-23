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
package com.example.myexcel.core.reflect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhouhong
 * @version 1.0
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassFieldContainer {

    Class<?> clazz;

    List<Field> declaredFields = Lists.newArrayList();

    Map<String, Field> fieldMap = Maps.newHashMap();

    ClassFieldContainer parent;

    public Field getFieldByName(String fieldName) {
        return this.getFieldByName(fieldName, this);
    }

    public List<Field> getFieldsByAnnotation(Class<? extends Annotation> annotationClass) {
        Objects.requireNonNull(annotationClass);
        List<Field> annotationFields = Lists.newArrayList();
        this.getFieldsByAnnotation(this, annotationClass, annotationFields);
        return annotationFields;
    }

    public List<Field> getFields() {
        List<Field> fields = Lists.newArrayList();
        this.getFieldsByContainer(this, fields);
        return fields;
    }

    private void getFieldsByContainer(ClassFieldContainer classFieldContainer, List<Field> fields) {
        ClassFieldContainer parentContainer = classFieldContainer.getParent();
        if (parentContainer != null) {
            this.getFieldsByContainer(parentContainer, fields);
        }
        filterFields(classFieldContainer.getDeclaredFields(), fields);
    }

    private void getFieldsByAnnotation(ClassFieldContainer classFieldContainer, Class<? extends Annotation> annotationClass, List<Field> annotationFieldContainer) {
        ClassFieldContainer parentContainer = classFieldContainer.getParent();
        if (parentContainer != null) {
            this.getFieldsByAnnotation(parentContainer, annotationClass, annotationFieldContainer);
        }
        List<Field> annotationFields = classFieldContainer.declaredFields.stream().filter(field -> field.isAnnotationPresent(annotationClass)).collect(Collectors.toList());
        filterFields(annotationFields, annotationFieldContainer);
    }

    private void filterFields(List<Field> declaredFields, List<Field> fieldContainer) {
        for (int i = 0, size = declaredFields.size(); i < size; i++) {
            Field field = declaredFields.get(i);
            Optional<Field> fieldOptional = fieldContainer
                    .stream()
                    .filter(f -> f.getName().equals(field.getName()))
                    .findFirst();
            if (fieldOptional.isPresent()) {
                fieldContainer.set(i, field);
            } else {
                fieldContainer.add(field);
            }
        }
    }

    private Field getFieldByName(String fieldName, ClassFieldContainer container) {
        Field field = container.getFieldMap().get(fieldName);
        if (field != null) {
            return field;
        }
        ClassFieldContainer parentContainer = container.getParent();
        if (parentContainer == null) {
            return null;
        }
        return getFieldByName(fieldName, parentContainer);
    }

}
