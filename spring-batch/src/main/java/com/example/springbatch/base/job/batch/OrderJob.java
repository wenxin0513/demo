package com.example.springbatch.base.job.batch;

import com.example.springbatch.constant.JobConstant;
import com.example.springbatch.order.service.OrderService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderJob extends BaseJob {

    @Autowired
    OrderService orderService;


    public OrderJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,OrderService orderService) {
        super(jobBuilderFactory, stepBuilderFactory);
        this.orderService = orderService;
    }


    /**
     * 构建job
     * @return
     */
    public Job buildJob() {
        return jobBuilderFactory.get(JobConstant.ORDER_JOB)
                .start(Step1()).next(Step2())
                .build();
    }

    /**
     * 构建Step
     * @return
     */
    private Step Step1() {
        return stepBuilderFactory.get("autoRedemptionStep")
                .tasklet((contribution, chunkContext) -> {
                    //执行
                    orderService.orderInsert();
                    //结束
                    return RepeatStatus.FINISHED;
                }).build();
    }

    private Step Step2() {
        return stepBuilderFactory.get("autoRedemptionStep")
                .tasklet((contribution, chunkContext) -> {
                    //执行
                    orderService.orderList();
                    //结束
                    return RepeatStatus.FINISHED;
                }).build();
    }


}
