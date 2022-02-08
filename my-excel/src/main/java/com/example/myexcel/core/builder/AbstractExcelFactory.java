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
package com.example.myexcel.core.builder;

import com.example.myexcel.core.FreezePane;
import com.example.myexcel.core.WorkbookType;
import com.example.myexcel.core.parser.ContentTypeEnum;
import com.example.myexcel.core.parser.HtmlTableParser;
import com.example.myexcel.core.parser.Td;
import com.example.myexcel.core.parser.Tr;
import com.example.myexcel.core.strategy.WidthStrategy;
import com.example.myexcel.core.style.*;
import com.example.myexcel.core.style.BorderStyle;
import com.example.myexcel.utils.TdUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @author zhouhong
 * @version 1.0
 */
public abstract class AbstractExcelFactory implements ExcelFactory {

    protected Workbook workbook;
    /**
     * 每行的单元格最大高度map
     */
    private Map<Integer, Short> maxTdHeightMap = Maps.newHashMap();
    /**
     * 是否使用默认样式
     */
    private boolean useDefaultStyle;
    /**
     * 自定义颜色
     */
    private CustomColor customColor;
    /**
     * 单元格样式映射
     */
    private Map<Map<String, String>, CellStyle> cellStyleMap = Maps.newHashMap();
    /**
     * 样式容器
     */
    private Map<HtmlTableParser.TableTag, CellStyle> defaultCellStyleMap;
    /**
     * 字体map
     */
    private Map<String, Font> fontMap = Maps.newHashMap();
    /**
     * 冻结区域
     */
    private FreezePane[] freezePanes;
    /**
     * 自动宽度策略
     */
    protected WidthStrategy widthStrategy = WidthStrategy.CUSTOM_WIDTH;
    /**
     * 暂存单元格，由后续行认领
     */
    private List<Td> stagingTds = Lists.newLinkedList();

    private CreationHelper createHelper;

    private DataFormat format;

    @Override
    public ExcelFactory useDefaultStyle() {
        this.useDefaultStyle = true;
        return this;
    }

    @Override
    public ExcelFactory freezePanes(FreezePane... freezePanes) {
        this.freezePanes = freezePanes;
        return this;
    }

    @Override
    public ExcelFactory workbookType(WorkbookType workbookType) {
        if (Objects.nonNull(workbook)) {
            return this;
        }
        switch (workbookType) {
            case XLS:
                workbook = new HSSFWorkbook();
                break;
            case XLSX:
                workbook = new XSSFWorkbook();
                break;
            case SXLSX:
                workbook = new SXSSFWorkbook(1);
                break;
            default:
                workbook = new XSSFWorkbook();
        }
        return this;
    }

    @Override
    public ExcelFactory widthStrategy(@NonNull WidthStrategy widthStrategy) {
        this.widthStrategy = widthStrategy;
        return this;
    }

    protected String getRealSheetName(String sheetName) {
        if (sheetName == null) {
            sheetName = "sheet";
        }
        Sheet sheet = workbook.getSheet(sheetName);
        int sort = 1;
        String realSheetName = sheetName;
        while (sheet != null) {
            realSheetName = sheetName + " (" + sort + ")";
            sheet = workbook.getSheet(realSheetName);
            sort++;
        }
        return realSheetName;
    }

