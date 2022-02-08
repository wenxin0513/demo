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
package com.example.myexcel.core.reader;

import com.example.myexcel.core.ReadContext;
import com.example.myexcel.core.RowWrapper;
import com.example.myexcel.core.annotation.ExcelColumn;
import com.example.myexcel.core.builder.AbstractExcelFactory;
import com.example.myexcel.core.constant.Constants;
import com.example.myexcel.core.converter.ConverterWarpper;
import com.example.myexcel.core.function.CallBackFunction;
import com.example.myexcel.core.function.DefaultExceptionFunction;
import com.example.myexcel.core.function.ExceptionFunction;
import com.example.myexcel.core.parser.ExcelHandler;
import com.example.myexcel.exception.ExcelReadException;
import com.example.myexcel.exception.SaxReadException;
import com.example.myexcel.utils.ReflectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 一般导入（不支持csv文件导入，支持图片读取）
 *
 * @author zhouhong
 * @version 1.0
 */
@Slf4j
public class DefaultExcelReader<T> {

    private static final int DEFAULT_SHEET_INDEX = 0;

    private Class<T> dataType;

    private int sheetIndex = DEFAULT_SHEET_INDEX;

    private Predicate<Row> rowFilter = row -> true;

    private Predicate<T> beanFilter = bean -> true;

    private Workbook wb;

    protected WeakReference<Workbook> rwb;

    private ExceptionFunction exceptionFunction = new DefaultExceptionFunction();

    private Map<String, XSSFPicture> xssfPicturesMap = Collections.emptyMap();

    private Map<String, HSSFPicture> hssfPictureMap = Collections.emptyMap();

    private boolean isXSSFSheet;

    private String sheetName;

    private boolean errorComment = false;

    private boolean hasError = false;

    private Function<String, String> trim = v -> {
        if (v == null) {
            return v;
        }
        return v.trim();
    };

    private DefaultExcelReader(Class<T> dataType) {
        this.dataType = dataType;
    }

    public static <T> DefaultExcelReader<T> of(@NonNull Class<T> clazz) {
        return new DefaultExcelReader<>(clazz);
    }

    public DefaultExcelReader<T> sheet(int index) {
        if (index >= 0) {
            this.sheetIndex = index;
        } else {
            throw new IllegalArgumentException("Sheet index must be greater than or equal to 0");
        }
        return this;
    }

