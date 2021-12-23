package com.example.demo.avistor.util;

import cn.hutool.core.util.NumberUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorDecimal;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorType;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class RoundFunction extends AbstractFunction {

    public static Number getNumberValue(Map<String, Object> env, AviatorObject value){
        AviatorType val1AviatorType = value.getAviatorType();
        if(val1AviatorType.equals(AviatorType.String)){
            String valStr = value.stringValue(env);
            if(StringUtils.isEmpty(valStr)){
                return 0D;
            }else if(valStr.matches("-?\\d+\\.?\\d*")){
                return Double.parseDouble(valStr);
            }else{
                return 0D;
            }
        }else{
            return value.numberValue(env);
        }
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject number, AviatorObject num_digits) {
        Number numberVal = getNumberValue(env,number);
        Number num_digitsVal = getNumberValue(env,num_digits);
        Integer digits = num_digitsVal.intValue();
//            System.err.println("numberVal::::"+numberVal);
        BigDecimal decimal = NumberUtil.toBigDecimal(numberVal).setScale(digits, RoundingMode.HALF_UP);
        System.out.println("测试"+decimal);
        return AviatorDecimal.valueOf(decimal);
    }

    @Override
    public String getName() {
        return "ROUND";
    }

    public static void main(String[] args) {
        AviatorEvaluator.addFunction(new RoundFunction());
        String expression = "a*b*c";
        Map<String, Object> params = new HashMap<>();
        params.put("a",new BigDecimal("4.65"));
        params.put("b",new BigDecimal("4.65"));
        params.put("c",new BigDecimal("4.65"));
        Expression compiledExp = AviatorEvaluator.compile(expression);
        compiledExp.execute(params);
    }
}
