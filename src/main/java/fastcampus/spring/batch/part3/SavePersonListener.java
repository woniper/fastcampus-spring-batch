package fastcampus.spring.batch.part3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.*;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        public void afterStep(StepExecution stepExecution) {
            log.info("afterStep : {}", stepExecution.getWriteCount());
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
        public void annotationBeforeJob(JobExecution jobExecution) {
            log.info("annotationBeforeJob");
        }

        @AfterJob
        public void annotationAfterJob(JobExecution jobExecution) {
            int sum = jobExecution.getStepExecutions().stream()
                    .mapToInt(StepExecution::getWriteCount)
                    .sum();
            log.info("annotationAfterJob : {}", sum);
        }
    }

    public static class SavePersonRetryListener implements RetryListener {

        private final AtomicInteger count = new AtomicInteger();

        @Override
        public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
            return true;
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("close");
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("onError :{}", count.incrementAndGet());
        }
    }
}
