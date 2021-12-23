package com.example.aviatorstarter.function.general;


import com.example.aviatorstarter.util.NumberToCNUtil;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @description: 数字转大写金额
 * @Date : 2021/8/31
 * @Author : xujunqiang
 */
public class ChineseNumberUpper extends AbstractFunction {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg) {
        Object value = arg.getValue(env);
        BigDecimal numValue = value == null ? null : new BigDecimal(value.toString());
        return new AviatorString(NumberToCNUtil.number2CNMontrayUnit(numValue));
    }

    @Override
    public String getName() {
        return "chinese.number.upper";
    }

}
