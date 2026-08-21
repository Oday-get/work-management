package com.example.workmanagement.repository;

import com.example.workmanagement.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByUsernameIgnoreCase(String username);
    Optional<Person> findByUsername(String username);
}