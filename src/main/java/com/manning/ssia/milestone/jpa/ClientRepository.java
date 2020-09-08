package com.manning.ssia.milestone.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {

 Optional<Client> findByName(String name);
 boolean existsByName(String name);
 }
