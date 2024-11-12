package cris.prs.msg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyTypedMessageFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class KafkaRestServiceTest {

    @Autowired
    private ReplyingKafkaTemplate<String,String,String> replyingKafkaTemplate;

    @Autowired
    private AppKafkaProperties appKafkaProperties;


    @GetMapping("/send")
    public void hello() throws ExecutionException, InterruptedException {
        log.info("inside hello");
        Message<String> msg = MessageBuilder.withPayload("test payload " + LocalDateTime.now().toString())
                .setHeader(KafkaHeaders.TOPIC, appKafkaProperties.getRequestTopic())
                        .build();
        if(!replyingKafkaTemplate.waitForAssignment(Duration.ofSeconds(10))){
            throw new IllegalStateException("Reply container did not initialize");
        }
        RequestReplyTypedMessageFuture<String,String,String> future = replyingKafkaTemplate.sendAndReceive(msg, new ParameterizedTypeReference<String>() {});

        msg = future.get();

        log.info("Received: {}",msg);
    }
}
