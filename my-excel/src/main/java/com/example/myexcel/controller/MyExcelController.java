package com.example.myexcel.controller;

import com.example.myexcel.core.builder.ExcelBuilder;
import com.example.myexcel.core.builder.FreemarkerExcelBuilder;
import com.example.myexcel.entity.Student;
import com.example.myexcel.entity.Teacher;
import com.example.myexcel.utils.AttachmentExportUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "excel")
public class MyExcelController {

    @GetMapping(value = "test")
    public void createExcel(HttpServletResponse response) {
        Map<String, Object> dataMap = new HashMap<>();
        List<Student> studentList = new ArrayList();
        Student str = new Student();
        str.setId("1");
        str.setName("张三");
        str.setSex("男");
        Student str1 = new Student();
        str1.setId("2");
        str1.setName("小明");
        str1.setSex("男");
        Student str2 = new Student();
        str2.setId("3");
        str2.setName("小红");
        str2.setSex("女");
        studentList.add(str);
        studentList.add(str1);
        studentList.add(str2);
        dataMap.put("student",studentList);
        Teacher teacher = new Teacher();
        teacher.setId("1");
        teacher.setName("张三老师");
        teacher.setSex("男");
        Teacher teacher1 = new Teacher();
        teacher1.setId("2");
        teacher1.setName("李四老师");
        teacher1.setSex("男");
        Teacher teacher2 = new Teacher();
        teacher2.setId("3");
        teacher2.setName("王五老师");
        teacher2.setSex("男");
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(teacher);
        teachers.add(teacher1);
        teachers.add(teacher2);
        dataMap.put("teacher",teachers);
        ExcelBuilder excelBuilder = new FreemarkerExcelBuilder();
        Workbook workbook = excelBuilder.template("/templates/sheet001.ftl").build(dataMap);
        AttachmentExportUtil.export(workbook, "老师、学生信息表", response);
    }
}
