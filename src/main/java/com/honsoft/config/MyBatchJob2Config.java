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
public class MyBatchJob2Config {
	
	@Autowired
	JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job myJob2(@Qualifier("myStep2") Step step2) {
		return jobBuilderFactory.get("SimpleJob2").incrementer(new DailyJobTimeStamper()).flow(step2).end().build();
		
	}
	
	@Bean
	public Step myStep2(@Qualifier("myTasklet2") Tasklet tasklet2) {
		return stepBuilderFactory.get("SimpleStep2").tasklet(tasklet2).build();
		
	}
	
	@Bean
	public Tasklet myTasklet2() {
		return new SimpleTasklet();
	}
}
