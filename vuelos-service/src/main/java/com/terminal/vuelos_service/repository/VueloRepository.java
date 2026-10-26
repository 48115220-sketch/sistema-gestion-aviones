package com.terminal.vuelos_service.repository;

import com.terminal.vuelos_service.entidad.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VueloRepository extends JpaRepository<Vuelo, Integer> {
    List<Vuelo> findByAvionIdAndEstado(Integer avionId, String estado);
}