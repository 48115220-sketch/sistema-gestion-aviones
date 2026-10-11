package com.escuela.alumno_service.service;

import com.escuela.alumno_service.config.RabbitMQConfig;
import com.escuela.alumno_service.dto.AlumnoAltaDTO;
import com.escuela.alumno_service.dto.AlumnoConCursoDTO;
import com.escuela.alumno_service.entidad.Alumno;
import com.escuela.alumno_service.event.AlumnoEvent;
import com.escuela.alumno_service.repository.AlumnoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlumnoServiceImpl implements AlumnoService {

    @Autowired private AlumnoRepository alumnoRepository;
    @Autowired(required = false) private RabbitTemplate rabbitTemplate;

    @Override public List<AlumnoConCursoDTO> obtenerTodos() { return alumnoRepository.findAll().stream().map(this::convertirAConCursoDTO).collect(Collectors.toList()); }
    @Override public AlumnoConCursoDTO obtenerPorId(Integer id) { return alumnoRepository.findById(id).map(this::convertirAConCursoDTO).orElse(null); }
    
    @Override public List<AlumnoConCursoDTO> obtenerPorCurso(Integer cursoId) {
        return alumnoRepository.findByCursoIdAndEstado(cursoId, "HABILITADO").stream().map(this::convertirAConCursoDTO).collect(Collectors.toList());
    }

    @Override public AlumnoConCursoDTO guardar(AlumnoAltaDTO altaDTO) {
        Alumno alumno = new Alumno(); alumno.setNombre(altaDTO.nombre()); alumno.setApellido(altaDTO.apellido()); alumno.setDni(altaDTO.dni()); alumno.setCursoId(altaDTO.cursoId()); alumno.setEstado("HABILITADO");
        Alumno guardado = alumnoRepository.save(alumno);
        if (rabbitTemplate != null && guardado.getCursoId() != null) {
            try { rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_ALUMNO_ALTA, new AlumnoEvent(guardado.getId().longValue(), guardado.getCursoId().longValue())); } catch (Exception e) {}
        }
        return convertirAConCursoDTO(guardado);
    }

    @Override public AlumnoConCursoDTO actualizar(Integer id, AlumnoAltaDTO altaDTO) {
        return alumnoRepository.findById(id).map(alumno -> {
            alumno.setNombre(altaDTO.nombre()); alumno.setApellido(altaDTO.apellido()); alumno.setDni(altaDTO.dni()); alumno.setCursoId(altaDTO.cursoId());
            return convertirAConCursoDTO(alumnoRepository.save(alumno));
        }).orElse(null);
    }

    @Override public boolean eliminar(Integer id) {
        return alumnoRepository.findById(id).map(alumno -> { alumno.setEstado("DESHABILITADO"); alumno.setCursoId(null); alumnoRepository.save(alumno); return true; }).orElse(false);
    }

    private AlumnoConCursoDTO convertirAConCursoDTO(Alumno alumno) {
        AlumnoConCursoDTO dto = new AlumnoConCursoDTO(); dto.setId(alumno.getId()); dto.setNombre(alumno.getNombre()); dto.setApellido(alumno.getApellido()); dto.setDni(alumno.getDni()); dto.setCursoId(alumno.getCursoId()); dto.setEstado(alumno.getEstado()); return dto;
    }
}
