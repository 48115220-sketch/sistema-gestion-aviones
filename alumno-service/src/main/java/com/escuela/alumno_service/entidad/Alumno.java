package com.escuela.alumno_service.entidad;
import jakarta.persistence.*;
@Entity
public class Alumno {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    private String nombre; private String apellido; private String dni; private Integer cursoId; private String estado = "HABILITADO";
    public Alumno() {}
    public Integer getId() { return id; } public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; } public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; } public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; } public void setDni(String dni) { this.dni = dni; }
    public Integer getCursoId() { return cursoId; } public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }
    public String getEstado() { return estado; } public void setEstado(String estado) { this.estado = estado; }
}
