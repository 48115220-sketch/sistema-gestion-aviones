package com.terminal.personal_service.repository;

import com.terminal.personal_service.entidad.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Integer> {
    boolean existsByDni(String dni);
    boolean existsByDniAndIdNot(String dni, Integer id);
    Optional<Personal> findByDni(String dni);
}