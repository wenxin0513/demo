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

import com.cntaiping.tplhk.reins.common.excel.core.constant.FileType;
import com.cntaiping.tplhk.reins.common.excel.core.constant.LinkType;
import com.cntaiping.tplhk.reins.common.excel.core.converter.ConverterProxy;
import com.cntaiping.tplhk.reins.common.excel.core.converter.ExcelRepository;
import com.cntaiping.tplhk.reins.common.excel.core.converter.ReadConverter;
import com.cntaiping.tplhk.reins.common.excel.core.converter.WriteConverter;

import java.lang.annotation.*;

/**
 * @author zhouhong
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ExcelColumn {
    /**
     * 列标题
     *
     * @return 标题
     */
    String title() default "";

    /**
     * 行索引，
     * 配合列索引，可以指定读取Excel中固定位置的内容；
     * 不配合列索引，则表示读取当前指定行的所有数据；
     *
     * @return
     */
    int locate() default -1;

    /**
     * 列索引，从零开始，不允许重复，数值越大越靠后
     * 不指定表示从Excel忽略读取该字段
     *
     * @return int
     */
    int index() default -1;

    /**
     * 读取指定行数
     * -1:不指定
     *
     * @return
     */
    int size() default -1;

    /**
     * @return 是否必填
     */
    boolean required() default false;

    /**
     * 批注信息, 生成模板时生效
     *
     * @return
     */
    String comment() default "";

    /**
     * 时间格式化，如yyyy-MM-dd HH:mm:ss
     *
     * @return 时间格式化
     */
    String dateFormatPattern() default "";

    /**
     * 校验日期是否存在
     * @return
     */
    boolean isValidDate() default false;
    /**
     * 分组
     *
     * @return 分组类类型集合
     */
    Class<?>[] groups() default {};

    /**
     * 为null时默认值
     *
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * @return 最大长度, 读取时生效, 默认不限制
     */
    int maxLength() default -1;

    /**
     * 宽度
     *
     * @return 宽度
     */
    int width() default -1;

    /**
     * 是否强制转换成字符串
     *
     * @return 是否强制转换成字符串
     */
    boolean convertToString() default false;

    /**
     * 小数格式化
     *
     * @return 格式化
     */
    String decimalFormat() default "";

    /**
     * 样式
     *
     * @return 样式集合
     */
    String[] style() default {};

    /**
     * 链接
     *
     * @return linkType
     */
    LinkType linkType() default LinkType.NONE;

    /**
     * 简单映射，如"1:男,2:女"
     *
     * @return String
     */
    String mapping() default "";

    /**
     * 文件类型
     *
     * @return 文件类型
     */
    FileType fileType() default FileType.NONE;




    /**
     * 自定义读取转换器
     *
     * @return
     */
    Class<? extends ReadConverter> readConverter() default ConverterProxy.class;

    /**
     * 自定义写入转换器
     *
     * @return
     */
    Class<? extends WriteConverter> writeConverter() default ConverterProxy.class;

    /**
     * @return 下拉框数据源, 生成模板和验证数据时生效
     */
    Class<? extends ExcelRepository> repository() default ConverterProxy.class;

}
