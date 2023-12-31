package com.example.pontiliveapp.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import com.example.pontiliveapp.databinding.ActivityRouteBinding
import com.example.pontiliveapp.model.Lugar
import com.example.pontiliveapp.model.getLugares
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.util.Date

class RouteActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityRouteBinding
    private lateinit var map: MapView // Mapa
    private lateinit var roadManager: RoadManager
    private var roadOverlay: Polyline? = null
    private lateinit var locationClient: FusedLocationProviderClient // Cliente de ubicación
    private lateinit var locationRequest: LocationRequest // Solicitud de ubicación
    private lateinit var locationCallback: LocationCallback // Callback de ubicación
    private lateinit var lastLocation: Location // Última ubicación conocida
    private lateinit var marker: Marker // Marcador
    private val permisosUbicacionRequestCode = 123 // Identificador único para la solicitud de permisos
    private var destino: Lugar? = null
    private var distancia: TextView? = null
    private var tiempoRestante: TextView? = null
    private var pasos: TextView? = null
    private var bandera = true
    private lateinit var sensorManager: SensorManager
    private lateinit var stepSensor: Sensor
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var jsonArray: JSONArray = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("RouteActivity", "onCreate ejecutándose")

        distancia = binding.distancia
        tiempoRestante = binding.tiempoRestante
        pasos = binding.pasos

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (sensor in sensorList) {
            Log.d("RouteActivity", "Sensor disponible: ${sensor.name}")
        }


        try {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!!
            if (stepSensor == null) {
                // El dispositivo no tiene el sensor de contador de pasos
                Toast.makeText(this,"Este dispositivo no admite el sensor de contador de pasos",Toast.LENGTH_SHORT).show()
            } else {
                // El sensor está disponible, puedes continuar con su uso
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Imprimir el mensaje de la excepción
            Log.e("RouteActivity", "Error al obtener el sensor de contador de pasos: ${e.message}")
            // Mostrar un mensaje al usuario
            Toast.makeText(this, "Error al obtener el sensor de contador de pasos", Toast.LENGTH_SHORT).show()
        }


        binding.locationButton.setOnClickListener() {
            //centrar el mapa en la ubicacion del usuario
            if (lastLocation != null) {
                map.controller.setZoom(18.0)
                val latitud = lastLocation.latitude
                val longitud = lastLocation.longitude
                val geoPoint = GeoPoint(latitud, longitud)
                map.controller.animateTo(geoPoint)
            }
        }

        binding.cancelButton.setOnClickListener() {
            finish()
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        //recibir los datos de la actividad anterior
        val nombreLugar = intent.getStringExtra("nombre")
        //buscar el lugar de destino en la base de datos
        destino = getLugares().find { it.nombre == nombreLugar }

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()
        locationCallback = createLocationCallBack()

        roadManager = OSRMRoadManager(this, "ANDROID")

        setMap()

        startLocationUpdates()

    }

    // metodo onPause
    override fun onPause() {
        super.onPause()
        locationClient.removeLocationUpdates(locationCallback)
        map.onPause()
        running = false
        Log.d("RouteActivity", "onPause ejecutándose")
    }

    // metodo onResume
    override fun onResume() {
        super.onResume()
        map.onResume()
        running = true
        Log.d("RouteActivity", "onResume ejecutándose")
    }

    // Listener para el sensor de pasos
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Manejar cambios en la precisión si es necesario
    }

    // Listener para el sensor de pasos
    override fun onSensorChanged(event: SensorEvent) {
        if (running) {
            totalSteps = event.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            binding.pasos.text = "Pasos: $currentSteps"
            Log.d("RouteActivity", "Pasos actualizados: $currentSteps")
        }
    }

    // funcion para establecer el mapa en la actividad
    fun setMap() {
        Configuration.getInstance().load(this,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))
        map = binding.osmMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
    }

    fun drawRoute(start: GeoPoint, finish: GeoPoint) {
        var routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)
        val road = roadManager.getRoad(routePoints)
        distancia!!.text = String.format("%.2f", road.mLength) + " km"
        tiempoRestante!!.text = String.format("%.0f", road.mDuration / 60) + " min"

        if (map != null) {
            if (roadOverlay != null) {
                map.getOverlays().remove(roadOverlay);
            }
            roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay!!.getOutlinePaint().setColor(Color.RED)
            roadOverlay!!.getOutlinePaint().setStrokeWidth(10F)
            map.getOverlays().add(roadOverlay)
        }
    }

    fun createLocationRequest(): LocationRequest {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(3000)
            .build()
        return locationRequest
    }

    fun createLocationCallBack(): LocationCallback {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                if (result != null) {
                    lastLocation = result.lastLocation!!
                    addLocation(lastLocation)
                    if (map != null) {
                        if (destino != null) {
                            // limbia el mapa, crea los dos marcadores y centra el mapa en la ubicacion del usuario
                            if (map.overlays.size > 0) {
                                map.overlays.clear()
                            }
                            val start = GeoPoint(lastLocation.latitude, lastLocation.longitude)
                            val finish = GeoPoint(destino!!.latitud, destino!!.longitud)
                            val startMarker = Marker(map)
                            startMarker.position = start
                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            startMarker.title = "Mi ubicación"
                            val finishMarker = Marker(map)
                            finishMarker.position = finish
                            finishMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            finishMarker.title = destino!!.nombre
                            map.overlays.add(startMarker)
                            map.overlays.add(finishMarker)
                            drawRoute(start, finish)
                            if (bandera) {
                                map.controller.setZoom(18.0)
                                val latitud = lastLocation.latitude
                                val longitud = lastLocation.longitude
                                val geoPoint = GeoPoint(latitud, longitud)
                                map.controller.animateTo(geoPoint)
                                bandera = false
                            }
                        }
                    }
                }
            }
        }
        return locationCallback
    }

    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ),
            permisosUbicacionRequestCode
        )
    }

    //funcion que añade una ubicacion a un archivo json con nombre historial.json
    fun addLocation(location: Location) {

        val jsonObj = JSONObject()
        try {
            jsonObj.put("latitud", location.latitude)
            jsonObj.put("longitud", location.longitude)
            val fecha = Date(location.time).toString()
            jsonObj.put("fecha", fecha)
            jsonArray.put(jsonObj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var output: Writer? = null
        val filename = "historial.json"
        try {
            val file = File(baseContext.getExternalFilesDir(null), filename)
            output = BufferedWriter(FileWriter(file))
            output.write(jsonArray.toString())
            output.close()
            //imprime un toast con la ubicion del archivo
            //Toast.makeText(this, "Ubicación guardada en: " + file.toString(), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}