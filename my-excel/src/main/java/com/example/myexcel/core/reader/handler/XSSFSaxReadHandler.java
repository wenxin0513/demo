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

import com.cntaiping.tplhk.reins.common.excel.core.Cell;
import com.cntaiping.tplhk.reins.common.excel.core.ReadContext;
import com.cntaiping.tplhk.reins.common.excel.core.Row;
import com.cntaiping.tplhk.reins.common.excel.core.RowWrapper;
import com.cntaiping.tplhk.reins.common.excel.core.builder.AbstractExcelFactory;
import com.cntaiping.tplhk.reins.common.excel.core.converter.ConverterWarpper;
import com.cntaiping.tplhk.reins.common.excel.core.parser.ExcelHandler;
import com.cntaiping.tplhk.reins.common.excel.core.reader.SaxExcelReader;
import com.cntaiping.tplhk.reins.common.excel.exception.StopReadException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * sax处理
 *
 * @author zhouhong
 * @version 1.0
 */
@Slf4j
public class XSSFSaxReadHandler<T> extends AbstractReadHandler<T> implements XSSFSheetXMLHandler.SheetContentsHandler {

    private Row currentRow;

    private int count;
    private int sheetIndex;
    private String sheetName;

    public XSSFSaxReadHandler(InputStream inputStream, SaxExcelReader.ReadConfig<T> readConfig) throws IOException {
        this.init(inputStream, readConfig);
    }

    public XSSFSaxReadHandler<T> ofIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
        this.sheetName = null;
        return this;
    }

    public XSSFSaxReadHandler<T> ofName(String sheetName) {
        this.sheetIndex = -1;
        this.sheetName = sheetName;
        return this;
    }

    @Override
    public void startRow(int rowNum) {
        currentRow = new Row(rowNum);
        obj = this.newInstance(readConfig.getDataType());
    }

    @Override
    public void endRow(int rowNum) {
        this.initFieldMap(rowNum);
        if (!readConfig.getRowFilter().test(currentRow)) {
            return;
        }
        if (!readConfig.getBeanFilter().test(obj)) {
            return;
        }
        count++;
        RowWrapper<T> wrapper = (RowWrapper<T>) RowWrapper.builder()
                .rowIndex(rowNum)
                .data(obj)
                .build();
        if (readConfig.getFunction() != null) {
            readConfig.getFunction().reverse(fieldMap, readConfig.getExceptionFunction());
            ExcelHandler handler = readConfig.getFunction().apply(wrapper);
            //回写Excel
            if (!ObjectUtils.isEmpty(handler.getCells())) {
                if (readConfig.getErrorComment()) {
                    hasError=true;
                    handler.getCells().forEach(c ->
                            AbstractExcelFactory.errorComment(
                                    rwb.get(),
                                    sheetIndex,
                                    sheetName,
                                    c.getRowNum(),
                                    c.getColNum(),
                                    c.getErrorMsg()
                            )
                    );
                }
                // todo modify by xujunqiang
                errReadContextList.addAll(handler.getCells());
            }
            if (handler.isStop()) {
                throw new StopReadException("停止读取");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void cell(String cellReference, String formattedValue,
                     XSSFComment comment) {
        if (cellReference == null) {
            return;
        }
        int thisCol = (new CellReference(cellReference)).getCol();
        formattedValue = readConfig.getTrim().apply(formattedValue);
        this.addTitleConsumer.accept(formattedValue, currentRow.getRowNum(), thisCol);
        if (!readConfig.getRowFilter().test(currentRow)) {
            return;
        }
        if (isMapType) {
            ((Map<Cell, String>) obj).put(new Cell(currentRow.getRowNum(), thisCol), formattedValue);
            return;
        }
        Field field = fieldMap.get(thisCol);
        if (field != null) {
            //读取内容并转换
            ReadContext readContext = ConverterWarpper.readConvert(
                    obj, field, formattedValue, currentRow.getRowNum(), thisCol, readConfig.getExceptionFunction());
            //回写Excel
            if (readConfig.getErrorComment() && StringUtils.isNotEmpty(readContext.getErrorMsg())) {
                hasError=true;
                AbstractExcelFactory.errorComment(
                        rwb.get(),
                        sheetIndex,
                        sheetName,
                        readContext.getRowNum(),
                        readContext.getColNum(),
                        readContext.getErrorMsg()
                );
            }
        }
    }

    @Override
    public void endSheet() {
        log.info("Import completed, total number of rows {}", count);
    }
}
