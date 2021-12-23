package com.example.demo.drools.entity;

import lombok.Data;

@Data
public class QueryParam {
    private String paramId ;
    private String paramSign ;
    private Integer max;
    private ParamInfo paramInfo;
}
