package com.escuela.curso_service.entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private Integer docenteId;
    private Integer cupo = 30;

    public Curso() {}

    public Curso(Integer id, String nombre, Integer docenteId, Integer cupo) {
        this.id = id;
        this.nombre = nombre;
        this.docenteId = docenteId;
        this.cupo = cupo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getDocenteId() { return docenteId; }
    public void setDocenteId(Integer docenteId) { this.docenteId = docenteId; }

    public Integer getCupo() { return cupo; }
    public void setCupo(Integer cupo) { this.cupo = cupo; }
}