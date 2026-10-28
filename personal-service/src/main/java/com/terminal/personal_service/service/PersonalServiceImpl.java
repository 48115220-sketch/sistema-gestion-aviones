package com.terminal.personal_service.service;

import com.terminal.personal_service.dto.PersonalAltaDTO;
import com.terminal.personal_service.dto.PersonalDTO;
import com.terminal.personal_service.entidad.Personal;
import com.terminal.personal_service.exception.ResourceNotFoundException;
import com.terminal.personal_service.repository.PersonalRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class PersonalServiceImpl implements PersonalService {

    @Autowired
    private PersonalRepository personalRepository;

    @Override
    public List<PersonalDTO> obtenerTodos() {
        log.debug("Recuperando listado completo del personal de vuelo");
        return personalRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonalDTO obtenerPorId(Integer id) {
        log.debug("Buscando personal de vuelo por ID: {}", id);
        return personalRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResourceNotFoundException("Personal de vuelo con ID " + id + " no encontrado."));
    }

    @Override
    public PersonalDTO guardar(PersonalAltaDTO altaDTO) {
        log.info("Guardando nuevo personal de vuelo: {} {}", altaDTO.nombre(), altaDTO.apellido());
        
        if (altaDTO.dni() != null && personalRepository.existsByDni(altaDTO.dni())) {
            throw new IllegalArgumentException("El DNI " + altaDTO.dni() + " ya se encuentra registrado.");
        }

        Personal personal = new Personal();
        personal.setNombre(altaDTO.nombre());
        personal.setApellido(altaDTO.apellido());
        personal.setDni(altaDTO.dni());
        personal.setSueldo(altaDTO.sueldo());
        personal.setCargo(altaDTO.cargo());
        personal.setTipo(altaDTO.tipo());

        Personal guardado = personalRepository.save(personal);
        log.info("Personal guardado exitosamente con ID: {}", guardado.getId());
        return convertirADTO(guardado);
    }

    @Override
    public PersonalDTO actualizar(Integer id, PersonalAltaDTO altaDTO) {
        log.info("Actualizando personal con ID: {}", id);
        
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personal de vuelo con ID " + id + " no encontrado."));

        if (altaDTO.dni() != null && personalRepository.existsByDniAndIdNot(altaDTO.dni(), id)) {
            throw new IllegalArgumentException("El DNI " + altaDTO.dni() + " ya pertenece a otro personal de vuelo.");
        }

        personal.setNombre(altaDTO.nombre());
        personal.setApellido(altaDTO.apellido());
        personal.setDni(altaDTO.dni());
        personal.setSueldo(altaDTO.sueldo());
        personal.setCargo(altaDTO.cargo());
        personal.setTipo(altaDTO.tipo());

        Personal actualizado = personalRepository.save(personal);
        log.info("Personal ID {} actualizado con éxito", id);
        return convertirADTO(actualizado);
    }

    @Override
    public boolean eliminar(Integer id) {
        log.info("Eliminando personal con ID: {}", id);
        if (!personalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Personal de vuelo con ID " + id + " no encontrado.");
        }
        personalRepository.deleteById(id);
        log.info("Personal ID {} eliminado correctamente", id);
        return true;
    }

    private PersonalDTO convertirADTO(Personal personal) {
        return new PersonalDTO(
                personal.getId(),
                personal.getNombre(),
                personal.getApellido(),
                personal.getDni(),
                personal.getSueldo(),
                personal.getCargo(),
                personal.getTipo()
        );
    }
}