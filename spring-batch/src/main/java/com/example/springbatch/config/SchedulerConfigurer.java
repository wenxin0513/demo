package com.example.springbatch.config;

import com.example.springbatch.base.job.batch.GoodsJob;
import com.example.springbatch.base.job.batch.OrderJob;
import com.example.springbatch.goods.service.GoodsService;
import com.example.springbatch.order.service.OrderService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.activation.DataSource;

/**
 * @author wenxin
 * @version 1.0
 * @title: QuartzConfigurer
 * @description: TODO
 * @date 2019/6/28 9:46
 */
@Configuration
public class SchedulerConfigurer {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public SimpleJobOperator jobOperator(JobExplorer jobExplorer,
                                         JobRepository jobRepository,
                                         JobRegistry jobRegistry,
                                         JobLauncher jobLauncher) {

        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobLauncher(jobLauncher);
        return jobOperator;
    }

    /**
     * jobOperator需要此处的JobRegistryBeanPostProcessor
     *
     * @param jobRegistry
     * @return
     */
    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }


    /**
     * 批量自动赎回Job
     * @param orderService
     * @return
     */
    @Bean
    public Job order(OrderService orderService) {
        return new OrderJob(
           jobBuilderFactory,
           stepBuilderFactory,
           orderService
        ).buildJob();
    }


    @Bean
    public Job goods(GoodsService goodsService) {
        return new GoodsJob(
                jobBuilderFactory,
                stepBuilderFactory,
                goodsService
        ).buildJob();
    }


}
