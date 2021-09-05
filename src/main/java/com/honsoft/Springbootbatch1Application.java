package com.honsoft;

import java.util.List;

import javax.batch.runtime.JobExecution;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.PlatformTransactionManager;

import com.honsoft.config.UniqueNameGenerator;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(nameGenerator = UniqueNameGenerator.class)
@EnableBatchProcessing
public class Springbootbatch1Application implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(Springbootbatch1Application.class);

	@Autowired
	private JobOperator jobOperator;

	@Autowired
	private JobExplorer jobExplorer;

	public static void main(String[] args) {
		SpringApplication.run(Springbootbatch1Application.class, args);
	}

	@Bean
	public BatchConfigurer customBatchConfigurer() {
		return new DefaultBatchConfigurer() {
			@Autowired
			@Qualifier("mysqlDataSource")
			private DataSource dataSource;

			@Autowired
			@Qualifier("mysqlDataSourceTransactionManager")
			private PlatformTransactionManager transactionManager;

			@Override
			protected JobRepository createJobRepository() throws Exception {
				JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
				factory.setDataSource(dataSource);
				factory.setTransactionManager(transactionManager);
				// factory.setTablePrefix(null);
				factory.afterPropertiesSet();

				return factory.getObject();

			}

			@Override
			protected JobExplorer createJobExplorer() throws Exception {
				JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
				factory.setDataSource(dataSource);
				// factory.setTablePrefix(null);
				factory.afterPropertiesSet();

				return factory.getObject();
			}

		};
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(
			@Qualifier("jobRegistry") JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
		// jobRegistryBeanPostProcessor.afterPropertiesSet();

		return jobRegistryBeanPostProcessor;
	}

	@Bean
	public JobOperator jobOperator(@Qualifier("jobRepository") JobRepository jobRepository,
			@Qualifier("jobRegistry") JobRegistry jobRegistry, @Qualifier("jobExplorer") JobExplorer jobExplorer,
			@Qualifier("jobLauncher") JobLauncher jobLauncher) {
		SimpleJobOperator jobOperator = new SimpleJobOperator();
		jobOperator.setJobExplorer(jobExplorer);
		jobOperator.setJobLauncher(jobLauncher);
		jobOperator.setJobRegistry(jobRegistry);
		jobOperator.setJobRepository(jobRepository);

		return jobOperator;
	}

	@Override
	public void run(String... args) throws Exception {
		// JobInstance jobInstance = jobOperator.getJobInstances("SimpleJob", 0, 0);
		int instanceCnt = jobExplorer.getJobInstanceCount("SimpleJob");
		List<JobInstance> instanceList = jobExplorer.getJobInstances("SimpleJob", 0, instanceCnt);
		for (JobInstance instance : instanceList) {
			logger.info(instance.getId() + " , " + instance.getInstanceId() + " , " + instance.getJobName());
		}

		Long jobId = jobOperator.startNextInstance("SimpleJob");
		
		//if (jobId > 0) {
		//	JobInstance jobInstance = jobExplorer.getJobInstance(jobId);
		//	logger.info(jobInstance.getInstanceId() + " , " + jobInstance.getJobName());
		//}
		
		// List<Long> executionList = jobOperator.getExecutions(jobId);
		// for (Long id : executionList) {
		//	 logger.info(jobOperator.getSummary(id));
		// }
		
	}
}