    public DefaultExcelReader<T> sheet(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public DefaultExcelReader<T> rowFilter(@NonNull Predicate<Row> rowFilter) {
        this.rowFilter = rowFilter;
        return this;
    }

    public DefaultExcelReader<T> beanFilter(@NonNull Predicate<T> beanFilter) {
        this.beanFilter = beanFilter;
        return this;
    }

    public DefaultExcelReader<T> exceptionally(ExceptionFunction exceptionFunction) {
        this.exceptionFunction = exceptionFunction;
        return this;
    }

    public DefaultExcelReader<T> noTrim() {
        this.trim = v -> v;
        return this;
    }

    public DefaultExcelReader<T> errorComment() {
        this.errorComment = true;
        return this;
    }

    public Workbook errorWorkBook() {
        if (errorComment && hasError) {
            return this.rwb.get();
        }
        return null;
    }

    /**
     * 按照Excel指定位置读取内容
     *
     * @param file
     * @return
     * @throws Exception
     */
    public T readLocate(@NonNull File file) throws Exception {
        return readLocate(file, null);
    }

    /**
     * 按照Excel指定位置读取内容
     *
     * @param fileInputStream
     * @return
     * @throws Exception
     */
    public T readLocate(@NonNull InputStream fileInputStream) throws Exception {
        return readLocate(fileInputStream, null);
    }

    /**
     * 按照Excel指定位置读取内容
     *
     * @param file
     * @param password
     * @return
     * @throws Exception
     */
    public T readLocate(@NonNull File file, String password) throws Exception {
        checkFileSuffix(file);
        Map<Integer, Field> fieldMap = ReflectUtil.getFieldMapOfExcelColumn(dataType);
        if (fieldMap.isEmpty()) {
            return null;
        }
        try {
            Sheet sheet = getSheetOfFile(file, password);
            return getDataByFields(sheet, fieldMap);
        } finally {
            clearWorkbook();
        }
    }

    /**
     * 按照Excel指定位置读取内容
     *
     * @param fileInputStream
     * @param password
     * @return
     * @throws Exception
     */
    public T readLocate(@NonNull InputStream fileInputStream, String password) throws Exception {
        Map<Integer, Field> fieldMap = ReflectUtil.getFieldMapOfExcelColumn(dataType);
        if (fieldMap.isEmpty()) {
            return null;
        }
        try {
            Sheet sheet = getSheetOfInputStream(fileInputStream, password);
            return getDataByFields(sheet, fieldMap);
        } finally {
            clearWorkbook();
        }
    }

    public DefaultExcelReader<T> read(@NonNull InputStream fileInputStream, CallBackFunction<T> function) throws Exception {
        return read(fileInputStream, null, function);
    }

    public DefaultExcelReader<T> read(@NonNull InputStream fileInputStream, String password, CallBackFunction<T> function) throws Exception {
        Map<Integer, Field> fieldMap = ReflectUtil.getFieldMapOfExcelColumn(dataType);
        if (fieldMap.isEmpty()) {
            return this;
        }
        try {
            Sheet sheet = getSheetOfInputStream(fileInputStream, password);
            return getDataFromFile(sheet, fieldMap, function);
        } finally {
            if (!errorComment) {
                clearWorkbook();
            }
        }
    }

    public DefaultExcelReader<T> read(@NonNull File file, CallBackFunction<T> function) throws Exception {
        return read(file, null, function);
    }

    public DefaultExcelReader<T> read(@NonNull File file, String password, CallBackFunction<T> function) throws Exception {
        checkFileSuffix(file);
        Map<Integer, Field> fieldMap = ReflectUtil.getFieldMapOfExcelColumn(dataType);
        if (fieldMap.isEmpty()) {
            return this;
        }
        try {
            Sheet sheet = getSheetOfFile(file, password);
            return getDataFromFile(sheet, fieldMap, function);
        } finally {
            if (!errorComment) {
                clearWorkbook();
            }
        }
    }

    private void checkFileSuffix(@NonNull File file) {
        if (!file.getName().endsWith(Constants.XLSX) && !file.getName().endsWith(Constants.XLS)) {
            throw new IllegalArgumentException("Support only. xls and. xlsx suffix files");
        }
    }

    private void clearWorkbook() throws IOException {
        if (Objects.nonNull(wb)) {
            wb.close();
        }
    }

    private Sheet getSheetOfInputStream(@NonNull InputStream fileInputStream, String password) throws IOException {
        if (StringUtils.isEmpty(password)) {
            wb = WorkbookFactory.create(fileInputStream);
            if (errorComment) {
                this.rwb = new WeakReference<>(wb);
            }
        } else {
            wb = WorkbookFactory.create(fileInputStream, password);
            if (errorComment) {
                this.rwb = new WeakReference<>(wb);
            }
        }
        return getSheet();
    }

    private Sheet getSheetOfFile(@NonNull File file, String password) throws IOException {
        if (StringUtils.isEmpty(password)) {
            wb = WorkbookFactory.create(file);
            if (errorComment) {
                this.rwb = new WeakReference<>(wb);
            }
        } else {
            wb = WorkbookFactory.create(file, password);
            if (errorComment) {
                this.rwb = new WeakReference<>(wb);
            }
        }
        return getSheet();
    }

    private Sheet getSheet() {
        Sheet sheet;
        if (sheetName != null) {
            sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new ExcelReadException("Cannot find sheet based on sheetName:" + sheetName);
            }
        } else {
            sheet = wb.getSheetAt(sheetIndex);
        }
        getAllPictures(sheet);
        return sheet;
    }

