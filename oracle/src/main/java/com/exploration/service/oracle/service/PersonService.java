package com.exploration.service.oracle.service;

import java.time.Instant;

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

    public PersonService(PersonRepository repo, KafkaTemplate<String, PersonEvent> kafka, @Value("${kafka.topic}") String topic) {
        this.repo = repo;
        this.kafka = kafka;
        this.topic = topic;
    }

    @Transactional
    public Person createOrUpdate(Person p){
        p.setUpdatedAt(Instant.now());
        Person saved = repo.save(p);

        // Publish event to Kafka (source=postgres)
        PersonEvent event = new PersonEvent(saved.getId(), saved.getName(), saved.getEmail(), saved.getUpdatedAt(), "oracle");
        kafka.send(topic, event);
        return saved;
    }
}

