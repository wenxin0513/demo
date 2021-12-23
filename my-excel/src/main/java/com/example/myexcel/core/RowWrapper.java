package com.example.myexcel.core;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhouhong
 * @version 1.0
 * @title: RowWrapper
 * @date 2019/12/25 17:22
 */
@Data
@Builder
public class RowWrapper<T> {

    private Integer sheetIndex;

    private Integer rowIndex;

    private T data;
}
