package fastcampus.spring.batch;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OrderStatistics {
    private Long amount;
    private LocalDate date;

    @Builder
    private OrderStatistics(Long amount, LocalDate date) {
        this.amount = amount;
        this.date = date;
    }
}
