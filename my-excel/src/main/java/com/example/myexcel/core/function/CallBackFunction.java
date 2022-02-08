package com.example.myexcel.core.function;

import com.example.myexcel.core.ReadContext;
import com.example.myexcel.core.RowWrapper;
import com.example.myexcel.core.annotation.ExcelColumn;
import com.example.myexcel.core.lambda.SFunction;
import com.example.myexcel.core.parser.ExcelHandler;
import com.example.myexcel.core.specification.AbstractSpecification;
import com.example.myexcel.utils.ApplicationUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.util.ObjectUtils;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author zhouhong
 * @version 1.0
 * @title: CallBackFunction
 * @date 2020/1/6 10:03
 */
public abstract class CallBackFunction<T> extends AbstractSpecification<T> implements Function<RowWrapper<T>, ExcelHandler> {

    private Map<String, List<Integer>> cellMap = Maps.newHashMap();
    private Map<Integer, Field> fieldMap;
    protected BiFunction<Throwable, ReadContext, ExceptionFunction.ExceptionPolicy> exceptionFunction;

    private List<T> container = Lists.newArrayList();

    /**
     * 将cell index对应的字段转换成字段名称对应的cell index
     * 方便根据字段名称取cell index
     *
     * @param fieldMap
     */
    public void reverse(Map<Integer, Field> fieldMap, BiFunction<Throwable, ReadContext, ExceptionFunction.ExceptionPolicy> exceptionFunction) {
        if (cellMap.size() == 0) {
            this.fieldMap = fieldMap;
            this.exceptionFunction = exceptionFunction;
            fieldMap.forEach((k, v) -> {
                String fname = v.getName();
                List<Integer> cells = cellMap.get(fname);
                if (ObjectUtils.isEmpty(cells)) {
                    cells = Lists.newArrayList();
                }
                cells.add(k);
                cellMap.put(fname, cells);
            });
        }
    }

    /**
     * 获取字段对应的cell index
     *
     * @param function
     * @return
     */
    public List<Integer> cellIndex(SFunction<T, ?> function) {
        String property = this.toProperty(function);
        return cellMap.get(property);
    }


    protected abstract List<ReadContext> callApply(RowWrapper<T> rowWrapper);

    @Override
    public ExcelHandler apply(RowWrapper<T> rowWrapper) {
        ExcelHandler handler = ExcelHandler.builder().build();

        final List<ReadContext> errors = Lists.newArrayList();
        fieldMap.forEach((colIndex, field) -> {
            Object context;
            try {
                context = field.get(rowWrapper.getData());
            } catch (IllegalAccessException e) {
                return;
            }
            ReadContext build = ReadContext.builder()
                    .object(rowWrapper.getData())
                    .rowNum(rowWrapper.getRowIndex())
                    .field(field)
                    .val(context)
                    .colNum(colIndex)
                    .build();
            checkExcelColumn(errors, build, handler);
        });
        if (!handler.isStop()) {
            errors.addAll(callApply(rowWrapper));
        }
        if (!ObjectUtils.isEmpty(errors)) {
            handler.setCells(errors);
            errors.forEach(context -> {
                ExceptionFunction.ExceptionPolicy policy = this.exceptionFunction.apply(null, context);
                if (ExceptionFunction.ExceptionPolicy.STOP.equals(policy)) {
                    handler.setStop(true);
                    return;
                }
            });
        }
        if (errors.size() == 0) {
            container.add(rowWrapper.getData());
        }
        return handler;
    }

    protected ReadContext context(RowWrapper<T> rowWrapper, String val, int colNum, String message) {
        return ReadContext.builder()
                .object(rowWrapper.getData())
                .field(fieldMap.get(colNum))
                .val(val)
                .rowNum(rowWrapper.getRowIndex())
                .colNum(colNum)
                .errorMsg(message)
                .build();
    }


    private void checkExcelColumn(List<ReadContext> readContexts, ReadContext context, ExcelHandler handler) {
        ExcelColumn excelColumn = context.getField().getAnnotation(ExcelColumn.class);
        Object value = context.getVal();
        // required
        Boolean required = excelColumn.required();
        String title = ApplicationUtil.getMSA().getMessage(excelColumn.title(), excelColumn.title());
        if (null != required && required) {
            if (ObjectUtils.isEmpty(value)) {
                context.setErrorMsg(title + "必须填写");
                readContexts.add(context);
                ExceptionFunction.ExceptionPolicy policy = exceptionFunction.apply(null, context);
                if (ExceptionFunction.ExceptionPolicy.STOP.equals(policy)) {
                    handler.setStop(true);
                }
                return;
            }
        }
        // maxLength
        Integer maxLength = excelColumn.maxLength();
        if (-1 != maxLength) {
            if (!ObjectUtils.isEmpty(value) && value.toString().length() > maxLength) {
                context.setErrorMsg(title + "超过最大长度: " + maxLength);
                readContexts.add(context);
                ExceptionFunction.ExceptionPolicy policy = exceptionFunction.apply(null, context);
                if (ExceptionFunction.ExceptionPolicy.STOP.equals(policy)) {
                    handler.setStop(true);
                }
                return;
            }
        }
        return;
    }

    public List<T> getData() {
        return container;
    }
}
