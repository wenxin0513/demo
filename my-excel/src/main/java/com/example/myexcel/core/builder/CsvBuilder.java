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
package com.example.myexcel.core.builder;

import com.example.myexcel.core.Csv;
import com.example.myexcel.core.annotation.ExcelColumn;
import com.example.myexcel.core.annotation.ExcelTable;
import com.example.myexcel.core.annotation.ExcludeColumn;
import com.example.myexcel.core.constant.Constants;
import com.example.myexcel.core.container.Pair;
import com.example.myexcel.core.container.ParallelContainer;
import com.example.myexcel.core.reflect.ClassFieldContainer;
import com.example.myexcel.exception.CsvBuildException;
import com.example.myexcel.utils.ReflectUtil;
import com.example.myexcel.utils.TempFileOperator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * CSV文件构建器
 * 只支持Bean列表方式导出csv文件
 * 与excel导出注解相比，width、style属性将不被支持
 *
 * @author zhouhong
 * @version 1.0
 */
public class CsvBuilder<T> implements Closeable {

    private static final Pattern PATTERN_QUOTES_PREMISE = Pattern.compile("[,\"]+");

    private static final Pattern PATTERN_QUOTES = Pattern.compile("\"");

    private String globalDefaultValue;
    /**
     * 默认值集合
     */
    private Map<Field, String> defaultValueMap;
    /**
     * 标题
     */
    private List<String> titles;

    private List<Field> fields;
    /**
     * 文件路径
     */
    private Csv csv;

    private CsvBuilder() {
    }

    public static <T> CsvBuilder<T> of(Class<T> clazz) {
        CsvBuilder<T> csvBuilder = new CsvBuilder<>();
        ClassFieldContainer classFieldContainer = ReflectUtil.getAllFieldsOfClass(clazz);
        csvBuilder.fields = csvBuilder.getFields(classFieldContainer);
        Path csvTemp = TempFileOperator.createTempFile("d_t_c", Constants.CSV);
        csvBuilder.csv = new Csv(csvTemp);
        return csvBuilder;
    }

    public CsvBuilder<T> groups(Class<?>... groups) {
        fields = this.getGroupFields(fields, groups);
        return this;
    }

    public CsvBuilder<T> noTitles() {
        this.titles = null;
        return this;
    }

    public Csv build(List<T> beans) {
        return this.build(beans, csv);
    }

    public void append(List<T> beans) {
        this.build(beans, csv);
    }

    public Csv build() {
        return csv;
    }

    private Csv build(List<T> beans, Csv csv) {
        try {
            if (beans == null || beans.isEmpty()) {
                return csv;
            }
            List<List<?>> contents = getRenderContent(beans, fields);
            this.writeToCsv(contents, csv);
        } catch (Exception e) {
            TempFileOperator.deleteTempFile(csv.getFilePath());
            throw new CsvBuildException("Build csv failure", e);
        }
        return csv;
    }

    private List<Field> getFields(ClassFieldContainer classFieldContainer, Class<?>... groups) {
        ExcelTable excelTable = classFieldContainer.getClazz().getAnnotation(ExcelTable.class);
        boolean excelTableExist = Objects.nonNull(excelTable);
        boolean excludeParent = false;
        boolean includeAllField = false;
        boolean ignoreStaticFields = true;
        if (excelTableExist) {
            excludeParent = excelTable.excludeParent();
            includeAllField = excelTable.includeAllField();
            if (!excelTable.defaultValue().isEmpty()) {
                globalDefaultValue = excelTable.defaultValue();
            }
            ignoreStaticFields = excelTable.ignoreStaticFields();
        }
        List<Field> preElectionFields = this.getPreElectionFields(classFieldContainer, excludeParent, includeAllField);
        if (ignoreStaticFields) {
            preElectionFields = preElectionFields.stream()
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .collect(Collectors.toList());
        }
        boolean useFieldNameAsTitle = excelTableExist && excelTable.useFieldNameAsTitle();
        List<Field> sortedFields = getGroupFields(preElectionFields, groups);
        List<String> titles = Lists.newArrayListWithCapacity(preElectionFields.size());
        defaultValueMap = Maps.newHashMapWithExpectedSize(preElectionFields.size());
        boolean needToAddTitle = this.titles == null;
        for (Field field : sortedFields) {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (excelColumn != null) {
                if (needToAddTitle) {
                    if (useFieldNameAsTitle && excelColumn.title().isEmpty()) {
                        titles.add(field.getName());
                    } else {
                        titles.add(excelColumn.title());
                    }
                }
                if (!excelColumn.defaultValue().isEmpty()) {
                    defaultValueMap.put(field, excelColumn.defaultValue());
                }
            } else {
                if (needToAddTitle) {
                    if (useFieldNameAsTitle) {
                        titles.add(field.getName());
                    } else {
                        titles.add(null);
                    }
                }
            }
        }
        boolean hasTitle = titles.stream().anyMatch(StringUtils::isNotBlank);
        if (hasTitle) {
            this.titles = titles;
        }
        return sortedFields;
    }

