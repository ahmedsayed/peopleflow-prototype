package com.pplflw.prototype.config.kafka;

import com.pplflw.prototype.domains.Employee;
import com.pplflw.prototype.domains.Employer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    Map<String, Object> configProps;

    @PostConstruct
    private void postConstruct() {
        configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    }

    @Bean
    public ProducerFactory<String, Employer> employerProducerFactory() {
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Employer> employerKafkaTemplate() {
        
        return new KafkaTemplate<>(employerProducerFactory());
    }

    @Bean
    public ProducerFactory<String, Employee> employeeProducerFactory() {

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Employee> employeeKafkaTemplate() {
        
        return new KafkaTemplate<>(employeeProducerFactory());
    }
}
