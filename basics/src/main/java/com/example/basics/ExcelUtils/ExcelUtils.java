package com.example.basics.ExcelUtils;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelUtils {


    public static final String OFFICE_EXCEL_XLS = "xls";
    public static final String OFFICE_EXCEL_XLSX = "xlsx";

    public static void main(String[] args) throws IOException, InvalidFormatException {
        readExcelSheet();
    }

    private static String readExcelSheet() throws IOException, InvalidFormatException {
        StringBuilder sb = new StringBuilder();
        Workbook workbook = getWorkbook("E:\\rtret.xls");
        Sheet sheet = workbook.getSheet("数据字典");
        if( sheet != null){
            int rowNos = sheet.getLastRowNum();// 得到excel的总记录条数
            for (int i = 1; i <= rowNos; i++) {// 遍历行
                Row row = sheet.getRow(i);
                if(row != null){
                    int columNos = row.getLastCellNum();// 表头总共的列数
                    Cell cell = row.getCell(0);
                    cell.setCellType(CellType.STRING);
                    Cell cell4 = row.getCell(3);
                    cell4.setCellType(CellType.STRING);


                    Cell cell2 = row.getCell(1);
                    cell2.setCellType(CellType.STRING);


                    Cell cell3 = row.getCell(2);
                    cell3.setCellType(CellType.STRING);
//                    System.out.println("\n" +
//                            "INSERT INTO \"t_dict_type\"(\"create_by\", \"create_time\", \"status\", \"update_by\", \"update_time\", \"dict_type\", \"type_name\")\n" +
//                            "VALUES ('system', now(), 1, 'system', now(), '"+cell4.getStringCellValue()+"', '"+cell.getStringCellValue()+"');");
                    System.out.println("INSERT INTO \"public\".\"t_dict_item\"( \"dict_type\", \"dict_code\", \"name_cn\", \"name_en\", \"name_tw\", \"index_no\", \"attr1\", \"attr2\", \"attr3\", \"attr4\", \"status\", \"create_time\", \"create_by\", \"update_time\", \"update_by\")\n" +
                            "VALUES ( '"+cell4.getStringCellValue()+"', '"+cell2.getStringCellValue()+"', '"+cell3.getStringCellValue()+"', '"+ cell3.getStringCellValue()+"', '"+ cell3.getStringCellValue()+"', 1, NULL, NULL, NULL, NULL, 1, now(), 'system', now(), 'system');");
                }
            }
        }

        return sb.toString();
    }
    /**
     * 根据文件路径获取Workbook对象
     * @param filepath 文件全路径
     */
    public static Workbook getWorkbook(String filepath)
            throws EncryptedDocumentException, InvalidFormatException, IOException {
        InputStream is = null;
        Workbook wb = null;
        if (StringUtils.isEmpty(filepath)) {
            throw new IllegalArgumentException("文件路径不能为空");
        } else {
            String suffiex = getSuffiex(filepath);
            if (StringUtils.isEmpty(suffiex)) {
                throw new IllegalArgumentException("文件后缀不能为空");
            }
            if (OFFICE_EXCEL_XLS.equals(suffiex) || OFFICE_EXCEL_XLSX.equals(suffiex)) {
                try {
                    is = new FileInputStream(filepath);
                    wb = WorkbookFactory.create(is);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (wb != null) {
                        wb.close();
                    }
                }
            } else {
                throw new IllegalArgumentException("该文件非Excel文件");
            }
        }
        return wb;
    }

    /**
     * 获取后缀
     * @param filepath filepath 文件全路径
     */
    private static String getSuffiex(String filepath) {
        if (StringUtils.isEmpty(filepath)) {
            return "";
        }
        int index = filepath.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return filepath.substring(index + 1, filepath.length());
    }
}
