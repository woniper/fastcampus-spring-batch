package fastcampus.spring.batch.part4;

import fastcampus.spring.batch.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.util.List;

@Slf4j
public class SaveUserStepExecutionListener implements StepExecutionListener {

    private final UserRepository userRepository;

    public SaveUserStepExecutionListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {}

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        List<User> users = userRepository.findAll();
        log.info("saved user size : {}", users.size());

        return stepExecution.getExitStatus();
    }

}
