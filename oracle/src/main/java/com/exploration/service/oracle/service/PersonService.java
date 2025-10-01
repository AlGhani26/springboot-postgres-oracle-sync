package com.exploration.service.oracle.service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exploration.service.oracle.dto.PersonEvent;
import com.exploration.service.oracle.entity.Person;
import com.exploration.service.oracle.repository.PersonRepository;

@Service
public class PersonService {
    private final PersonRepository repo;
    private final KafkaTemplate<String, PersonEvent> kafka;
    private final String topic;
    private final AtomicLong idGenerator = new AtomicLong(1); // start dari 1

    public PersonService(PersonRepository repo, KafkaTemplate<String, PersonEvent> kafka, 
                         @Value("${kafka.topic}") String topic) {
        this.repo = repo;
        this.kafka = kafka;
        this.topic = topic;
    }

    private Long generateId() {
        return idGenerator.getAndIncrement();
    }

    @Transactional
    public Person createOrUpdate(Person p){
        if (p.getId() == null) {
            p.setId(generateId());
        }

        p.setUpdatedAt(Instant.now());
        Person saved = repo.save(p);

        PersonEvent event = new PersonEvent(
            saved.getId(),
            saved.getName(),
            saved.getEmail(),
            saved.getUpdatedAt(),
            "oracle" // atau postgres
        );

        kafka.send(topic, event);
        return saved;
    }
}


