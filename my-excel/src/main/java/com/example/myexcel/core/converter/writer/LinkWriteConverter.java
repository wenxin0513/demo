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
import com.example.myexcel.core.constant.LinkEmail;
import com.example.myexcel.core.constant.LinkType;
import com.example.myexcel.core.constant.LinkUrl;
import com.example.myexcel.core.container.Pair;
import com.example.myexcel.core.converter.WriteConverter;

import java.lang.reflect.Field;

/**
 * 超链接写转换器
 *
 * @author zhouhong
 * @version 1.0
 */
public class LinkWriteConverter implements WriteConverter {

    @Override
    public Pair<Class, Object> writeConvert(Field field, Object fieldVal) {
        LinkType linkType = field.getAnnotation(ExcelColumn.class).linkType();
        switch (linkType) {
            case URL:
                return Pair.of(LinkUrl.class, fieldVal);
            case EMAIL:
                return Pair.of(LinkEmail.class, fieldVal);
            default:
                throw new IllegalArgumentException("Illegal linkType type, only URL or email");
        }
    }

    @Override
    public boolean support(Field field, Object fieldVal) {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        return excelColumn != null && !LinkType.NONE.equals(excelColumn.linkType());
    }
}
