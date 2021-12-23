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
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * HSSF sax处理
 *
 * @author zhouhong
 * @version 1.0
 */
@Slf4j
public class HSSFSaxReadHandler<T> extends AbstractReadHandler<T> implements HSSFListener {

    private Row currentRow;

    private String sheetName;

    private POIFSFileSystem fs;

    private int lastRowNumber = -1;

    /**
     * Should we output the formula, or the value it has?
     */
    private boolean outputFormulaValues = true;

    /**
     * For parsing Formulas
     */
    private EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;
    private HSSFWorkbook stubWorkbook;

    // Records we pick up as we process
    private SSTRecord sstRecord;
    private FormatTrackingHSSFListener formatListener;

    /**
     * So we known which sheet we're on
     */
    private int sheetIndex = -1;
    private BoundSheetRecord[] orderedBSRs;
    private List<BoundSheetRecord> boundSheetRecords = Lists.newArrayList();

    // For handling formulas with string results
    private int nextRow;
    private int nextColumn;
    private boolean outputNextStringRecord;

    public HSSFSaxReadHandler(InputStream inputStream,
                              SaxExcelReader.ReadConfig<T> readConfig) throws IOException {
        this.fs = new POIFSFileSystem(inputStream);
        this.init(inputStream, readConfig);
    }

    public void process() throws IOException {
        long startTime = System.currentTimeMillis();
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);

        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();

        if (outputFormulaValues) {
            request.addListenerForAllRecords(formatListener);
        } else {
            workbookBuildingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(formatListener);
            request.addListenerForAllRecords(workbookBuildingListener);
        }

        factory.processWorkbookEvents(request, fs);
        log.info("Sax import takes {} ms", System.currentTimeMillis() - startTime);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processRecord(Record record) {
        int thisRow = -1;
        int thisColumn = -1;
        String thisStr = null;

        switch (record.getSid()) {
            case BoundSheetRecord.sid:
                boundSheetRecords.add((BoundSheetRecord) record);
                break;
            case BOFRecord.sid:
                BOFRecord br = (BOFRecord) record;
                if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
                    if (workbookBuildingListener != null && stubWorkbook == null) {
                        stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
                    }
                    sheetIndex++;
                    obj = null;
                    lastRowNumber = -1;
                    if (orderedBSRs == null) {
                        orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
                    }
                    sheetName = orderedBSRs[sheetIndex].getSheetname();
                }
                break;
            case SSTRecord.sid:
                sstRecord = (SSTRecord) record;
                break;
            case BlankRecord.sid:
                BlankRecord brec = (BlankRecord) record;

                thisRow = brec.getRow();
                thisColumn = brec.getColumn();
                thisStr = null;
                break;
            case BoolErrRecord.sid:
                BoolErrRecord berec = (BoolErrRecord) record;

                thisRow = berec.getRow();
                thisColumn = berec.getColumn();
                thisStr = berec.isBoolean() ? String.valueOf(berec.getBooleanValue()) : null;
                break;
            case FormulaRecord.sid:
                FormulaRecord frec = (FormulaRecord) record;
                thisRow = frec.getRow();
                thisColumn = frec.getColumn();

                if (outputFormulaValues) {
                    if (Double.isNaN(frec.getValue())) {
                        // Formula result is a string
                        // This is stored in the next record
                        outputNextStringRecord = true;
                        nextRow = frec.getRow();
                        nextColumn = frec.getColumn();
                    } else {
                        thisStr = formatListener.formatNumberDateCell(frec);
                    }
                } else {
                    thisStr = HSSFFormulaParser.toFormulaString(stubWorkbook, frec.getParsedExpression());
                }
                break;
            case StringRecord.sid:
                if (outputNextStringRecord) {
                    // String for formula
                    StringRecord srec = (StringRecord) record;
                    thisStr = srec.getString();
                    thisRow = nextRow;
                    thisColumn = nextColumn;
                    outputNextStringRecord = false;
                }
                break;
            case LabelRecord.sid:
                LabelRecord lrec = (LabelRecord) record;

                thisRow = lrec.getRow();
                thisColumn = lrec.getColumn();
                thisStr = lrec.getValue();
                break;
            case LabelSSTRecord.sid:
                LabelSSTRecord lsrec = (LabelSSTRecord) record;

                thisRow = lsrec.getRow();
                thisColumn = lsrec.getColumn();
                if (sstRecord == null) {
                    thisStr = null;
                } else {
                    thisStr = sstRecord.getString(lsrec.getSSTIndex()).toString();
                }
                break;
            case NoteRecord.sid:
                NoteRecord nrec = (NoteRecord) record;
                thisRow = nrec.getRow();
                thisColumn = nrec.getColumn();
                thisStr = null;
                break;
            case NumberRecord.sid:
                NumberRecord numrec = (NumberRecord) record;

                thisRow = numrec.getRow();
                thisColumn = numrec.getColumn();
                // Format
                thisStr = formatListener.formatNumberDateCell(numrec);
                break;
            case RKRecord.sid:
                RKRecord rkrec = (RKRecord) record;
                thisRow = rkrec.getRow();
                thisColumn = rkrec.getColumn();
                thisStr = null;
                break;
            default:
                break;
        }

        // Handle missing column
        if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
            thisRow = mc.getRow();
            thisColumn = mc.getColumn();
            thisStr = null;
        }
        thisStr = readConfig.getTrim().apply(thisStr);
        this.addTitleConsumer.accept(thisStr, thisRow, thisColumn);

        // Handle new row
        if (thisRow != -1 && thisRow != lastRowNumber) {
            lastRowNumber = thisRow;
            currentRow = new Row(thisRow);
            obj = this.newInstance(readConfig.getDataType());
        }

        if (thisStr != null) {
            if (readConfig.getRowFilter().test(currentRow)) {
                if (isMapType) {
                    ((Map<Cell, String>) obj).put(new Cell(currentRow.getRowNum(), thisColumn), thisStr);
                } else {
                    Field field = fieldMap.get(thisColumn);
                    if (field != null) {
                        //读取内容并转换
                        ReadContext readContext = ConverterWarpper.readConvert(
                                obj, field, thisStr, currentRow.getRowNum(), thisColumn, readConfig.getExceptionFunction());
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
            }
        }

        // Handle end of row
        if (record instanceof LastCellOfRowDummyRecord) {
            if (!readConfig.getSheetNames().isEmpty()) {
                if (!readConfig.getSheetNames().contains(sheetName)) {
                    this.titles.clear();
                    return;
                }
            } else if (!readConfig.getSheetIndexs().contains(sheetIndex)) {
                this.titles.clear();
                return;
            }
            this.initFieldMap(currentRow.getRowNum());
            if (!readConfig.getRowFilter().test(currentRow)) {
                return;
            }
            if (!readConfig.getBeanFilter().test(obj)) {
                return;
            }
            RowWrapper<T> wrapper = (RowWrapper<T>) RowWrapper.builder()
                    .sheetIndex(sheetIndex)
                    .rowIndex(currentRow.getRowNum())
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
                }
                if (handler.isStop()) {
                    throw new StopReadException("停止读取");
                }
            }
        }
    }
}
