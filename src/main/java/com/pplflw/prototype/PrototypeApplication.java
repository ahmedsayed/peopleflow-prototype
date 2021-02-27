package com.pplflw.prototype;

import com.pplflw.prototype.domains.Employer;
import com.pplflw.prototype.repositories.EmployersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;

@SpringBootApplication
public class PrototypeApplication {

    @Autowired
    private EmployersRepository employersRepository;
    
    public static void main(String[] args) {
        SpringApplication.run(PrototypeApplication.class, args);
    }

    //TODO move
    @KafkaListener(id = "employers", 
            topicPartitions = {
                @TopicPartition(topic = "employers", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
    })
    private void testListener(@Payload Employer employer) {
        employersRepository.save(employer);
    }
}
