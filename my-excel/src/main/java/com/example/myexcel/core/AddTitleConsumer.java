package com.example.myexcel.core;

/**
 * @author zhouhong
 * @version 1.0
 */
@FunctionalInterface
public interface AddTitleConsumer<T, E, F> {

    void accept(T t, E e, F f);
}
