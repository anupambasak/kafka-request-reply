package cris.prs.msg;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

   @Autowired
   private AppKafkaProperties appKafkaProperties;


    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, appKafkaProperties.getBootstrapServer());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, appKafkaProperties.getGroupId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> repliesContainerFactory(
            ConsumerFactory<String, String> cf) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(appKafkaProperties.getConcurrency());
        factory.setConsumerFactory(cf);
        return factory;
    }

    @Bean
    public NewTopic replyTopic() {
        return TopicBuilder.name(appKafkaProperties.getReplyTopic())
                .partitions(appKafkaProperties.getPartitions())
                .replicas(appKafkaProperties.getReplicas())
                .build();
    }

    @Bean
    public NewTopic requestTopic() {
        return TopicBuilder.name(appKafkaProperties.getRequestTopic())
                .partitions(appKafkaProperties.getPartitions())
                .replicas(appKafkaProperties.getReplicas())
                .build();
    }
}
