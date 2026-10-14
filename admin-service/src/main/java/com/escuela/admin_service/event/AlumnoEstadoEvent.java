package com.escuela.admin_service.event;

public class AlumnoEstadoEvent {
    private Long idAlumno;
    private String estado;

    public AlumnoEstadoEvent() {}
    public AlumnoEstadoEvent(Long idAlumno, String estado) {
        this.idAlumno = idAlumno;
        this.estado = estado;
    }

    public Long getIdAlumno() { return idAlumno; }
    public void setIdAlumno(Long idAlumno) { this.idAlumno = idAlumno; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}