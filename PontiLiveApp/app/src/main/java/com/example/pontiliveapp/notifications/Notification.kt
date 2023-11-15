package com.example.pontiliveapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.pontiliveapp.R

//Importar función en las clases que requieran mandar una notificación
//Uso: sendNotification(baseContext,<Clase objetivo al abrir la notificación>::class.java,"Mensaje de ejemplo", "Título de la notificación")

fun sendNotification(context: Context, targetActivity: Class<out AppCompatActivity>, message: String, title: String) {
    val channelId = "channel"

    // Crear un NotificationManager
    val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

    // Verificar si el dispositivo tiene Android Oreo (API 26) o superior, ya que se requieren canales de notificación
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Canal de notificaciones", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    // Crear un intent para abrir la actividad deseada al hacer clic en la notificación
    val intent = Intent(context, targetActivity::class.java)
// Esto evita la creación de múltiples instancias de la actividad
    val pendingIntent = PendingIntent.getActivity(context, 0, intent,
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

    // Crear una notificación utilizando NotificationCompat.Builder
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.logo_app) // Icono de la notificación (debe existir en tu proyecto)
        .setContentTitle(title) // Título de la notificación
        .setContentText(message) // Contenido de la notificación
        .setContentIntent(pendingIntent) // Asignar el intent a la notificación
        .setAutoCancel(true)

    // Mostrar la notificación
    notificationManager.notify(1, builder.build()) // El valor 1 es un identificador único para la notificación
}