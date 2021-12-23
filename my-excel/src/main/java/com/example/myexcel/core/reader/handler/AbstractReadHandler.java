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
package com.example.myexcel.core.reader.handler;


import com.cntaiping.tplhk.reins.common.excel.core.AddTitleConsumer;
import com.cntaiping.tplhk.reins.common.excel.core.Cell;
import com.cntaiping.tplhk.reins.common.excel.core.ReadContext;
import com.cntaiping.tplhk.reins.common.excel.core.reader.SaxExcelReader;
import com.cntaiping.tplhk.reins.common.excel.utils.ReflectUtil;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取抽象
 *
 * @author zhouhong
 * @version 1.0
 */
public abstract class AbstractReadHandler<T> {

    protected boolean isMapType;

    protected boolean hasError=false;

    protected Map<Integer, Field> fieldMap;

    protected T obj;

    protected Map<String, Integer> titles = Maps.newHashMap();

    protected SaxExcelReader.ReadConfig<T> readConfig;

    protected WeakReference<Workbook> rwb;

    protected AddTitleConsumer<String, Integer, Integer> addTitleConsumer = (v, rowNum, colNum) -> {
    };

    protected List<ReadContext> errReadContextList = Lists.newArrayList();

    protected void init(InputStream inputStream, SaxExcelReader.ReadConfig<T> readConfig) {
        this.readConfig = readConfig;
        fieldMap = ReflectUtil.getFieldMapOfExcelColumn(readConfig.getDataType());
        if (fieldMap.isEmpty()) {
            addTitleConsumer = this::addTitles;
        }
        if (Boolean.TRUE.equals(this.readConfig.getErrorComment())) {
            try {
                this.rwb = new WeakReference<>(WorkbookFactory.create(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    T newInstance(Class<T> clazz) {
        if (isMapType) {
            return (T) new LinkedHashMap<Cell, String>();
        }
        if (clazz == Map.class) {
            isMapType = true;
            return (T) new LinkedHashMap<Cell, String>();
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void initFieldMap(int rowNum) {
        if (rowNum != 0 || !fieldMap.isEmpty()) {
            return;
        }
        Map<String, Field> titleFieldMap = ReflectUtil.getFieldMapOfTitleExcelColumn(readConfig.getDataType());
        fieldMap = Maps.newHashMapWithExpectedSize(titleFieldMap.size());
        titles.forEach((k, v) -> {
            fieldMap.put(v, titleFieldMap.get(k));
        });
    }

    private void addTitles(String formattedValue, int rowNum, int thisCol) {
        if (rowNum == 0) {
            titles.put(formattedValue, thisCol);
        }
    }

    public Workbook errorWorkbook() {
        return this.rwb.get();
    }

    public List<ReadContext> errReadContextList() {
        return this.errReadContextList;
    }

    public boolean hasError(){
        return hasError;
    }
}
