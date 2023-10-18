package com.example.pontiliveapp.model


class Lugar {

    lateinit var nombre: String
    lateinit var direccion: String
    lateinit var descripcion: String
    lateinit var urlImagen: String
    var IdImagen: Int = 0
    var latitud: Double = 0.0
    var longitud: Double = 0.0
    lateinit var espacio: String
    var poblacionActual: Int = 0
    var distanciaActual: Double = 0.0

    constructor(
        nombre: String,
        direccion: String,
        descripcion: String,
        urlImagen: String,
        latitud: Double,
        longitud: Double,
        espacio: String,
        poblacionActual: Int,
        distanciaActual: Double
    ) {
        this.nombre = nombre
        this.direccion = direccion
        this.descripcion = descripcion
        this.urlImagen = urlImagen
        this.latitud = latitud
        this.longitud = longitud
        this.espacio = espacio
        this.poblacionActual = poblacionActual
        this.distanciaActual = distanciaActual
    }

    constructor(
        nombre: String,
        direccion: String,
        descripcion: String,
        IdImagen: Int,
        latitud: Double,
        longitud: Double,
        espacio: String,
        poblacionActual: Int,
        distanciaActual: Double
    ) {
        this.nombre = nombre
        this.direccion = direccion
        this.descripcion = descripcion
        this.IdImagen = IdImagen
        this.latitud = latitud
        this.longitud = longitud
        this.espacio = espacio
        this.poblacionActual = poblacionActual
        this.distanciaActual = distanciaActual
    }

    constructor(
        nombre: String,
        direccion: String,
        descripcion: String,
        urlImagen: String,
        latitud: Double,
        longitud: Double,)
    {
        this.nombre = nombre
        this.direccion = direccion
        this.descripcion = descripcion
        this.urlImagen = urlImagen
        this.latitud = latitud
        this.longitud = longitud
    }
    constructor(){}

    override fun toString(): String {
        return "$descripcion"
    }

}