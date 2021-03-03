package com.pplflw.prototype.listners;

import com.pplflw.prototype.domains.Employer;
import com.pplflw.prototype.repositories.EmployersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;

@Configuration
public class EmployerKafkaListener {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private final EmployersRepository employersRepository;

    @Autowired
    public EmployerKafkaListener(EmployersRepository employersRepository) {
        this.employersRepository = employersRepository;
    }

    @KafkaListener(id = "employers", 
            topicPartitions = {
                @TopicPartition(topic = "employers", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
    })
    private void employerListener(@Payload Employer employer) {
        
        log.info("listener received a new employer {}", employer.getName());
        employersRepository.save(employer);
    }
}
