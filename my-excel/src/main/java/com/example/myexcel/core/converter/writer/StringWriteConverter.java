package com.example.myexcel.core.converter.writer;



import com.example.myexcel.core.annotation.ExcelColumn;
import com.example.myexcel.core.container.Pair;
import com.example.myexcel.core.converter.WriteConverter;

import java.lang.reflect.Field;

/**
 * @author simon
 * @version 2.5.0
 */
public class StringWriteConverter implements WriteConverter {

    @Override
    public boolean support(Field field, Object fieldVal) {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        return excelColumn != null && excelColumn.convertToString();
    }

    @Override
    public Pair<Class, Object> writeConvert(Field field, Object fieldVal) {
        return Pair.of(String.class, fieldVal.toString());
    }
}
