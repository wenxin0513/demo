/*
 * Copyright 2019 zhouhong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.myexcel.core.converter.writer;


import com.example.myexcel.core.annotation.ExcelColumn;
import com.example.myexcel.core.cache.WeakCache;
import com.example.myexcel.core.constant.Constants;
import com.example.myexcel.core.container.Pair;
import com.example.myexcel.core.converter.WriteConverter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author zhouhong
 * @version 1.0
 */
public class MappingWriteConverter implements WriteConverter {

    private WeakCache<String, Pair<Class, Object>> mappingCache = new WeakCache<>();

    @Override
    public boolean support(Field field, Object fieldVal) {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        return excelColumn != null && StringUtils.isNotBlank(excelColumn.mapping());
    }

    @Override
    public Pair<Class, Object> writeConvert(Field field, Object fieldVal) {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        String cacheKey = excelColumn.mapping() + "->" + fieldVal;
        Pair<Class, Object> mapping = mappingCache.get(cacheKey);
        if (mapping != null) {
            return mapping;
        }
        String[] mappingGroups = excelColumn.mapping().split(Constants.COMMA);
        Properties properties = new Properties();
        Arrays.stream(mappingGroups).forEach(m -> {
            String[] mappingGroup = m.split(Constants.COLON);
            if (mappingGroup.length != 2) {
                throw new IllegalArgumentException("Illegal mapping:" + m);
            }
            properties.setProperty(mappingGroup[0], mappingGroup[1]);
        });
        String property = properties.getProperty(fieldVal.toString());
        if (property == null) {
            return Pair.of(field.getType(), fieldVal);
        }
        Pair<Class, Object> result = Pair.of(String.class, property);
        mappingCache.cache(cacheKey, result);
        return result;
    }
}
