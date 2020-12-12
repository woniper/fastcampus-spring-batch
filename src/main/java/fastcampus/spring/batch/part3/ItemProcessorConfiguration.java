package fastcampus.spring.batch.part3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Configuration
public class ItemProcessorConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public ItemProcessorConfiguration(JobBuilderFactory jobBuilderFactory,
                                      StepBuilderFactory stepBuilderFactory) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job itemProcessorJob() {
        return jobBuilderFactory.get("itemProcessorJob")
                .incrementer(new RunIdIncrementer())
                .start(compositeItemProcessorStep())
                .build();
    }

    @Bean
    public Step compositeItemProcessorStep() {
        return stepBuilderFactory.get("compositeItemProcessorStep")
                .<String, Integer>chunk(1)
                .reader(itemReader())
                .processor(new CompositeItemProcessorBuilder<String, Integer>()
                        .delegates(itemProcessor(), itemProcessor2())
                        .build())
                .writer(itemWriter())
                .build();
    }

    private ItemReader<String> itemReader() {
        return new CustomItemReader<>(Arrays.asList("1", "12", "123", "1234"));
    }

    private ItemProcessor<String, Integer> itemProcessor() {
        return String::length;
    }

    private ItemProcessor<Integer, Integer> itemProcessor2() {
        return item -> {
            if (item % 2 == 0) {
                return item;
            }

            return null;
        };
    }

    private ItemWriter<Integer> itemWriter() {
        return items -> items.forEach(x -> log.info(">>>>>>>>>>>>>>>>> " + x));
    }
}

