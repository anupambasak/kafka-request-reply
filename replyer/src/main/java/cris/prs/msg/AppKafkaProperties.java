package cris.prs.msg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConfigurationProperties(prefix = "kafka-config")
@Import({KafkaConfig.class})
@Getter
@Setter
@ToString
public class AppKafkaProperties {

    private String requestTopic;
    private String replyTopic;
    private String bootstrapServer;
    private String groupId;
    private int concurrency;
    private int partitions;
    private int replicas;

}
