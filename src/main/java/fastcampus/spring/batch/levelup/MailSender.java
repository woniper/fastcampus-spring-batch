package fastcampus.spring.batch.levelup;

import lombok.extern.slf4j.Slf4j;
import fastcampus.spring.batch.User;

@Slf4j
class MailSender {

    void send(User user) {
        log.info("send mail : {}", user);
    }

}
