package com.exploration.service.postgres.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.exploration.service.postgres.dto.PersonEvent;
import com.exploration.service.postgres.entity.Person;
import com.exploration.service.postgres.repository.PersonRepository;

@Component
public class PersonKafkaConsumer {
    private final PersonRepository repo;

    public PersonKafkaConsumer(PersonRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "service-postgres")
    public void consume(PersonEvent event) {
        // Abaikan event yang berasal dari Postgres sendiri
        if ("postgres".equals(event.source())) {
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


