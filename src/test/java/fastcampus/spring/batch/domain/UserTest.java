package fastcampus.spring.batch.domain;

import fastcampus.spring.batch.Orders;
import fastcampus.spring.batch.User;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    public void silver() {
        // given
        User user = User.builder()
                .orders(Collections.singletonList(Orders.builder()
                        .itemName("item")
                        .amount(200_001)
                        .build()))
                .build();

        // when
        user.levelUp();

        // then
        assertThat(user.getLevel()).isEqualTo(User.Level.SILVER);
    }

    @Test
    public void gold() {
        // given
        User user = User.builder()
                .orders(Collections.singletonList(Orders.builder()
                        .itemName("item")
                        .amount(300_001)
                        .build()))
                .build();

        // when
        user.levelUp();

        // then
        assertThat(user.getLevel()).isEqualTo(User.Level.GOLD);
    }

    @Test
    public void vip() {
        // given
        User user = User.builder()
                .orders(Collections.singletonList(Orders.builder()
                        .itemName("item")
                        .amount(500_001)
                        .build()))
                .build();

        // when
        user.levelUp();

        // then
        assertThat(user.getLevel()).isEqualTo(User.Level.VIP);
    }
}
