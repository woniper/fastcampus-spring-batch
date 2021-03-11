package fastcampus.spring.batch.part3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.*;

import java.util.List;

@Slf4j
public class SavePersonListener {

    public static class SavePersonSkipListener implements SkipListener<Person, Person> {

        // @OnSkipInRead
        @Override
        public void onSkipInRead(Throwable t) {

        }

        // @OnSkipInProcess
        @Override
        public void onSkipInProcess(Person item, Throwable t) {
            log.info("{}", item);
        }

        // OnSkipInWrite
        @Override
        public void onSkipInWrite(Person item, Throwable t) {
        }
    }

    public static class SavePersonStepListener {
        @BeforeStep
        public void beforeStep(StepExecution stepExecution) {
            log.info("beforeStep");
        }

        @AfterStep
        public ExitStatus afterStep(StepExecution stepExecution) {
            log.info("afterStep : {}", stepExecution.getWriteCount());
            return stepExecution.getExitStatus();
        }

        @BeforeRead
        public void beforeRead() {
            log.info("beforeRead");
        }

        @BeforeProcess
        public void beforeProcess() {
            log.info("beforeProcess");
        }

        @BeforeWrite
        public void beforeWrite() {
            log.info("beforeWrite");
        }

        @AfterRead
        public void afterRead(Person person) {
            log.info("afterRead : {}", person.getName());
        }

        @AfterProcess
        public void afterProcess(Person input, Person output) {
            log.info("afterProcess : {}", input.getName());
        }

        @AfterWrite
        public void afterWrite(List<Person> list) {
            log.info("afterWrite : {}", list.size());
        }
    }

    public static class SavePersonJobExecutionListener implements JobExecutionListener {
        @Override
        public void beforeJob(JobExecution jobExecution) {
            log.info("beforeJob");
        }

        @Override
        public void afterJob(JobExecution jobExecution) {
            int sum = jobExecution.getStepExecutions().stream()
                    .mapToInt(StepExecution::getWriteCount)
                    .sum();
            log.info("afterJob : {}", sum);
        }
    }

    public static class SavePersonAnnotationJobExecutionListener {
        @BeforeJob
        public void beforeJob(JobExecution jobExecution) {
            log.info("annotationBeforeJob");
        }

        @AfterJob
        public void afterJob(JobExecution jobExecution) {
            int sum = jobExecution.getStepExecutions().stream()
                    .mapToInt(StepExecution::getWriteCount)
                    .sum();
            log.info("annotationAfterJob : {}", sum);
        }
    }
}
