package fastcampus.spring.batch.levelup;

import fastcampus.spring.batch.UserRepository;
import lombok.extern.slf4j.Slf4j;
import fastcampus.spring.batch.User;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class LevelUpJobExecutionListener implements JobExecutionListener {

    private final UserRepository userRepository;
    private final MailSender mailSender;
    private final StopWatch stopWatch;

    public LevelUpJobExecutionListener(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.mailSender = new MailSender();
        this.stopWatch = new StopWatch();
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        stopWatch.start();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        List<User> users = userRepository.findAllByUpdatedDate(LocalDate.now()).stream()
                .peek(mailSender::send)
                .collect(Collectors.toList());

        stopWatch.stop();

        log.info("회원등급 업데이트 배치 프로그램");
        log.info("-------------------------------");
        log.info("총 데이터 처리 {}건, 처리 시간 : {}millis", users.size(), stopWatch.getTotalTimeMillis());
    }
}
