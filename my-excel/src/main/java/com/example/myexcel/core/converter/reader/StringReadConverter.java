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
package com.example.myexcel.core.converter.reader;

import com.cntaiping.tplhk.reins.common.excel.core.converter.ReadConverter;

import java.lang.reflect.Field;

/**
 * String读取转换器
 *
 * @author zhouhong
 * @version 1.0
 */
public class StringReadConverter implements ReadConverter<String, String> {

    @Override
    public String readConvert(Field field, String obj) {
        return obj;
    }
}