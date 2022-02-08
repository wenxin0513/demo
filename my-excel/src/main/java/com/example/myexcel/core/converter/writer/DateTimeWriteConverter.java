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
import com.example.myexcel.core.cache.Cache;
import com.example.myexcel.core.cache.WeakCache;
import com.example.myexcel.core.container.Pair;
import com.example.myexcel.core.converter.WriteConverter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author zhouhong
 * @version 1.0
 */
public class DateTimeWriteConverter implements WriteConverter {

    private static final Cache<String, DateTimeFormatter> DATETIME_FORMATTER_CONTAINER = new WeakCache<>();

    private static final WeakCache<String, ThreadLocal<SimpleDateFormat>> SIMPLE_DATE_FORMAT_WEAK_CACHE = new WeakCache<>();

    @Override
    public boolean support(Field field, Object fieldVal) {
        Class<?> fieldType = field.getType();
        boolean validType = fieldType == LocalDateTime.class || fieldType == LocalDate.class || fieldType == Date.class;
        if (!validType) {
            return false;
        }
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        return excelColumn != null && StringUtils.isNotBlank(excelColumn.dateFormatPattern());
    }

    @Override
    public Pair<Class, Object> writeConvert(Field field, Object fieldVal) {
        Class<?> fieldType = field.getType();
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        // 时间格式化
        String dateFormatPattern = excelColumn.dateFormatPattern();
        if (fieldType == LocalDateTime.class) {
            LocalDateTime localDateTime = (LocalDateTime) fieldVal;
            DateTimeFormatter formatter = getDateTimeFormatter(dateFormatPattern);
            return Pair.of(String.class, formatter.format(localDateTime));
        } else if (fieldType == LocalDate.class) {
            LocalDate localDate = (LocalDate) fieldVal;
            DateTimeFormatter formatter = getDateTimeFormatter(dateFormatPattern);
            return Pair.of(String.class, formatter.format(localDate));
        }
        SimpleDateFormat simpleDateFormat = this.getSimpleDateFormat(dateFormatPattern);
        return Pair.of(String.class, simpleDateFormat.format((Date) fieldVal));
    }

    /**
     * 获取时间格式化
     *
     * @param dateFormat 时间格式化
     * @return DateTimeFormatter
     */
    private DateTimeFormatter getDateTimeFormatter(String dateFormat) {
        DateTimeFormatter formatter = DATETIME_FORMATTER_CONTAINER.get(dateFormat);
        if (formatter == null) {
            formatter = DateTimeFormatter.ofPattern(dateFormat);
            DATETIME_FORMATTER_CONTAINER.cache(dateFormat, formatter);
        }
        return formatter;
    }

    private SimpleDateFormat getSimpleDateFormat(String dateFormatPattern) {
        ThreadLocal<SimpleDateFormat> tl = SIMPLE_DATE_FORMAT_WEAK_CACHE.get(dateFormatPattern);
        if (tl == null) {
            tl = ThreadLocal.withInitial(() -> new SimpleDateFormat(dateFormatPattern));
            SIMPLE_DATE_FORMAT_WEAK_CACHE.cache(dateFormatPattern, tl);
        }
        return tl.get();
    }
}
