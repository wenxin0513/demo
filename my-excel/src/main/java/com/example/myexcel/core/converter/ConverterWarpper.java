/*
 * Copyright 2019 harry
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
package com.example.myexcel.core.converter;

import com.cntaiping.tplhk.reins.common.core.util.ApplicationUtil;
import com.cntaiping.tplhk.reins.common.excel.core.ReadContext;
import com.cntaiping.tplhk.reins.common.excel.core.annotation.ExcelColumn;
import com.cntaiping.tplhk.reins.common.excel.core.container.Pair;
import com.cntaiping.tplhk.reins.common.excel.core.function.ExceptionFunction;
import com.cntaiping.tplhk.reins.common.excel.exception.ExcelReadException;
import com.cntaiping.tplhk.reins.common.excel.exception.SaxReadException;
import com.cntaiping.tplhk.reins.common.excel.utils.DateUtil;
import com.cntaiping.tplhk.reins.common.excel.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.function.BiFunction;

/**
 * 转换器上下文
 *
 * @author harry
 * @version 1.0
 */
@Slf4j
public class ConverterWarpper {

    private static final Pair<Class, Object> NULL_PAIR = Pair.of(String.class, null);

    /**
     * 读取转换器
     *
     * @param obj               封装的对象
     * @param field             读取的字段
     * @param context           读取的内容
     * @param rowNum            行号
     * @param cellNum           列索引
     * @param exceptionFunction 异常捕获器
     * @return
     */
    public static ReadContext readConvert(Object obj, Field field, String context, int rowNum, int cellNum, BiFunction<Throwable, ReadContext, ExceptionFunction.ExceptionPolicy> exceptionFunction) {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        ReadContext result = ReadContext.builder()
                .object(obj)
                .field(field)
                .val(context)
                .rowNum(rowNum)
                .colNum(cellNum)
                .build();
        ReadConverter converter = ApplicationUtil.getContext().getBean(excelColumn.readConverter());
        if (converter == null) {
            throw new IllegalStateException("No suitable type converter was found.");
        }
        if (excelColumn.isValidDate()) {
            if (!DateUtil.isValidDate(context,excelColumn.dateFormatPattern())) {
                throw new ExcelReadException(field.getName() + "日期不存在");
            }
        }
        Object value;
        try {
            value = converter.readConvert(field, context);
        } catch (Exception e) {
            result.setErrorMsg(e.getMessage());
            exceptionFunction.apply(e, result);
            return result;
        }
        if (value == null) {
            return result;
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new SaxReadException("Failed to set the " + field.getName() + " field value to " + context, e);
        }
        return result;
    }

    /**
     * 写入转换器
     *
     * @param field
     * @param object
     * @return
     */
    public static Pair<? extends Class, Object> writeConvert(Field field, Object object) {

        Object result = ReflectUtil.getFieldValue(object, field);
        if (result == null) {
            return NULL_PAIR;
        }
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);

        WriteConverter converter = ApplicationUtil.getContext().getBean(excelColumn.writeConverter());
        if (converter == null) {
            throw new IllegalStateException("No suitable type converter was found.");
        }
        Pair<Class, Object> value = null;
        try {
            value = converter.writeConvert(field, result);
        } catch (Exception e) {
            value = Pair.of(field.getType(), result);
        }
        return value;
    }
}
