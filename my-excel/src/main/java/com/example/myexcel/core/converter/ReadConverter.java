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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either exps or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.myexcel.core.converter;

import com.cntaiping.tplhk.reins.common.excel.exception.ExcelReadConvertException;

import java.lang.reflect.Field;

/**
 * 读Excel转换接口
 *
 * @author harry
 * @version 1.0
 */
public interface ReadConverter<E, T> {

    /**
     * 读Excel转换
     *
     * @param obj   被转换对象
     * @param field 字段，提供额外信息
     * @return 转换结果
     */
    T readConvert(Field field, E obj) throws ExcelReadConvertException;
}