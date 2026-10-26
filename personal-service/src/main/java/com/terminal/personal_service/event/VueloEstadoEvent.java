package com.terminal.personal_service.event;

public class VueloEstadoEvent {
    private Long idVuelo;
    private String estado;

    public VueloEstadoEvent() {}
    public VueloEstadoEvent(Long idVuelo, String estado) {
        this.idVuelo = idVuelo;
        this.estado = estado;
    }

    public Long getIdVuelo() { return idVuelo; }
    public void setIdVuelo(Long idVuelo) { this.idVuelo = idVuelo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}