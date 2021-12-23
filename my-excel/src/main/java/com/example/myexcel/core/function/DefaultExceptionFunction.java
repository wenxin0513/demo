package com.example.myexcel.core.function;

import com.cntaiping.tplhk.reins.common.excel.core.ReadContext;

/**
 * @author zhouhong
 * @version 1.0
 * @title: DefaultExceptionFunction
 * @date 2020/1/7 10:33
 */
public class DefaultExceptionFunction extends ExceptionFunction {

    @Override
    public ExceptionPolicy callApply(Throwable cause, ReadContext context) {
        return ExceptionPolicy.ERROR;
    }

}
