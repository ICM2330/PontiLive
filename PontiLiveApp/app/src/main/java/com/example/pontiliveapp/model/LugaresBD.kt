package com.example.pontiliveapp.model

// metodo que retorna una lista de lugares ficticios. es la "Base de Datos"
fun getLugares(): List<Lugar> {
    val lugaresFicticios = mutableListOf<Lugar>()

    lugaresFicticios.add(
        Lugar(
            "Playa del Sol",
            "123 Calle Arena",
            "Hermosa playa con aguas cristalinas.",
            "https://ejemplo.com/playa.jpg",
            4.6270,
            -74.0644
        )
    )

    lugaresFicticios.add(
        Lugar(
            "Montaña Aventura",
            "456 Ruta Montañosa",
            "Increíbles senderos y vistas panorámicas.",
            "https://ejemplo.com/montaña.jpg",
            4.6285,
            -74.0626
        )
    )

    lugaresFicticios.add(
        Lugar(
            "Restaurante Sabores",
            "789 Calle Gourmet",
            "Comida deliciosa de todo el mundo.",
            "https://ejemplo.com/restaurante.jpg",
            4.6300,
            -74.0644
        )
    )

    lugaresFicticios.add(
        Lugar(
            "Museo Histórico",
            "321 Calle Historia",
            "Arte y cultura en un entorno fascinante.",
            "https://ejemplo.com/museo.jpg",
            4.6302,
            -74.0660
        )
    )

    lugaresFicticios.add(
        Lugar(
            "Parque Tranquilo",
            "567 Avenida Verde",
            "Relajación en medio de la naturaleza.",
            "https://ejemplo.com/parque.jpg",
            4.6291,
            -74.0668
        )
    )

    lugaresFicticios.add(
        Lugar(
            "Cascada Escondida",
            "890 Camino Natural",
            "Un rincón secreto en la selva.",
            "https://ejemplo.com/cascada.jpg",
            4.6278,
            -74.0667
        )
    )

    return lugaresFicticios
}
