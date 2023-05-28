package com.example.shoeshop.repository;

import com.example.shoeshop.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByUsername(String username);
    Optional<Person> findByActivationCode(String activationCode);
}
