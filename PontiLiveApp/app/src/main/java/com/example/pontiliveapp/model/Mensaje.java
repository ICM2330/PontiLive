package com.example.pontiliveapp.model;

public class Mensaje {

    private String emisor;
    private String receptor;
    private String contenidoMensaje;

    private String fechaEmision;

    public Mensaje(String emisor, String receptor, String contenidoMensaje, String fechaEmision) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.contenidoMensaje = contenidoMensaje;
        this.fechaEmision = fechaEmision;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getContenidoMensaje() {
        return contenidoMensaje;
    }

    public void setContenidoMensaje(String contenidoMensaje) {
        this.contenidoMensaje = contenidoMensaje;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
}
