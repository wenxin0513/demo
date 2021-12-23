package com.example.springbatch.base.job.batch;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhouhong
 * @version 1.0
 * @title: BaseJob
 * @description: TODO
 * @date 2019/9/5 11:06
 */
public class BaseJob {

    @Autowired
    protected final JobBuilderFactory jobBuilderFactory;

    @Autowired
    protected final StepBuilderFactory stepBuilderFactory;

    public BaseJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }
}
