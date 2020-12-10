package fastcampus.spring.batch;

import fastcampus.spring.batch.save.SaveUserTasklet;
import lombok.extern.slf4j.Slf4j;
import fastcampus.spring.batch.levelup.LevelUpJobExecutionListener;
import fastcampus.spring.batch.save.SaveUserStepExecutionListener;
import fastcampus.spring.batch.statistics.OrderStatisticsTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Configuration
public class UserBatchConfiguration {

    private final int CHUNK = 1000;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final TaskExecutor taskExecutor;

    public UserBatchConfiguration(JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory,
                                  EntityManagerFactory entityManagerFactory,
                                  UserRepository userRepository,
                                  JdbcTemplate jdbcTemplate,
                                  TaskExecutor taskExecutor) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.taskExecutor = taskExecutor;
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("userLevelUpJob")
                .incrementer(new RunIdIncrementer())
                .start(this.saveUserStep())
                .next(this.userLevelUpStep())
                .next(this.orderStatisticsStep())
                .listener(new LevelUpJobExecutionListener(userRepository))
                .build();
    }

    @Bean
    public Step saveUserStep() {
        return this.stepBuilderFactory.get("saveUserStep")
                .tasklet(new SaveUserTasklet(userRepository))
                .listener(new SaveUserStepExecutionListener(userRepository))
                .build();
    }

    @Bean
    public Step userLevelUpStep() {
        return this.stepBuilderFactory.get("userLevelUpStep")
                .<User, User>chunk(CHUNK)
                .reader(this.userItemReader())
                .processor(this.userItemProcessor())
                .writer(this.userItemWriter())
                .faultTolerant()
                .build();
    }

    @Bean
    public Step orderStatisticsStep() {
        return this.stepBuilderFactory.get("orderStatisticsStep")
                .tasklet(new OrderStatisticsTasklet(jdbcTemplate))
                .build();
    }

    private JpaPagingItemReader<User> userItemReader() {
        return new JpaPagingItemReaderBuilder<User>()
                .queryString("select u from User u")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK)
                .name("userItemReader")
                .build();
    }

    private ItemProcessor<User, User> userItemProcessor() {
        return (user) -> {
            if (user.availableLevelUp()) {
                return user;
            }

            return null;
        };
    }

    private ItemWriter<User> userItemWriter() {
        return (users -> users.forEach(x -> {
            x.levelUp();
            userRepository.save(x);
        }));
    }

}
