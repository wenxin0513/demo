package com.example.myexcel.core.specification;


import com.example.myexcel.core.lambda.SFunction;

import javax.persistence.criteria.*;

/**
 * @author zhouhong
 * @version 1.0
 * @title: EqualSpecification
 * @date 2019/7/3 11:41
 */
public class EqualSpecification<T> extends AbstractSpecification<T> {
    private final String property;
    private final Object[] values;

    public EqualSpecification(String property, Object... values) {
        this.property = property;
        this.values = values;
    }

    public EqualSpecification(SFunction<T, ?> property, Object... values) {
        this.property = toProperty(property);
        this.values = values;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From from = (From) getRoot(property, root);
        String field = getProperty(property);
        if (values == null) {
            return cb.isNull(from.get(field));
        }
        if (values.length == 1) {
            return getPredicate(from, cb, values[0], field);
        }

        Predicate[] predicates = new Predicate[values.length];
        for (int i = 0; i < values.length; i++) {
            predicates[i] = getPredicate(root, cb, values[i], field);
        }
        return cb.or(predicates);
    }

    private Predicate getPredicate(From root, CriteriaBuilder cb, Object value, String field) {
        return value == null ? cb.isNull(root.get(field)) : cb.equal(root.get(field), value);
    }
}
