package com.terminal.vuelos_service.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "vuelos")
public class Vuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String numeroVuelo;
    private String origen;
    private String destino;
    private String horaSalida;
    private String horaLlegada;
    private Integer avionId;
    private String estado = "PROGRAMADO";

    public Vuelo() {}

    public Vuelo(Integer id, String numeroVuelo, String origen, String destino, String horaSalida, String horaLlegada, Integer avionId, String estado) {
        this.id = id;
        this.numeroVuelo = numeroVuelo;
        this.origen = origen;
        this.destino = destino;
        this.horaSalida = horaSalida;
        this.horaLlegada = horaLlegada;
        this.avionId = avionId;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumeroVuelo() { return numeroVuelo; }
    public void setNumeroVuelo(String numeroVuelo) { this.numeroVuelo = numeroVuelo; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }

    public String getHoraLlegada() { return horaLlegada; }
    public void setHoraLlegada(String horaLlegada) { this.horaLlegada = horaLlegada; }

    public Integer getAvionId() { return avionId; }
    public void setAvionId(Integer avionId) { this.avionId = avionId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}