    private T getDataByFields(Sheet sheet, Map<Integer, Field> fieldMap) {
        long startTime = System.currentTimeMillis();
        final int firstRowNum = sheet.getFirstRowNum();
        final int lastRowNum = sheet.getLastRowNum();
        log.info("FirstRowNum:{},LastRowNum:{}", firstRowNum, lastRowNum);
        if (lastRowNum < 0) {
            log.info("Reading excel takes {} milliseconds", System.currentTimeMillis() - startTime);
            return null;
        }
        DataFormatter formatter = new DataFormatter();
        try {
            final T obj = dataType.newInstance();
            fieldMap.forEach((key, field) -> {
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                if (excelColumn == null) {
                    return;
                }
                int locate = excelColumn.locate(), index = excelColumn.index(), size = excelColumn.size();
                if (index >= 0) {
                    Row row = sheet.getRow(locate);
                    Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell == null) {
                        return;
                    }
                    String content = trim.apply(formatter.formatCellValue(cell));
                    ConverterWarpper.readConvert(obj, field, content, row.getRowNum(), index, exceptionFunction);
                }
                if (size > 0) {
                    List<List<String>> x = Lists.newArrayList();
                    for (int i = locate; i <= lastRowNum; i++) {
                        Row row = sheet.getRow(i);
                        if (row == null) {
                            log.info("Row of {} is null,it will be ignored.", i);
                            continue;
                        }
                        boolean noMatchResult = rowFilter.negate().test(row);
                        if (noMatchResult) {
                            log.info("Row of {} does not meet the filtering criteria, it will be ignored.", i);
                            continue;
                        }
                        int lastColNum = row.getLastCellNum();
                        if (lastColNum < 0) {
                            continue;
                        }
                        List<String> y = Lists.newArrayList();
                        for (int c = 0; c < lastColNum; c++) {
                            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            if (cell == null) {
                                return;
                            }
                            String content = formatter.formatCellValue(cell);
                            content = trim.apply(content);
                            y.add(content);
                        }
                        x.add(y);
                    }
                    try {
                        field.set(obj, x);
                    } catch (IllegalAccessException e) {
                        throw new SaxReadException("Failed to set the " + field.getName() + " field value to " + x, e);
                    }
                }
                log.info("Reading excel takes {} milliseconds", System.currentTimeMillis() - startTime);
            });
            return obj;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private DefaultExcelReader<T> getDataFromFile(Sheet sheet, Map<Integer, Field> fieldMap, CallBackFunction<T> function) {
        long startTime = System.currentTimeMillis();
        final int firstRowNum = sheet.getFirstRowNum();
        final int lastRowNum = sheet.getLastRowNum();
        log.info("FirstRowNum:{},LastRowNum:{}", firstRowNum, lastRowNum);
        if (lastRowNum < 0) {
            log.info("Reading excel takes {} milliseconds", System.currentTimeMillis() - startTime);
            return this;
        }
        DataFormatter formatter = new DataFormatter();
        for (int i = firstRowNum; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                log.info("Row of {} is null,it will be ignored.", i);
                continue;
            }
            boolean noMatchResult = rowFilter.negate().test(row);
            if (noMatchResult) {
                log.info("Row of {} does not meet the filtering criteria, it will be ignored.", i);
                continue;
            }
            int lastColNum = row.getLastCellNum();
            if (lastColNum < 0) {
                continue;
            }
            T obj = instanceObj(fieldMap, formatter, sheet, row);
            if (beanFilter.test(obj)) {
                RowWrapper<T> wrapper = (RowWrapper<T>) RowWrapper.builder()
                        .sheetIndex(sheetIndex)
                        .rowIndex(row.getRowNum())
                        .data(obj)
                        .build();
                if (function != null) {
                    function.reverse(fieldMap, exceptionFunction);
                    ExcelHandler handler = function.apply(wrapper);
                    //回写Excel
                    if (!ObjectUtils.isEmpty(handler.getCells())) {
                        if (errorComment) {
                            hasError = true;
                            handler.getCells().forEach(c ->
                                    AbstractExcelFactory.errorComment(
                                            rwb.get(),
                                            sheet, row.getRowNum(),
                                            c.getColNum(),
                                            c.getErrorMsg()
                                    )
                            );
                        }
                    }
                    if (handler.isStop()) {
                        break;
                    }
                }
            }
        }
        log.info("Reading excel takes {} milliseconds", System.currentTimeMillis() - startTime);
        return this;
    }

