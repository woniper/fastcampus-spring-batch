package fastcampus.spring.batch.statistics;

import lombok.extern.slf4j.Slf4j;
import fastcampus.spring.batch.OrderStatistics;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class OrderStatisticsTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    public OrderStatisticsTasklet(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<OrderStatistics> orderStatistics = jdbcTemplate.query("select sum(amount) as amount, created_date " +
                        "from orders " +
                        "where created_date >= '2020-11-01' and created_date <= '2020-11-04' " +
                        "group by created_date ",
                (rs, rowNum) -> OrderStatistics.builder()
                        .amount(Long.parseLong(rs.getString("amount")))
                        .date(LocalDate.parse(rs.getString("created_date"), DateTimeFormatter.ISO_DATE))
                        .build());

        orderStatistics.forEach(x -> log.info("{} : {}", x.getDate(), x.getAmount()));

        return RepeatStatus.FINISHED;
    }
}
