package com.pplflw.prototype;

import com.pplflw.prototype.config.constants.KafkaConstants;
import com.pplflw.prototype.domains.Employer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * This is class is used to simulate another microservice (i.e.: Employers Service) by creating 
 * a compacted kafka topic with the key is the employer's id or code for keeping track of the last form
 * of each employer.
 * 
 * It's also used to initialize our own Employee topic, which in production I assume it will be
 * pre-created with more configurations. Hence, the assumption that we don't need it on production env.
 */
@Configuration
@Profile("!prod")  
public class StartupHousekeeper {

    @Autowired
    private KafkaTemplate<String, Employer> employerKafkaTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReadyEvent() {

        Employer employer = new Employer(1L, "Google", true);
        employerKafkaTemplate.send(KafkaConstants.EMPLOYERS_TOPIC_NAME, employer.getId().toString(), employer);

        employer = new Employer(2L, "Netflix", true);
        employerKafkaTemplate.send(KafkaConstants.EMPLOYERS_TOPIC_NAME, employer.getId().toString(), employer);

        employer = new Employer(3L, "Microsoft", true);
        employerKafkaTemplate.send(KafkaConstants.EMPLOYERS_TOPIC_NAME, employer.getId().toString(), employer);
    }

    @Bean
    public NewTopic createEmployersTopic() {

        Map<String, String> configMap = new HashMap<>();

        configMap.put("delete.retention.ms", "1");
        configMap.put("segment.ms", "1");
        configMap.put("min.cleanable.dirty.ratio", "0.01");

        return TopicBuilder.name(KafkaConstants.EMPLOYERS_TOPIC_NAME).partitions(1).replicas(1).compact()
                .configs(configMap).build();
    }

    @Bean
    public NewTopic createEmployeeTopic() {

        return TopicBuilder.name(KafkaConstants.EMPLOYEES_TOPIC_NAME).partitions(1).replicas(1).build();
    }
}
