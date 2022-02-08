package com.example.myexcel.core.specification;


import com.example.myexcel.core.lambda.SFunction;

import javax.persistence.criteria.*;

/**
 * @author zhouhong
 * @version 1.0
 * @title: GtSpecification
 * @date 2019/7/3 11:43
 */
public class GtSpecification<T> extends AbstractSpecification<T> {
    private final String property;
    private final Comparable<Object> compare;

    public GtSpecification(String property, Comparable<? extends Object> compare) {
        this.property = property;
        this.compare = (Comparable<Object>) compare;
    }

    public GtSpecification(SFunction<T, ?> property, Comparable<? extends Object> compare) {
        this.property = toProperty(property);
        this.compare = (Comparable<Object>) compare;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From from = (From) getRoot(property, root);
        String field = getProperty(property);
        return cb.greaterThan(from.get(field), compare);
    }
}
