package com.terminal.aviones_service.service;

import com.terminal.aviones_service.client.PersonalClient;
import com.terminal.aviones_service.client.VueloClient;
import com.terminal.aviones_service.dto.AvionAltaDTO;
import com.terminal.aviones_service.dto.AvionConPersonalDTO;
import com.terminal.aviones_service.dto.PersonalDTO;
import com.terminal.aviones_service.dto.VueloDTO;
import com.terminal.aviones_service.entidad.Avion;
import com.terminal.aviones_service.repository.AvionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AvionServiceImpl implements AvionService {

    @Autowired
    private AvionRepository avionRepository;

    @Autowired(required = false)
    private PersonalClient personalClient;

    @Autowired(required = false)
    private VueloClient vueloClient;

    @Override
    public List<AvionConPersonalDTO> obtenerTodos() {
        log.debug("Obteniendo listado de todos los aviones");
        return avionRepository.findAll()
                .stream()
                .map(this::convertirAConPersonalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AvionConPersonalDTO obtenerPorId(Integer id) {
        log.debug("Buscando avion con ID: {}", id);
        return avionRepository.findById(id)
                .map(this::convertirAConPersonalDTO)
                .orElse(null);
    }

    @Override
    public AvionConPersonalDTO guardar(AvionAltaDTO altaDTO) {
        log.info("Creando nuevo avion modelo: {} | Personal ID: {} | Capacidad: {}", altaDTO.modelo(), altaDTO.personalId(), altaDTO.capacidadPersonas());
        Avion avion = new Avion();
        avion.setModelo(altaDTO.modelo());
        avion.setCapacidadPersonas(altaDTO.capacidadPersonas() != null ? altaDTO.capacidadPersonas() : 180);
        avion.setCapacidadEquipaje(altaDTO.capacidadEquipaje() != null ? altaDTO.capacidadEquipaje() : 5000.0f);
        avion.setPersonalId(altaDTO.personalId());
        avion.setEstado(altaDTO.estado() != null ? altaDTO.estado() : "EN_SERVICIO");

        Avion guardado = avionRepository.save(avion);
        log.info("Avion guardado exitosamente con ID: {}", guardado.getId());
        return convertirAConPersonalDTO(guardado);
    }

    @Override
    public AvionConPersonalDTO actualizar(Integer id, AvionAltaDTO altaDTO) {
        log.info("Actualizando avion ID: {}", id);
        return avionRepository.findById(id)
                .map(avion -> {
                    if (altaDTO.modelo() != null) avion.setModelo(altaDTO.modelo());
                    if (altaDTO.personalId() != null) avion.setPersonalId(altaDTO.personalId());
                    if (altaDTO.capacidadPersonas() != null) {
                        avion.setCapacidadPersonas(altaDTO.capacidadPersonas());
                    }
                    if (altaDTO.capacidadEquipaje() != null) {
                        avion.setCapacidadEquipaje(altaDTO.capacidadEquipaje());
                    }
                    if (altaDTO.estado() != null) {
                        avion.setEstado(altaDTO.estado());
                    }
                    Avion actualizado = avionRepository.save(avion);
                    log.info("Avion ID {} actualizado correctamente", id);
                    return convertirAConPersonalDTO(actualizado);
                })
                .orElseGet(() -> {
                    log.warn("No se encontro el avion ID {} para actualizar", id);
                    return null;
                });
    }

    @Override
    public boolean eliminar(Integer id) {
        log.info("Iniciando proceso de eliminacion para avion ID: {}", id);
        if (!avionRepository.existsById(id)) {
            log.warn("El avion ID {} no existe", id);
            return false;
        }

        if (vueloClient != null) {
            try {
                String token = obtenerTokenActual();
                log.debug("Consultando vuelos asignados al avion ID {} via VueloClient", id);
                List<VueloDTO> vuelos = vueloClient.obtenerTodosVuelos(token);
                if (vuelos != null) {
                    boolean tieneVuelos = vuelos.stream()
                            .anyMatch(v -> v.avionId() != null && v.avionId().equals(id));
                    if (tieneVuelos) {
                        log.warn("No se puede eliminar el avion ID {} porque contiene vuelos asignados", id);
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "No se puede eliminar el avion porque tiene vuelos asignados activos."
                        );
                    }
                }
            } catch (ResponseStatusException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error de comunicacion con vuelos-service al validar eliminacion del avion ID {}: {}", id, e.getMessage());
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "No se pudo verificar si el avion tiene vuelos asignados. Verifique que vuelos-service este activo."
                );
            }
        }

        avionRepository.deleteById(id);
        log.info("Avion ID {} eliminado de la base de datos", id);
        return true;
    }

    private AvionConPersonalDTO convertirAConPersonalDTO(Avion avion) {
        PersonalDTO personalDTO = null;
        if (avion.getPersonalId() != null && personalClient != null) {
            try {
                String token = obtenerTokenActual();
                log.debug("Consultando informacion del personal ID {} via PersonalClient", avion.getPersonalId());
                personalDTO = personalClient.obtenerPersonalPorId(token, avion.getPersonalId());
            } catch (Exception e) {
                log.warn("No se pudo obtener la informacion del personal ID {}: {}", avion.getPersonalId(), e.getMessage());
            }
        }
        return new AvionConPersonalDTO(
                avion.getId(),
                avion.getModelo(),
                avion.getCapacidadPersonas(),
                avion.getCapacidadEquipaje(),
                avion.getPersonalId(),
                avion.getEstado() != null ? avion.getEstado() : "EN_SERVICIO",
                personalDTO
        );
    }

    private String obtenerTokenActual() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            return request.getHeader("Authorization");
        }
        return null;
    }
}