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
package com.example.myexcel.core.annotation;

import com.example.myexcel.core.WorkbookType;

import java.lang.annotation.*;

/**
 * @author zhouhong
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ExcelTable {

    /**
     * 创建的excel包含所有字段
     *
     * @return true/false
     */
    boolean includeAllField() default true;

    /**
     * 是否忽略父类属性
     *
     * @return true/false
     */
    boolean excludeParent() default false;

    /**
     * 工作簿类型，.xls、.xlsx
     *
     * @return WorkbookType
     */
    WorkbookType workbookType() default WorkbookType.SXLSX;

    /**
     * sheeName
     *
     * @return sheeName
     */
    String sheetName() default "";

    /**
     * 是否使用字段名称作为标题
     *
     * @return true/false
     */
    boolean useFieldNameAsTitle() default false;

    /**
     * 为null时默认值
     *
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * 是否自动换行
     *
     * @return true/false
     */
    boolean wrapText() default true;

    /**
     * 是否过滤静态字段
     *
     * @return true/false
     */
    boolean ignoreStaticFields() default true;

    /**
     * 标题分离器
     *
     * @return 分离器
     */
    String titleSeparator() default "->";

    /**
     * 标题行高度
     *
     * @return 标题行高度
     */
    int titleRowHeight() default -1;

    /**
     * 普通行高度
     *
     * @return 普通行高度
     */
    int rowHeight() default -1;

    /**
     * 样式
     *
     * @return 样式
     */
    String[] style() default {};
}
