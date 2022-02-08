package com.example.myexcel.core.specification;
import com.example.myexcel.core.lambda.SFunction;
import com.example.myexcel.core.lambda.SerializedLambda;
import groovy.time.BaseDuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;


import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.io.Serializable;

/**
 * @author zhouhong
 * @version 1.0
 * @title: AbstractSpecification
 * @date 2019/7/3 11:40
 */
public abstract class AbstractSpecification<T> implements Specification<T>, Serializable {

    public String toProperty(SFunction<T,?> property) {
        return SerializedLambda.resolve(property).methodToProperty();
    }
    public BaseDuration.From getRoot(String property, Root<T> root) {
        if (property.contains(".")) {
            String joinProperty = StringUtils.split(property, ".")[0];
            return (BaseDuration.From) root.join(joinProperty, JoinType.LEFT);
        }
        return (BaseDuration.From) root;
    }

    public String getProperty(String property) {
        if (property.contains(".")) {
            return StringUtils.split(property, ".")[1];
        }
        return property;
    }

}
