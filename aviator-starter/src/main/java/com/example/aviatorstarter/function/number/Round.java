package com.example.aviatorstarter.function.number;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorDecimal;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * @description: εθδΊε₯
 * @Date : 2021/8/31
 * @Author : xujunqiang
 */
public class Round extends AbstractFunction {
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg) {
        Object value = arg.getValue(env);
        return AviatorDecimal.valueOf(Math.round(new Double(value.toString())));
    }

    @Override
    public String getName() {
        return "round";
    }
}
