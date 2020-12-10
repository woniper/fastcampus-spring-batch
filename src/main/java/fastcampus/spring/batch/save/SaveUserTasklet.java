package fastcampus.spring.batch.save;

import fastcampus.spring.batch.UserRepository;
import fastcampus.spring.batch.Orders;
import fastcampus.spring.batch.User;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SaveUserTasklet implements Tasklet {

    private final int LOOP_SIZE = 100;
    private final UserRepository userRepository;

    public SaveUserTasklet(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        insert();

        return RepeatStatus.FINISHED;
    }

    private void insert() {
        List<User> users = createUsers();

        Collections.shuffle(users);

        userRepository.saveAll(users);
        userRepository.flush();
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();

        // normal
        for (int i = 0; i < LOOP_SIZE; i++) {
            users.add(User.builder()
                    .orders(Collections.singletonList(Orders.builder()
                            .createdDate(LocalDate.of(2020, 11, 1))
                            .itemName("item" + i)
                            .amount(1_000)
                            .build()))
                    .username("username" + i)
                    .build());
        }

        // silver
        for (int i = LOOP_SIZE; i < LOOP_SIZE * 2; i++) {
            users.add(User.builder()
                    .orders(Collections.singletonList(Orders.builder()
                            .createdDate(LocalDate.of(2020, 11, 2))
                            .itemName("item" + i)
                            .amount(200_001)
                            .build()))
                    .username("username" + i)
                    .build());
        }

        // gold
        for (int i = LOOP_SIZE * 2; i < LOOP_SIZE * 3; i++) {
            users.add(User.builder()
                    .orders(Collections.singletonList(Orders.builder()
                            .createdDate(LocalDate.of(2020, 11, 3))
                            .itemName("item" + i)
                            .amount(300_001)
                            .build()))
                    .username("username" + i)
                    .build());
        }

        // vip
        for (int i = LOOP_SIZE * 3; i < LOOP_SIZE * 4; i++) {
            users.add(User.builder()
                    .orders(Collections.singletonList(Orders.builder()
                            .createdDate(LocalDate.of(2020, 11, 4))
                            .itemName("item" + i)
                            .amount(500_001)
                            .build()))
                    .username("username" + i)
                    .build());
        }
        return users;
    }
}
