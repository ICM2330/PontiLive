package com.example.pontiliveapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

public class Usuario  {

    private String urlImagen;
    private String objectID;
    private String nombre;
    private String usuario;
    private String contrasena;
    private String descripcion;
    private String personalizable1;
    private String personalizable2;

    // Constructor completo
    public Usuario(String nombre, String usuario, String contrasena, String urlImagen, String descripcion, String personalizable1, String personalizable2) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.urlImagen = urlImagen;
        this.descripcion = descripcion;
        this.personalizable1 = personalizable1;
        this.personalizable2 = personalizable2;
    }

    // Constructor sin urlImagen, descripcion y personalizable1/2
    public Usuario(String usuario, String urlImagen, String objectID) {
        this.usuario = usuario;
        this.urlImagen = urlImagen;
        this.objectID = objectID;

    }



    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPersonalizable1() {
        return personalizable1;
    }

    public void setPersonalizable1(String personalizable1) {
        this.personalizable1 = personalizable1;
    }

    public String getPersonalizable2() {
        return personalizable2;
    }

    public void setPersonalizable2(String personalizable2) {
        this.personalizable2 = personalizable2;
    }
}
