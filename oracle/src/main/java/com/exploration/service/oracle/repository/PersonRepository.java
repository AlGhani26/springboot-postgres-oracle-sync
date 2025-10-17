package com.exploration.service.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exploration.service.oracle.entity.Person;

public interface PersonRepository extends JpaRepository<Person, String> {}