    private List<Field> getGroupFields(List<Field> preElectionFields, Class<?>[] groups) {
        List<Class<?>> selectedGroupList = Objects.nonNull(groups) ? Arrays.stream(groups).filter(Objects::nonNull).collect(Collectors.toList()) : Collections.emptyList();
        return preElectionFields.stream()
                .filter(field -> !field.isAnnotationPresent(ExcludeColumn.class) && ReflectUtil.isFieldSelected(selectedGroupList, field))
                .sorted(ReflectUtil::sortFields)
                .collect(Collectors.toList());
    }

    private List<Field> getPreElectionFields(ClassFieldContainer classFieldContainer, boolean excludeParent, boolean includeAllField) {
        if (includeAllField) {
            if (excludeParent) {
                return classFieldContainer.getDeclaredFields();
            } else {
                return classFieldContainer.getFields();
            }
        }
        if (excludeParent) {
            return classFieldContainer.getDeclaredFields().stream()
                    .filter(field -> field.isAnnotationPresent(ExcelColumn.class)).collect(Collectors.toList());
        } else {
            return classFieldContainer.getFieldsByAnnotation(ExcelColumn.class);
        }
    }

    /**
     * 获取需要被渲染的内容
     *
     * @param data         数据集合
     * @param sortedFields 排序字段
     * @return 结果集
     */
    private List<List<?>> getRenderContent(List<T> data, List<Field> sortedFields) {
        List<ParallelContainer> resolvedDataContainers = IntStream.range(0, data.size()).parallel().mapToObj(index -> {
            List<?> resolvedDataList = this.getRenderContent(data.get(index), sortedFields);
            return new ParallelContainer<>(index, resolvedDataList);
        }).collect(Collectors.toCollection(LinkedList::new));
        data.clear();

        // 重排序
        return resolvedDataContainers.stream()
                .sorted(Comparator.comparing(ParallelContainer::getIndex))
                .map(ParallelContainer<List<Pair<? extends Class, ?>>>::getData).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * 获取需要被渲染的内容
     *
     * @param data         数据集合
     * @param sortedFields 排序字段
     * @return 结果集
     */
    private List<?> getRenderContent(T data, List<Field> sortedFields) {
        //TODO 启动报错 临时屏蔽
//        return sortedFields.stream()
//                .map(field -> {
//                    Pair<? extends Class, Object> value = ConverterWarpper.writeConvert(field, data);
//                    if (value.getValue() != null) {
//                        return value;
//                    }
//                    String defaultValue = defaultValueMap.get(field);
//                    if (defaultValue != null) {
//                        return Pair.of(field.getType(), defaultValue);
//                    }
//                    if (globalDefaultValue != null) {
//                        return Pair.of(field.getType(), globalDefaultValue);
//                    }
//                    return value;
//                })
//                .map(Pair::getValue)
//                .collect(Collectors.toCollection(LinkedList::new));
        return null;
    }

    private void writeToCsv(List<List<?>> data, Csv csv) {
        if (titles != null) {
            data.add(0, titles);
            titles = null;
        }
        List<String> content = data.stream().map(d -> {
            return d.stream().map(v -> {
                if (v == null) {
                    return "";
                }
                String vStr = v.toString();
                vStr = PATTERN_QUOTES.matcher(vStr).replaceAll("\"\"");
                boolean hasComma = PATTERN_QUOTES_PREMISE.matcher(v.toString()).find();
                if (hasComma) {
                    vStr = "\"" + vStr + "\"";
                }
                return vStr;
            }).collect(Collectors.joining(Constants.COMMA));
        }).collect(Collectors.toCollection(LinkedList::new));

        try {
            Files.write(csv.getFilePath(), content, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        clear();
    }

    public void clear() {
        if (csv != null) {
            csv.clear();
        }
    }
}
