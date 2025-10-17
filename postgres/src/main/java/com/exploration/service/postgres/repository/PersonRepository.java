package com.exploration.service.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exploration.service.postgres.entity.Person;

// public interface PersonRepository extends JpaRepository<Person, Long> {}
public interface PersonRepository extends JpaRepository<Person, String> {}
