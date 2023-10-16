package com.example.pontiliveapp.model

import com.example.pontiliveapp.R

val lugaresBD = mutableListOf<Lugar>()

// metodo que retorna una lista de lugares ficticios. es la "Base de Datos"
fun getLugares(): List<Lugar> {

    lugaresBD.add(
        Lugar(
            "Edificio de Ingeniería",
            "Edificio 42 Cra. 7 No. 40-62",
            "Edificio de laboratorios de 14.000 metros cuadrados destinados a la docencia, investigación y servicio que impulsan el crecimiento y desarrollo tecnológico del país.",
            R.drawable.inge,
            4.626941979191182,
            -74.06392742281277,
            "Estudiadero",
            73,
            0.5
        )
    )

    lugaresBD.add(
        Lugar(
            "Donde Cris",
            "Cl. 41 #13a-17",
            "Bar insignia de los estudiantes javerianos.",
            R.drawable.cris,
            4.628996852919736,
            -74.06775163652283,
            "Tomadero",
            23,
            40.0
        )
    )

    lugaresBD.add(
        Lugar(
            "Biblioteca General",
            "Cra. 7 No. 41 - 00",
            "Biblioteca general de la universidad.",
            R.drawable.biblioteca,
            4.628813654211962,
            -74.06462521932352,
            "Estudiadero",
            50,
            32.0
        )
    )

    lugaresBD.add(
        Lugar(
            "Básicas",
            "Cra. 7 #44-84",
            "Facultad de ciencias básicas.",
            R.drawable.basicas,
            4.631084540493098,
            -74.06406711165323,
            "Estudiadero",
            60,
            200.0
        )
    )

    lugaresBD.add(
        Lugar(
            "Parque Nacional",
            "Cl. 35 #3-50",
            "Parque Nacional de Bogotá",
            R.drawable.parque,
            4.625673087851711,
            -74.06370327356615,
            "Pachadero",
            150,
            10.0
        )
    )

    lugaresBD.add(
        Lugar(
            "Canchas Sintéticas",
            "Cl. 35 #3-50",
            "Canchas multideportivas",
            R.drawable.canchas,
            4.627038546462039,
            -74.0628215518354,
            "Parchadero",
            10,
            12.0
        )
    )

    lugaresBD.add(
        Lugar(
            "Edificio Giraldo",
            "Cl. 40 #6-23",
            "Edificio insignia de la universidad.",
            R.drawable.giraldo,
            4.626798254866005,
            -74.06476855027721,
            "Estudiadero",
            48,
            30.0
        )
    )

    lugaresBD.add(
        Lugar(
            "Arqui-diseño",
            "Cra. 7 #40-2",
            "Facultad de Arqui-diseño.",
            R.drawable.arqui,
            4.627538871829708,
            -74.06475935038338,
            "Estudiadero",
            12,
            20.0
        )
    )

    lugaresBD.add(
        Lugar(
            "Artes",
            "Cl. 40b #5-37",
            "Facultad de Artes.",
            R.drawable.artes,
            4.62675228643284,
            -74.06443098620127,
            "Estudiadero",
            42,
            3.0
        )
    )

    lugaresBD.add(
        Lugar(
            "Arca",
            "Cra. 7 #40b - 36",
            "Edificio insiginia de la universidad.",
            R.drawable.arca,
            4.627841416703429,
            -74.06506410425487,
            "Estudiadero",
            14,
            20.0
        )
    )

    return lugaresBD
}

fun getLugarName(nombre: String): Lugar {
    for (lugar in lugaresBD) {
        if (lugar.nombre == nombre) {
            return lugar
        }
    }
    return Lugar()

}
