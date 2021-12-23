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
package com.example.myexcel.core.parser;

/**
 * 内容类型枚举
 *
 * @author zhouhong
 * @version 1.0
 */
public enum ContentTypeEnum {

    STRING,

    BOOLEAN,

    DOUBLE,

    DATE,

    DROP_DOWN_LIST,

    NUMBER_DROP_DOWN_LIST,

    BOOLEAN_DROP_DOWN_LIST,

    LINK_EMAIL,

    LINK_URL,

    IMAGE;

    public static boolean isString(ContentTypeEnum contentTypeEnum) {
        return STRING == contentTypeEnum;
    }

    public static boolean isBool(ContentTypeEnum contentTypeEnum) {
        return BOOLEAN == contentTypeEnum;
    }

    public static boolean isDouble(ContentTypeEnum contentTypeEnum) {
        return DOUBLE == contentTypeEnum;
    }

    public static boolean isLink(ContentTypeEnum contentTypeEnum) {
        return LINK_URL == contentTypeEnum || LINK_EMAIL == contentTypeEnum;
    }

}
