package com.example.myexcel.core.lambda;


import java.io.Serializable;
import java.util.function.Function;

/**
 * @author zhouhong
 * @version 1.0
 * @title: SFunction
 * @description: 支持序列化的 Function
 * @date 2019/9/19 18:27
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
