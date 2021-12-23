package com.example.demo.drools.service;

import com.example.demo.drools.entity.QueryParam;

public interface RuleEngineService {

     void executeAddRule(QueryParam param) ;
     void executeRemoveRule(QueryParam param) ;
}
