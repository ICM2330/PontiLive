package com.example.pontiliveapp.model

class Lugar {

    var nombre: String
    var direccion: String
    var descripcion: String
    var urlImagen: String
    var latitud: Double
    var longitud: Double

    constructor(
        nombre: String,
        direccion: String,
        descripcion: String,
        urlImagen: String,
        latitud: Double,
        longitud: Double
    ) {
        this.nombre = nombre
        this.direccion = direccion
        this.descripcion = descripcion
        this.urlImagen = urlImagen
        this.latitud = latitud
        this.longitud = longitud
    }

}