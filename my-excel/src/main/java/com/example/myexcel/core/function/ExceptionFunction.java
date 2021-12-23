package com.example.myexcel.core.function;

import com.cntaiping.tplhk.reins.common.excel.core.ReadContext;
import com.cntaiping.tplhk.reins.common.excel.exception.ExcelReadException;

import java.util.function.BiFunction;

/**
 * @author zhouhong
 * @version 1.0
 * @title: ExceptionFunction
 * @date 2020/1/7 9:56
 */
public abstract class ExceptionFunction implements BiFunction<Throwable, ReadContext, ExceptionFunction.ExceptionPolicy> {

    public abstract ExceptionPolicy callApply(Throwable cause, ReadContext context);

    /**
     * @param cause   可以为 null
     * @param context
     * @return
     */
    @Override
    public ExceptionPolicy apply(Throwable cause, ReadContext context) {
        ExceptionPolicy exceptionPolicy = callApply(cause, context);
        switch (exceptionPolicy) {
            case ERROR:
                if (null==cause){
                    throw new ExcelReadException("第"+context.getRowNum()+"行,"+"第"+(context.getColNum()+1)+"列，"+context.getErrorMsg());
                }else {
                    throw new ExcelReadException("第"+context.getRowNum()+"行,"+"第"+(context.getColNum()+1)+"列，"+context.getErrorMsg());
                }
            case STOP:
                break;
            case OK:
                break;
        }
        return exceptionPolicy;
    }

    public enum ExceptionPolicy {
        ERROR,//直接返回异常
        STOP,//不返回异常，但中断后续读取
        OK//读取正确的数据，忽略异常的数据
    }

}
