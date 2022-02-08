package com.example.myexcel.core.specification;

import com.example.myexcel.core.lambda.SFunction;

import javax.persistence.criteria.*;

/**
 * @author zhouhong
 * @version 1.0
 * @title: BetweenSpecification
 * @date 2019/7/3 11:40
 */
public class BetweenSpecification<T> extends AbstractSpecification<T> {
    private final String property;
    private final Comparable<Object> lower;
    private final Comparable<Object> upper;

    public BetweenSpecification(String property, Object lower, Object upper) {
        this.property = property;
        this.lower = (Comparable<Object>) lower;
        this.upper = (Comparable<Object>) upper;
    }

    public BetweenSpecification(SFunction<T, ?> property, Object lower, Object upper) {
        this.property = toProperty(property);
        this.lower = (Comparable<Object>) lower;
        this.upper = (Comparable<Object>) upper;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From from = (From) getRoot(property, root);
        String field = getProperty(property);
        return cb.between(from.get(field), lower, upper);
    }
}
