package com.terminal.aviones_service.event;

public class VueloEvent {
    private Long idVuelo;
    private Long idAvion;

    public VueloEvent() {}
    public VueloEvent(Long idVuelo, Long idAvion) {
        this.idVuelo = idVuelo;
        this.idAvion = idAvion;
    }

    public Long getIdVuelo() { return idVuelo; }
    public void setIdVuelo(Long idVuelo) { this.idVuelo = idVuelo; }

    public Long getIdAvion() { return idAvion; }
    public void setIdAvion(Long idAvion) { this.idAvion = idAvion; }
}