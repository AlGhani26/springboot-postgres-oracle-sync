package com.exploration.service.postgres.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exploration.service.postgres.dto.PersonEvent;
import com.exploration.service.postgres.entity.Person;
import com.exploration.service.postgres.repository.PersonRepository;

@Service
public class PersonService {
    private final PersonRepository repo;
    private final KafkaTemplate<String, PersonEvent> kafka;
    private final String topic;

    public PersonService(PersonRepository repo, KafkaTemplate<String, PersonEvent> kafka, 
                         @Value("${kafka.topic}") String topic) {
        this.repo = repo;
        this.kafka = kafka;
        this.topic = topic;
    }

    @Transactional
    public Person createOrUpdate(Person p){
        // Jika ID null, buat UUID baru
        if (p.getId() == null || p.getId().isBlank()) {
            p.setId(UUID.randomUUID().toString());
        }

        p.setUpdatedAt(Instant.now());
        Person saved = repo.save(p);

        // Deteksi CREATE vs UPDATE
        String opType = repo.existsById(saved.getId()) ? "UPDATE" : "CREATE";

        PersonEvent event = new PersonEvent(
            saved.getId(),
            saved.getName(),
            saved.getEmail(),
            saved.getUpdatedAt(),
            "postgres",
            opType
        );

        kafka.send(topic, event);
        return saved;
    }

    @Transactional
    public void delete(String id){
        Person p = repo.findById(id).orElse(null); 
        if (p == null) return; 

        repo.deleteById(id);

        PersonEvent event = new PersonEvent(
            id,
            null,
            null, 
            Instant.now(),
            "postgres",
            "DELETE"
        );

        kafka.send(topic, event);
    }
}
