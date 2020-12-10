package fastcampus.spring.batch.part3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class ItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    public ItemReaderConfiguration(JobBuilderFactory jobBuilderFactory,
                                   StepBuilderFactory stepBuilderFactory,
                                   DataSource dataSource,
                                   EntityManagerFactory entityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job itemReaderJob() {
        return jobBuilderFactory.get("itemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(customItemReaderStep())
                .next(csvFileStep())
                .next(jdbcStep())
                .next(jpaStep())
                .build();
    }

    @Bean
    public Step customItemReaderStep() {
        return stepBuilderFactory.get("customItemReaderStep")
                .<Person, Person>chunk(100)
                .reader(new CustomItemReader<>(getItems()))
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Step csvFileStep() {
        return stepBuilderFactory.get("personStep")
                .<Person, Person>chunk(100)
                .reader(csvFileItemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Step jdbcStep() {
        return stepBuilderFactory.get("jdbcStep")
                .<Person, Person>chunk(100)
                .reader(jdbcCursorItemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Step jpaStep() {
        return stepBuilderFactory.get("jpaStep")
                .<Person, Person>chunk(100)
                .reader(jpaCursorItemReader())
                .writer(itemWriter())
                .build();
    }

    private JpaCursorItemReader<Person> jpaCursorItemReader() {
        JpaCursorItemReader<Person> itemReader = new JpaCursorItemReader<>();

        itemReader.setEntityManagerFactory(entityManagerFactory);
        itemReader.setQueryString("select p from Person p");

        return itemReader;
    }


    private JdbcCursorItemReader<Person> jdbcCursorItemReader() {
        JdbcCursorItemReader<Person> itemReader = new JdbcCursorItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setSql("select * from person");
        itemReader.setRowMapper((rs, rowNum) -> new Person(rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4)));

        return itemReader;
    }

    private FlatFileItemReader<Person> csvFileItemReader() {
        FlatFileItemReader<Person> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setEncoding("UTF-8");
        flatFileItemReader.setResource(new ClassPathResource("test.csv"));
        flatFileItemReader.setLinesToSkip(1);

        DefaultLineMapper<Person> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "name", "age", "address");

        defaultLineMapper.setLineTokenizer(tokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSet -> {
            int id = fieldSet.readInt(0);
            String name = fieldSet.readString(1);
            String age = fieldSet.readString(2);
            String address = fieldSet.readString(3);

            return new Person(id, name, age, address);
        });

        flatFileItemReader.setLineMapper(defaultLineMapper);

        return flatFileItemReader;
    }

    private ItemWriter<Person> itemWriter() {
        return items -> {
            log.info(items.stream()
                    .map(Person::getName)
                    .collect(Collectors.joining(", ")));
        };
    }

    private List<Person> getItems() {
        List<Person> items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            items.add(new Person(i + 1, "test name" + i, "test age", "test address"));
        }

        return items;
    }

}