    /**
     * 创建行-row
     *
     * @param tr    tr
     * @param sheet sheet
     */
    protected void createRow(Tr tr, Sheet sheet) {
        Row row = sheet.createRow(tr.getIndex());
        if (!tr.isVisibility()) {
            row.setZeroHeight(true);
        }
        stagingTds.stream().filter(blankTd -> Objects.equals(blankTd.getRow(), tr.getIndex())).forEach(td -> {
            if (null == tr.getTdList()) {
                tr.setTdList(Lists.newLinkedList());
            }
            tr.getTdList().add(td);
        });
        for (Td td : tr.getTdList()) {
            this.createCell(td, sheet, row);
            if (td.getRowSpan() == 0) {
                continue;
            }
            for (int i = td.getRow() + 1, rowBound = td.getRowBound(); i <= rowBound; i++) {
                for (int j = td.getCol(), colBound = td.getColBound(); j <= colBound; j++) {
                    stagingTds.add(Td.builder()
                            .row(i)
                            .col(j)
                            .th(td.isTh())
                            .style(td.getStyle())
                            .build());
                }
            }
        }
        // 移除暂存区空白单元格
        stagingTds.removeIf(td -> Objects.equals(td.getRow(), tr.getIndex()));
        if (tr.getHeight() > 0) {
            row.setHeightInPoints(tr.getHeight());
        } else {
            // 设置行高，最小12
            if (maxTdHeightMap.get(row.getRowNum()) == null) {
                row.setHeightInPoints(row.getHeightInPoints() + 5);
            } else {
                row.setHeightInPoints((short) (maxTdHeightMap.get(row.getRowNum()) + 5));
                maxTdHeightMap.remove(row.getRowNum());
            }
        }
    }

    /**
     * 创建单元格
     *
     * @param td         td
     * @param sheet      sheet
     * @param currentRow 当前行
     */
    protected void createCell(Td td, Sheet sheet, Row currentRow) {
        Cell cell;
        if (td.isFormula()) {
            cell = currentRow.createCell(td.getCol(), CellType.FORMULA);
            cell.setCellFormula(td.getContent());
        } else {
            String content = td.getContent();
            switch (td.getTdContentType()) {
                case DOUBLE:
                    cell = currentRow.createCell(td.getCol(), CellType.NUMERIC);
                    if (null != content) {
                        cell.setCellValue(Double.parseDouble(content));
                    }
                    break;
                case BOOLEAN:
                    cell = currentRow.createCell(td.getCol(), CellType.BOOLEAN);
                    if (null != content) {
                        cell.setCellValue(Boolean.parseBoolean(content));
                    }
                    break;
                case NUMBER_DROP_DOWN_LIST:
                    cell = currentRow.createCell(td.getCol(), CellType.NUMERIC);
                    String firstEle = setDropDownList(td, sheet, content);
                    if (firstEle != null) {
                        cell.setCellValue(Double.parseDouble(firstEle));
                    }
                    break;
                case BOOLEAN_DROP_DOWN_LIST:
                    cell = currentRow.createCell(td.getCol(), CellType.BOOLEAN);
                    firstEle = setDropDownList(td, sheet, content);
                    if (firstEle != null) {
                        cell.setCellValue(Boolean.parseBoolean(firstEle));
                    }
                    break;
                case DROP_DOWN_LIST:
                    cell = currentRow.createCell(td.getCol(), CellType.STRING);
                    firstEle = setDropDownList(td, sheet, content);
                    if (firstEle != null) {
                        cell.setCellValue(firstEle);
                    }
                    break;
                case LINK_URL:
                    cell = setLink(td, currentRow, HyperlinkType.URL);
                    break;
                case LINK_EMAIL:
                    cell = setLink(td, currentRow, HyperlinkType.EMAIL);
                    break;
                case IMAGE:
                    cell = currentRow.createCell(td.getCol());
                    setImage(td, sheet);
                    break;
                default:
                    cell = currentRow.createCell(td.getCol(), CellType.STRING);
                    cell.setCellValue(content);
                    break;
            }
        }
        // 设置单元格样式
        this.setCellStyle(currentRow, cell, td);
        if (td.getCol() != td.getColBound()) {
            for (int j = td.getCol() + 1, colBound = td.getColBound(); j <= colBound; j++) {
                cell = currentRow.createCell(j);
                this.setCellStyle(currentRow, cell, td);
            }
        }
        if (td.getColSpan() > 0 || td.getRowSpan() > 0) {
            sheet.addMergedRegion(new CellRangeAddress(td.getRow(), td.getRowBound(), td.getCol(), td.getColBound()));
        }
        //构建备注
        if (!StringUtils.isEmpty(td.getComment())) {
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            CreationHelper factory = workbook.getCreationHelper();
            ClientAnchor anchor = factory.createClientAnchor();
            anchor.setCol1(cell.getColumnIndex());
            anchor.setCol2(cell.getColumnIndex() + 4);
            anchor.setRow1(currentRow.getRowNum());
            anchor.setRow2(currentRow.getRowNum() + 2);
            Comment cellComment = drawing.createCellComment(anchor);

            XSSFRichTextString xssfRichTextString = new XSSFRichTextString(
                    td.getComment());
            Font commentFormatter = workbook.createFont();
            xssfRichTextString.applyFont(commentFormatter);
            cellComment.setString(xssfRichTextString);
            cell.setCellComment(cellComment);
        }
    }

