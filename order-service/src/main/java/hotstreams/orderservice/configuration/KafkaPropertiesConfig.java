package hotstreams.orderservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("kafka")
@Getter
@Setter
public class KafkaPropertiesConfig {
    private String topic;
    private String bootstrapServers;
    private String producerClientId;
    private String consumerClientId;
    private String consumerGroup;
}
