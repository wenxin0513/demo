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
package com.example.myexcel.core.converter;


import com.cntaiping.tplhk.reins.common.excel.core.container.Pair;

import java.lang.reflect.Field;

/**
 * 写Excel转换器
 * @author harry
 * @version 1.0
 */
public interface WriteConverter<E, T> {

    /**
     * 写Excel转换
     *
     * @param field    字段
     * @param fieldVal 字段对应的值
     * @return T
     */
    Pair<Class, T> writeConvert(Field field, E fieldVal);

    /**
     * 是否支持转换
     *
     * @param field    字段
     * @param fieldVal 字段值
     * @return true/false
     */
    boolean support(Field field, E fieldVal);

}