    private void setImage(Td td, Sheet sheet) {
        if (td.getFile() == null) {
            return;
        }
        try {
            if (createHelper == null) {
                createHelper = workbook.getCreationHelper();
            }
            byte[] bytes = Files.readAllBytes(td.getFile().toPath());
            String fileName = td.getFile().getName();
            int imgFormat;
            switch (fileName.substring(fileName.lastIndexOf(".") + 1)) {
                case "jpg":
                case "jpeg":
                    imgFormat = Workbook.PICTURE_TYPE_JPEG;
                    break;
                case "png":
                    imgFormat = Workbook.PICTURE_TYPE_PNG;
                    break;
                case "dib":
                    imgFormat = Workbook.PICTURE_TYPE_DIB;
                    break;
                case "emf":
                    imgFormat = Workbook.PICTURE_TYPE_EMF;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid image type");
            }
            int pictureIdx = workbook.addPicture(bytes, imgFormat);
            Drawing drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = createHelper.createClientAnchor();
            anchor.setCol1(td.getCol());
            anchor.setRow1(td.getRow());
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            pict.resize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Cell setLink(Td td, Row currentRow, HyperlinkType hyperlinkType) {
        if (StringUtils.isBlank(td.getContent())) {
            return currentRow.createCell(td.getCol());
        }
        if (createHelper == null) {
            createHelper = workbook.getCreationHelper();
        }
        Cell cell = currentRow.createCell(td.getCol(), CellType.STRING);
        cell.setCellValue(td.getContent());
        Hyperlink link = createHelper.createHyperlink(hyperlinkType);
        link.setAddress(td.getLink());
        cell.setHyperlink(link);
        return cell;
    }

    private String setDropDownList(Td td, Sheet sheet, String content) {
        if (content.length() > 250) {
            throw new IllegalArgumentException("The total number of words in the drop-down list should not exceed 250.");
        }
        CellRangeAddressList addressList = new CellRangeAddressList(
                td.getRow(), td.getRowBound(), td.getCol(), td.getColBound());
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        String[] list = content.split(",");
        DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(list);
        DataValidation validation = dvHelper.createValidation(
                dvConstraint, addressList);
        if (validation instanceof XSSFDataValidation) {
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
        } else {
            validation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(validation);
        if (list.length > 0) {
            return list[0];
        }
        return null;
    }

    /**
     * 设置单元格样式
     *
     * @param cell 单元格
     * @param td   td单元格
     */
    private void setCellStyle(Row row, Cell cell, Td td) {
        if (useDefaultStyle) {
            if (td.isTh()) {
                cell.setCellStyle(defaultCellStyleMap.get(HtmlTableParser.TableTag.th));
            } else {
                if (ContentTypeEnum.isLink(td.getTdContentType())) {
                    cell.setCellStyle(defaultCellStyleMap.get(HtmlTableParser.TableTag.link));
                } else {
                    cell.setCellStyle(defaultCellStyleMap.get(HtmlTableParser.TableTag.td));
                }
            }
        } else {
            if (td.getStyle().isEmpty()) {
                return;
            }
            String fs = td.getStyle().get("font-size");
            if (fs != null) {
                short fontSize = (short) TdUtil.getValue(fs);
                if (fontSize > maxTdHeightMap.getOrDefault(row.getRowNum(), FontStyle.DEFAULT_FONT_SIZE)) {
                    maxTdHeightMap.put(row.getRowNum(), fontSize);
                }
            }
            if (cellStyleMap.containsKey(td.getStyle())) {
                cell.setCellStyle(cellStyleMap.get(td.getStyle()));
                return;
            }
            CellStyle cellStyle = workbook.createCellStyle();
            // background-color
            BackgroundStyle.setBackgroundColor(cellStyle, td.getStyle(), customColor);
            // text-align
            TextAlignStyle.setTextAlign(cellStyle, td.getStyle());
            // border
            BorderStyle.setBorder(cellStyle, td.getStyle());
            // font
            FontStyle.setFont(() -> workbook.createFont(), cellStyle, td.getStyle(), fontMap, customColor);
            // word-break
            WordBreakStyle.setWordBreak(cellStyle, td.getStyle());
            // 内容格式
            String formatStr = td.getStyle().get("format");
            if (formatStr != null) {
                if (format == null) {
                    format = workbook.createDataFormat();
                }
                cellStyle.setDataFormat(format.getFormat(formatStr));
            }
            cell.setCellStyle(cellStyle);
            cellStyleMap.put(td.getStyle(), cellStyle);
        }
    }

    public static void errorComment(Workbook wk, int sheetIndex, int rowIndex, int colIndex,
                                    String message) {
        errorComment(wk, wk.getSheetAt(sheetIndex), rowIndex, colIndex, message);
    }
    public static void errorComment(Workbook wk, int sheetIndex,String sheetName, int rowIndex, int colIndex,
                                    String message) {
        if (-1==sheetIndex){
            if (StringUtils.isNotEmpty(sheetName)){
                errorComment(wk, wk.getSheet(sheetName), rowIndex, colIndex, message);
            }
        }else {
            errorComment(wk, wk.getSheetAt(sheetIndex), rowIndex, colIndex, message);
        }
    }
    public static void errorComment(Workbook workbook, Sheet sheet, int rowIndex, int colIndex,
                                    String message) {
        Comment cellComment = sheet.getCellComment(new CellAddress(rowIndex, colIndex));
        if (cellComment != null) {
            RichTextString text = cellComment.getString();
            if (text instanceof HSSFRichTextString) {
                cellComment.setString(new HSSFRichTextString(text.getString() + "\n" + message));
            } else {
                cellComment.setString(new XSSFRichTextString(text.getString() + "\n" + message));
            }
            return;
        }
        CreationHelper factory = workbook.getCreationHelper();
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setRow1(rowIndex);
        anchor.setRow2(rowIndex + 3);
        anchor.setCol1(colIndex);
        anchor.setCol2(colIndex + 3);

        Drawing<?> drawing = sheet.createDrawingPatriarch();

        RichTextString context = null;
        if (drawing instanceof HSSFPatriarch) {
            context = new HSSFRichTextString(message);
        } else {
            context = new XSSFRichTextString(message);
        }
        Comment comment = drawing.createCellComment(anchor);
        comment.setString(context);
        comment.setAuthor("system");
    }

    /**
     * 空工作簿
     *
     * @return Workbook
     */
    protected Workbook emptyWorkbook() {
        if (workbook == null) {
            workbook = new XSSFWorkbook();
        }
        workbook.createSheet();
        return workbook;
    }

    /**
     * 初始化默认单元格样式
     *
     * @param workbook workbook
     */
    protected void initCellStyle(Workbook workbook) {
        if (useDefaultStyle) {
            defaultCellStyleMap = new EnumMap<>(HtmlTableParser.TableTag.class);
            defaultCellStyleMap.put(HtmlTableParser.TableTag.th, new ThDefaultCellStyle().supply(workbook));
            defaultCellStyleMap.put(HtmlTableParser.TableTag.td, new TdDefaultCellStyle().supply(workbook));
            defaultCellStyleMap.put(HtmlTableParser.TableTag.link, new LinkDefaultCellStyle().supply(workbook));
        } else {
            if (workbook instanceof HSSFWorkbook) {
                HSSFPalette palette = ((HSSFWorkbook) workbook).getCustomPalette();
                customColor = new CustomColor(true, palette);
            } else {
                customColor = new CustomColor();
            }
        }
    }

    /**
     * 窗口冻结
     *
     * @param tableIndex table index
     * @param sheet      sheet
     */
    protected void freezePane(int tableIndex, Sheet sheet) {
        if (freezePanes != null && freezePanes.length > tableIndex) {
            FreezePane freezePane = freezePanes[tableIndex];
            if (freezePane == null) {
                throw new IllegalStateException("FreezePane is null");
            }
            sheet.createFreezePane(freezePane.getColSplit(), freezePane.getRowSplit());
        }
    }

    /**
     * 获取每列最大宽度
     *
     * @param trList trList
     * @return colMaxWidthMap
     */
    protected Map<Integer, Integer> getColMaxWidthMap(List<Tr> trList) {
        if (WidthStrategy.isNoAuto(widthStrategy) || WidthStrategy.isAutoWidth(widthStrategy)) {
            return Collections.emptyMap();
        }
        if (useDefaultStyle) {
            // 使用默认样式，需要重新修正加粗的标题自适应宽度
            trList.parallelStream().forEach(tr -> {
                tr.getTdList().stream().filter(Td::isTh).forEach(th -> {
                    int tdWidth = TdUtil.getStringWidth(th.getContent(), 0.25);
                    tr.getColWidthMap().put(th.getCol(), tdWidth);
                });
            });
        }
        int mapMaxSize = trList.stream().mapToInt(tr -> tr.getColWidthMap().size()).max().orElse(16);
        Map<Integer, Integer> colMaxWidthMap = Maps.newHashMapWithExpectedSize(mapMaxSize);
        trList.forEach(tr -> {
            tr.getColWidthMap().forEach((k, v) -> {
                Integer width = colMaxWidthMap.get(k);
                if (Objects.isNull(width) || v > width) {
                    colMaxWidthMap.put(k, v);
                }
            });
            tr.setColWidthMap(null);
        });
        return colMaxWidthMap;
    }

    /**
     * 设置每列宽度
     *
     * @param colMaxWidthMap 列最大宽度Map
     * @param sheet          sheet
     * @param maxColIndex    最大列索引
     */
    protected void setColWidth(Map<Integer, Integer> colMaxWidthMap, Sheet sheet, int maxColIndex) {
        if (WidthStrategy.isNoAuto(widthStrategy)) {
            return;
        }
        if (WidthStrategy.isAutoWidth(widthStrategy)) {
            if (sheet instanceof SXSSFSheet) {
                throw new UnsupportedOperationException("SXSSF does not support automatic width at this time");
            }
            for (int i = 0; i <= maxColIndex; i++) {
                sheet.autoSizeColumn(i);
            }
            return;
        }
        colMaxWidthMap.forEach((key, value) -> {
            int contentLength = value << 1;
            if (contentLength > 255) {
                contentLength = 255;
            }
            sheet.setColumnWidth(key, contentLength << 8);
        });
    }

    /**
     * 清除样式缓存
     */
    protected void clearCache() {
        cellStyleMap = Maps.newHashMap();
        fontMap = Maps.newHashMap();
        maxTdHeightMap = Maps.newHashMap();
    }

    /**
     * 关闭工作簿
     */
    protected void closeWorkbook() {
        if (workbook == null) {
            return;
        }
        try {
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
            workbook.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
