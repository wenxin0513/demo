package com.example.myexcel.core.specification;


import com.example.myexcel.core.lambda.SFunction;

import javax.persistence.criteria.*;

/**
 * @author zhouhong
 * @version 1.0
 * @title: LikeSpecification
 * @date 2019/7/3 11:44
 */
public class LikeSpecification<T> extends AbstractSpecification<T> {

    private final String property;
    private final String[] patterns;
    private final Flag flag;
    private final Boolean ignoreCase;

    public LikeSpecification(String property, Flag flag, String... patterns) {
        this.property = property;
        this.flag = flag;
        this.ignoreCase = false;
        this.patterns = patterns;
    }

    public LikeSpecification(String property, Flag flag, boolean ignoreCase, String... patterns) {
        this.property = property;
        this.flag = flag;
        this.ignoreCase = ignoreCase;
        this.patterns = patterns;
    }

    public LikeSpecification(SFunction<T, ?> property, Flag flag, String... patterns) {
        this.property = toProperty(property);
        this.flag = flag;
        this.ignoreCase = false;
        this.patterns = patterns;
    }

    public LikeSpecification(SFunction<T, ?> property, Flag flag, boolean ignoreCase, String... patterns) {
        this.property = toProperty(property);
        this.flag = flag;
        this.ignoreCase = ignoreCase;
        this.patterns = patterns;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From from = (From) getRoot(property, root);
        String field = getProperty(property);
        Expression x = ignoreCase? cb.upper(from.get(field)) :from.get(field);

        if (patterns.length == 1) {
            return cb.like(x, match(patterns[0]));
        }
        Predicate[] predicates = new Predicate[patterns.length];
        for (int i = 0; i < patterns.length; i++) {

            predicates[i] = cb.like(ignoreCase ? cb.upper(x) : x, match(patterns[i]));
        }
        return cb.or(predicates);
    }

    private String match(String pattern) {
        String s ="null";
        if (pattern == null) {
            return s;
        }
        s = ignoreCase ? pattern.toUpperCase() : pattern;
        switch (flag) {
            case LEFT:
                return "%" + s;
            case RIGHT:
                return s + "%";
            default:
                return "%" + s + "%";
        }
    }

    /**
     * like 匹配方式
     */
    public enum Flag {
        LEFT, RIGHT, AROUND
    }
}
