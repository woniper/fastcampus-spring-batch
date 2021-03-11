package fastcampus.spring.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@EnableBatchProcessing
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
    }

    @Bean
    @Primary
    TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);   // 기본 thread 크기
        taskExecutor.setMaxPoolSize(20);    // 최대 thread 크기
        taskExecutor.setThreadNamePrefix("example-thread-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
