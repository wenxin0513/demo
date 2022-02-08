package com.example.myexcel.core.specification;


import com.example.myexcel.core.lambda.SFunction;

import javax.persistence.criteria.*;

/**
 * @author zhouhong
 * @version 1.0
 * @title: NotInSpecification
 * @date 2019/7/3 11:45
 */
public class NotInSpecification<T> extends AbstractSpecification<T> {
    private String property;
    private Object[] values;

    public NotInSpecification(String property, Object[] values) {
        this.property = property;
        this.values = values;
    }

    public NotInSpecification(SFunction<T, ?> property, Object[] values) {
        this.property = toProperty(property);
        this.values = values;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From from = (From) getRoot(property, root);
        String field = getProperty(property);
        return from.get(field).in(values).not();
    }
}
