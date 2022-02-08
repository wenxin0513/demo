package com.example.myexcel.core.specification;

import com.example.myexcel.core.lambda.SFunction;

import javax.persistence.criteria.*;

/**
 * @author zhouhong
 * @version 1.0
 * @title: NotLikeSpecification
 * @date 2019/7/3 11:45
 */
public class NotLikeSpecification<T> extends AbstractSpecification<T> {
    private final String property;
    private final String[] patterns;

    public NotLikeSpecification(String property, String... patterns) {
        this.property = property;
        this.patterns = patterns;
    }

    public NotLikeSpecification(SFunction<T, ?> property, String... patterns) {
        this.property = toProperty(property);
        this.patterns = patterns;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From from = (From) getRoot(property, root);
        String field = getProperty(property);
        if (patterns.length == 1) {
            return cb.like(from.get(field), patterns[0]).not();
        }
        Predicate[] predicates = new Predicate[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            predicates[i] = cb.like(from.get(field), patterns[i]).not();
        }
        return cb.or(predicates);
    }
}
