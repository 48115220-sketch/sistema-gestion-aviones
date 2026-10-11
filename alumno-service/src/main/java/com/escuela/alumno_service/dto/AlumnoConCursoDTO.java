package com.escuela.alumno_service.dto;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class AlumnoConCursoDTO {
    private Integer id; private String nombre; private String apellido; private String dni; private Integer cursoId; private String estado; private CursoDTO curso;
}
