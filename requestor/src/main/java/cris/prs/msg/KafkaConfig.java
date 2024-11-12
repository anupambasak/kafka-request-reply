package cris.prs.msg;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Autowired
    private AppKafkaProperties appKafkaProperties;


    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, appKafkaProperties.getBootstrapServer());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, appKafkaProperties.getGroupId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate(
            ProducerFactory<String, String> pf,
            ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, String> replyContainer =
                kafkaListenerContainerFactory.createContainer(appKafkaProperties.getReplyTopic());
        replyContainer.getContainerProperties().setGroupId(appKafkaProperties.getGroupId());
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
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
