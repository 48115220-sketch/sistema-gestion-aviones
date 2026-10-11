package com.escuela.curso_service.event;

public class AlumnoEvent {
    private Long idAlumno;
    private Long idCurso;

    public AlumnoEvent() {}
    public AlumnoEvent(Long idAlumno, Long idCurso) {
        this.idAlumno = idAlumno;
        this.idCurso = idCurso;
    }

    public Long getIdAlumno() { return idAlumno; }
    public void setIdAlumno(Long idAlumno) { this.idAlumno = idAlumno; }

    public Long getIdCurso() { return idCurso; }
    public void setIdCurso(Long idCurso) { this.idCurso = idCurso; }
}
