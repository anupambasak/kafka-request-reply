package cris.prs.msg;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class RequestReplyKafkaService {


    @Autowired
    private KafkaConfig kafkaConfig;

    @KafkaListener(topics = "${kafka-config.request-topic}" , groupId = "${kafka-config.group-id}")
    @SendTo
    public Message<String> listen(ConsumerRecord<String, String> record) {
        String request = record.value();
//        record.headers().forEach( header -> {
//            log.info("Header ===>> {}:({}){}", header.key(), new String(header.value()),Arrays.toString(header.value()));
//        });

        // Process request
        String response = "Processed: " + request;
        // Get correlation ID from request headers
        Header correlationIdHeader = record.headers().lastHeader(KafkaHeaders.CORRELATION_ID);
        // Build and return the response message with the correlation ID in headers
        Header replyTopicHeader = record.headers().lastHeader(KafkaHeaders.REPLY_TOPIC);
        log.info("{}",response);
        return MessageBuilder.withPayload(response)
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationIdHeader != null ? correlationIdHeader.value() : null)
                .setHeader(KafkaHeaders.TOPIC, replyTopicHeader != null ? replyTopicHeader.value() : null )
                .build();
    }
}
