package com.escuela.curso_service.service;

import com.escuela.curso_service.client.AlumnoClient;
import com.escuela.curso_service.client.DocenteClient;
import com.escuela.curso_service.dto.AlumnoDTO;
import com.escuela.curso_service.dto.CursoAltaDTO;
import com.escuela.curso_service.dto.CursoConDocenteDTO;
import com.escuela.curso_service.dto.DocenteDTO;
import com.escuela.curso_service.entidad.Curso;
import com.escuela.curso_service.repository.CursoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired(required = false)
    private DocenteClient docenteClient;

    @Autowired(required = false)
    private AlumnoClient alumnoClient;

    @Override
    public List<CursoConDocenteDTO> obtenerTodos() {
        return cursoRepository.findAll()
                .stream()
                .map(this::convertirAConDocenteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CursoConDocenteDTO obtenerPorId(Integer id) {
        return cursoRepository.findById(id)
                .map(this::convertirAConDocenteDTO)
                .orElse(null);
    }

    @Override
    public CursoConDocenteDTO guardar(CursoAltaDTO altaDTO) {
        Curso curso = new Curso();
        curso.setNombre(altaDTO.nombre());
        curso.setDocenteId(altaDTO.docenteId());

        Curso guardado = cursoRepository.save(curso);
        return convertirAConDocenteDTO(guardado);
    }

    @Override
    public CursoConDocenteDTO actualizar(Integer id, CursoAltaDTO altaDTO) {
        return cursoRepository.findById(id)
                .map(curso -> {
                    curso.setNombre(altaDTO.nombre());
                    curso.setDocenteId(altaDTO.docenteId());
                    Curso actualizado = cursoRepository.save(curso);
                    return convertirAConDocenteDTO(actualizado);
                })
                .orElse(null);
    }

    @Override
    public boolean eliminar(Integer id) {
        if (!cursoRepository.existsById(id)) {
            return false;
        }

        if (alumnoClient != null) {
            try {
                String token = obtenerTokenActual();
                List<AlumnoDTO> alumnos = alumnoClient.obtenerTodosAlumnos(token);
                if (alumnos != null) {
                    boolean tieneAlumnos = alumnos.stream()
                            .anyMatch(a -> a.cursoId() != null && a.cursoId().equals(id));
                    if (tieneAlumnos) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "No se puede eliminar el curso porque contiene alumnos asignados."
                        );
                    }
                }
            } catch (ResponseStatusException e) {
                throw e;
            } catch (Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "No se pudo verificar si el curso tiene alumnos. Verifique que alumno-service esté activo."
                );
            }
        }

        cursoRepository.deleteById(id);
        return true;
    }

    private CursoConDocenteDTO convertirAConDocenteDTO(Curso curso) {
        DocenteDTO docenteDTO = null;
        if (curso.getDocenteId() != null && docenteClient != null) {
            try {
                String token = obtenerTokenActual();
                docenteDTO = docenteClient.obtenerDocentePorId(token, curso.getDocenteId());
            } catch (Exception e) {
                System.err.println("No se pudo obtener la informacion del docente: " + e.getMessage());
            }
        }
        return new CursoConDocenteDTO(
                curso.getId(),
                curso.getNombre(),
                curso.getDocenteId(),
                docenteDTO
        );
    }

    private String obtenerTokenActual() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("Authorization");
            }
        } catch (Exception ignored) {}
        return null;
    }
}