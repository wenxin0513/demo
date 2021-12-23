package com.example.demo.drools.entity;

import lombok.Data;

import java.util.Date;
@Data
public class ParamInfo {
    private String id ;
    private String paramSign ;

    private QueryParam queryParam = new QueryParam();
}
