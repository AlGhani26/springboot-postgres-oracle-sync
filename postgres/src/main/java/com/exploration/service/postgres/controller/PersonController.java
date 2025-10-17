package com.exploration.service.postgres.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.exploration.service.postgres.entity.Person;
import com.exploration.service.postgres.repository.PersonRepository;
import com.exploration.service.postgres.service.PersonService;

@RestController
@RequestMapping("/api/person")
public class PersonController {
    private final PersonRepository repo;
    private final PersonService service;

    public PersonController(PersonRepository repo, PersonService service) {
        this.repo = repo;
        this.service = service;
    }

    @GetMapping
    public List<Person> all() {
        return repo.findAll();
    }

    @PostMapping
    public Person create(@RequestBody Person p) {
        return service.createOrUpdate(p);
    }

    @PutMapping("/{id}")
    public Person update(@PathVariable String id, @RequestBody Person p) {
        p.setId(id);
        return service.createOrUpdate(p);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
