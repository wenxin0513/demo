package com.example.demo.avistor.util;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class IfFunction extends AbstractFunction {

    public static AviatorObject getMatchDataType(Map<String, Object> env,AviatorObject value){
        AviatorType val1AviatorType = value.getAviatorType();
        switch (val1AviatorType) {
            case Long:
            case Double:
            case BigInt:
            case Decimal:
                Number numberValue = value.numberValue(env);
                return AviatorDecimal.valueOf(numberValue);
            case String:
                String stringValue = value.stringValue(env);
                return new AviatorString(stringValue);
            case Boolean:
                Boolean boolValue = value.booleanValue(env);
                return AviatorBoolean.valueOf(boolValue);
            case JavaType:
                Object javaValue =  value.getValue(env);
                if(javaValue instanceof BigDecimal){
                    return AviatorDecimal.valueOf(javaValue);
                }else{
                    return value;
                }
        }
        return new AviatorString("UNKNOW_DATA_TYPE");
    }

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
    public AviatorObject call(Map<String, Object> env, AviatorObject logical_test, AviatorObject value_if_true, AviatorObject value_if_false) {
        Boolean flag = logical_test.booleanValue(env);
        if (flag) {
            System.out.println("测试"+getMatchDataType(env,value_if_true));
            return getMatchDataType(env,value_if_true);
        } else {
            System.out.println("测试"+getMatchDataType(env,value_if_false));
            return getMatchDataType(env,value_if_false);
        }
    }
    @Override
    public String getName() {
        return "IF";
    }

    public static void main(String[] args) {
        AviatorEvaluator.addFunction(new IfFunction());
        String expression = "IF(a==b,c,d)";
        Map<String, Object> params = new HashMap<>();
        params.put("a",2);
        params.put("b",new BigDecimal("2.00"));
        params.put("c",6);
        params.put("d",9);
        Expression compiledExp = AviatorEvaluator.compile(expression);
        Object execute = compiledExp.execute(params);
    }
}
