package com.escuela.alumno_service.repository;

import com.escuela.alumno_service.entidad.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    List<Alumno> findByCursoIdAndEstado(Integer cursoId, String estado);
}
