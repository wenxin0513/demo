package com.example.springbatch.controller;

import com.example.springbatch.util.SpringContextUtil;
import com.google.common.collect.Maps;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("job")
public class JobController {



    @GetMapping(value = "/batch")
    public void startOnceJob(String jobName) throws Exception{

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map parameters = Maps.newHashMap();
        parameters.put("time", new JobParameter(sf.format(new Date(System.currentTimeMillis()))));
        JobRegistry jobRegistry = SpringContextUtil.getBean(JobRegistry.class);
        Job job = jobRegistry.getJob(jobName);
        JobLauncher jobLauncher = SpringContextUtil.getBean(JobLauncher.class);
        JobExecution jobExecution = jobLauncher.run(job, new JobParameters(parameters));
        Long jobId = jobExecution.getJobId();
        System.out.println("Job任务正在处理。。。。");
    }
}
