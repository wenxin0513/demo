package com.example.demo.drools.controller;

import com.example.demo.drools.entity.QueryParam;
import com.example.demo.drools.entity.RuleResult;
import com.example.demo.drools.service.RuleEngineService;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/rule")
@Slf4j
public class RuleController {
    @Resource
    private KieSession kieSession;
    @Resource
    private RuleEngineService ruleEngineService ;

    @GetMapping ("/param")
    public void param (){
        QueryParam queryParam1 = new QueryParam() ;
        queryParam1.setParamId("1");
        queryParam1.setParamSign("+");
        queryParam1.setMax(1);
        QueryParam queryParam2 = new QueryParam() ;
        queryParam2.setParamId("2");
        queryParam2.setParamSign("-");
        // 入参
        kieSession.insert(queryParam1) ;
        kieSession.insert(queryParam2) ;
        kieSession.insert(this.ruleEngineService) ;
        // 返参
        RuleResult resultParam = new RuleResult() ;
        kieSession.insert(resultParam) ;
        Integer min = kieSession.fireAllRules() ;
        log.info("====参数返回===={}=====",min);

    }
}
