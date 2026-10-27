package com.terminal.aviones_service.entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aviones")
public class Avion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String modelo;
    private Integer capacidadPersonas;
    private Float capacidadEquipaje;
    private Integer personalId;
    private String estado;

    public Avion() {}

    public Avion(Integer id, String modelo, Integer capacidadPersonas, Float capacidadEquipaje, Integer personalId, String estado) {
        this.id = id;
        this.modelo = modelo;
        this.capacidadPersonas = capacidadPersonas;
        this.capacidadEquipaje = capacidadEquipaje;
        this.personalId = personalId;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getCapacidadPersonas() { return capacidadPersonas; }
    public void setCapacidadPersonas(Integer capacidadPersonas) { this.capacidadPersonas = capacidadPersonas; }

    public Float getCapacidadEquipaje() { return capacidadEquipaje; }
    public void setCapacidadEquipaje(Float capacidadEquipaje) { this.capacidadEquipaje = capacidadEquipaje; }

    public Integer getPersonalId() { return personalId; }
    public void setPersonalId(Integer personalId) { this.personalId = personalId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}