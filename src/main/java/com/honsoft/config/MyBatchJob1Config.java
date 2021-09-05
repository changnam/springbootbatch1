package com.honsoft.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.honsoft.tasklet.SimpleTasklet;

@Configuration
public class MyBatchJob1Config {
	
	@Autowired
	JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job myJob1(@Qualifier("myStep1") Step step1) {
		return jobBuilderFactory.get("SimpleJob").incrementer(new RunIdIncrementer()).flow(step1).end().build();
		
	}
	
	@Bean
	public Step myStep1(@Qualifier("myTasklet2") Tasklet tasklet1) {
		return stepBuilderFactory.get("SimpleStep").tasklet(tasklet1).build();
		
	}
	
	@Bean
	public Tasklet myTasklet1() {
		return new SimpleTasklet();
	}
}
