package com.votingsystem.repository;

import com.votingsystem.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    Optional<Election> findByStatus(String status);
}