    private void getAllPictures(Sheet sheet) {
        if (sheet instanceof XSSFSheet) {
            isXSSFSheet = true;
            XSSFDrawing xssfDrawing = ((XSSFSheet) sheet).getDrawingPatriarch();
            if (xssfDrawing == null) {
                return;
            }
            xssfPicturesMap = xssfDrawing.getShapes()
                    .stream()
                    .map(s -> (XSSFPicture) s)
                    .collect(Collectors.toMap(s -> {
                        XSSFClientAnchor anchor = (XSSFClientAnchor) s.getAnchor();
                        return anchor.getRow1() + "_" + anchor.getCol1();
                    }, s -> s));
        } else if (sheet instanceof HSSFSheet) {
            HSSFPatriarch hssfPatriarch = ((HSSFSheet) sheet).getDrawingPatriarch();
            if (hssfPatriarch == null) {
                return;
            }
            Spliterator<HSSFShape> spliterator = hssfPatriarch.spliterator();
            hssfPictureMap = Maps.newHashMap();
            spliterator.forEachRemaining(shape -> {
                if (shape instanceof HSSFPicture) {
                    HSSFPicture picture = (HSSFPicture) shape;
                    HSSFAnchor anchor = picture.getAnchor();
                    if (anchor instanceof HSSFClientAnchor) {
                        int row = ((HSSFClientAnchor) anchor).getRow1();
                        int col = ((HSSFClientAnchor) anchor).getCol1();
                        hssfPictureMap.put(row + "_" + col, picture);
                    }
                }
            });
        }
    }

    private T instanceObj(Map<Integer, Field> fieldMap, DataFormatter formatter, Sheet sheet, Row row) {
        T obj;
        try {
            obj = dataType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        fieldMap.forEach((index, field) -> {
            if (field.getType() == InputStream.class) {
                convertPicture(row, obj, index, field);
                return;
            }
            Cell cell = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String content = trim.apply(formatter.formatCellValue(cell));

            ReadContext readContext = ConverterWarpper.readConvert(obj, field, content, row.getRowNum(), index, exceptionFunction);
            if (errorComment && null != readContext.getErrorMsg()) {
                hasError = true;
                AbstractExcelFactory.errorComment(
                        rwb.get(), sheet,
                        row.getRowNum(),
                        cell.getColumnIndex(),
                        readContext.getErrorMsg()
                );
            }
        });
        return obj;
    }

    private void convertPicture(Row row, T obj, Integer index, Field field) {
        byte[] pictureData;
        if (isXSSFSheet) {
            XSSFPicture xssfPicture = xssfPicturesMap.get(row.getRowNum() + "_" + index);
            if (xssfPicture == null) {
                return;
            }
            pictureData = xssfPicture.getPictureData().getData();
        } else {
            HSSFPicture hssfPicture = hssfPictureMap.get(row.getRowNum() + "_" + index);
            if (hssfPicture == null) {
                return;
            }
            pictureData = hssfPicture.getPictureData().getData();
        }
        try {
            field.set(obj, new ByteArrayInputStream(pictureData));
        } catch (IllegalAccessException e) {
            throw new ExcelReadException("Failed to read picture.", e);
        }
    }
}
