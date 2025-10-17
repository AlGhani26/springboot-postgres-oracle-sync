package com.exploration.service.oracle.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.exploration.service.oracle.dto.PersonEvent;
import com.exploration.service.oracle.entity.Person;
import com.exploration.service.oracle.repository.PersonRepository;

@Component
public class PersonKafkaConsumer {
    private final PersonRepository repo;

    public PersonKafkaConsumer(PersonRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "service-oracle")
    public void consume(PersonEvent event) {
        // Abaikan event yang berasal dari Oracle sendiri
        if ("oracle".equals(event.source())) {
            return;
        }

        if ("DELETE".equalsIgnoreCase(event.operationType())) {
            repo.deleteById(event.id());
            return;
        }

        Person p = repo.findById(event.id()).orElse(new Person());
        p.setId(event.id());
        p.setName(event.name());
        p.setEmail(event.email());
        p.setUpdatedAt(event.updatedAt());

        repo.saveAndFlush(p);
    }
}
