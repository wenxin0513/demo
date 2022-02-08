package com.example.myexcel.core.parser;

import com.example.myexcel.core.ReadContext;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author zhouhong
 * @version 1.0
 * @title: ExcelHandler
 * @date 2020/1/3 17:24
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExcelHandler {

    boolean stop;

    List<ReadContext> cells;


}
