package com.terminal.vuelos_service.event;

import java.io.Serializable;

public class VueloEvent implements Serializable {
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