package com.example.demo.drools.service.impl;

import com.example.demo.drools.entity.QueryParam;
import com.example.demo.drools.service.RuleEngineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RuleEngineServiceImpl implements RuleEngineService {
    @Override
    public void executeAddRule(QueryParam param) {
        log.info("====================添加数据==================");
    }

    @Override
    public void executeRemoveRule(QueryParam param) {
        log.info("====================移除数据==================");
    }
}
