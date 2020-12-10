package fastcampus.spring.batch;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor
@ToString(exclude = "orders")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Enumerated(EnumType.STRING)
    private Level level = Level.NORMAL;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Orders> orders;

    private LocalDate updatedDate;

    @Builder
    private User(String username, List<Orders> orders) {
        this.username = username;
        this.orders = orders;
    }

    public boolean availableLevelUp() {
        return Level.availableLevelUp(this.getLevel(), this.getTotalAmount());
    }

    public Level levelUp() {
        Level nextLevel = Level.getNextLevel(this.getTotalAmount());
        this.level = nextLevel;
        this.updatedDate = LocalDate.now();
        return nextLevel;
    }

    public int getTotalAmount() {
        return orders.stream()
                .mapToInt(Orders::getAmount)
                .sum();
    }

    public enum Level {
        VIP(500_000, null),
        GOLD(500_000, Level.VIP),
        SILVER(300_000, Level.GOLD),
        NORMAL(200_000, Level.SILVER);

        private final int nextAmount;
        private final Level nextLevel;

        Level(int nextAmount, Level nextLevel) {
            this.nextAmount = nextAmount;
            this.nextLevel = nextLevel;
        }

        public static boolean availableLevelUp(Level level, int totalAmount) {
            if (Objects.isNull(level)) {
                return false;
            }

            if (Objects.isNull(level.nextLevel)) {
                return false;
            }

            return totalAmount >= level.nextAmount;
        }

        public static Level getNextLevel(int totalAmount) {
            if (totalAmount >= Level.VIP.nextAmount) {
                return Level.VIP;
            }

            if (totalAmount >= Level.GOLD.nextAmount) {
                return Level.VIP;
            }

            if (totalAmount >= Level.SILVER.nextAmount) {
                return Level.GOLD;
            }

            if (totalAmount >= Level.NORMAL.nextAmount) {
                return Level.SILVER;
            }

            return Level.NORMAL;
        }
    }
}
