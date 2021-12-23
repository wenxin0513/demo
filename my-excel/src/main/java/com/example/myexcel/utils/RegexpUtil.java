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
package com.example.myexcel.utils;

import java.util.regex.Pattern;

/**
 * @author zhouhong
 * @version 1.0
 */
public final class RegexpUtil {

    private static final Pattern PATTERN_COMMA = Pattern.compile(",");

    public static String removeComma(String content) {
        if (content == null) {
            return content;
        }
        return PATTERN_COMMA.matcher(content).replaceAll("");
    }
}
