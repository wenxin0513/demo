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
package com.example.myexcel.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Field;

/**
 * 读取异常上下文
 *
 * @author zhouhong
 * @version 1.0
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadContext<T> {

    T object;

    Field field;

    Object val;

    int rowNum;

    int colNum;

    String errorMsg;

}
