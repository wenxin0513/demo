package com.example.springbatch.base.job.batch;

import com.example.springbatch.constant.JobConstant;
import com.example.springbatch.goods.service.GoodsService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableBatchProcessing
@Import(DataSourceConfiguration.class)
public class GoodsJob extends BaseJob {

    @Autowired
    GoodsService goodsService;


    public GoodsJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, GoodsService goodsService) {
        super(jobBuilderFactory, stepBuilderFactory);
        this.goodsService = goodsService;
    }



    public Job buildJob() {
        return jobBuilderFactory.get(JobConstant.GOODS_JOB)
                .start(Step1())
                .build();
    }

    private Step Step1() {
        return stepBuilderFactory.get("autoRedemptionStep")
                .tasklet((contribution, chunkContext) -> {
                    //执行
                    goodsService.GoodsList();
                    //结束
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
