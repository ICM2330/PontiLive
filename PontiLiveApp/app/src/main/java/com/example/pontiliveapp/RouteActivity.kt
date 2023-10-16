package com.example.pontiliveapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.pontiliveapp.databinding.ActivityRouteBinding
import com.example.pontiliveapp.model.Lugar
import com.example.pontiliveapp.model.getLugares
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


class RouteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRouteBinding
    lateinit var map : MapView // Mapa
    private lateinit var roadManager: RoadManager
    private var roadOverlay: Polyline? = null
    lateinit var locationClient: FusedLocationProviderClient // Cliente de ubicación
    lateinit var locationRequest : LocationRequest // Solicitud de ubicación
    lateinit var locationCallback: LocationCallback // Callback de ubicación
    lateinit var lastLocation : Location // Última ubicación conocida
    lateinit var marker : Marker // Marcador
    private val permisosUbicacionRequestCode = 123 // Identificador único para la solicitud de permisos
    private var destino : Lugar? = null
    private var distancia : TextView? = null
    private var tiempoRestante : TextView? = null
    private var bandera = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        distancia = binding.distancia
        tiempoRestante = binding.tiempoRestante

        binding.locationButton.setOnClickListener(){
            //centrar el mapa en la ubicacion del usuario
            if(lastLocation!=null){
                map.controller.setZoom(18.0)
                val latitud = lastLocation.latitude
                val longitud = lastLocation.longitude
                val geoPoint = GeoPoint(latitud, longitud)
                map.controller.animateTo(geoPoint)
            }
        }

        binding.cancelButton.setOnClickListener(){
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
    }

    // metodo onResume
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    // funcion para establecer el mapa en la actividad
    fun setMap(){
        Configuration.getInstance().load(this,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))
        map = binding.osmMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
    }

    fun drawRoute(start : GeoPoint, finish : GeoPoint){
        var routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)
        val road = roadManager.getRoad(routePoints)
        distancia!!.text = String.format("%.2f", road.mLength) + " km"
        tiempoRestante!!.text = String.format("%.0f", road.mDuration/60) + " min"

        if(map!=null){
            if(roadOverlay != null){
                map.getOverlays().remove(roadOverlay);
            }
            roadOverlay = RoadManager.buildRoadOverlay(road)
            roadOverlay!!.getOutlinePaint().setColor(Color.RED)
            roadOverlay!!.getOutlinePaint().setStrokeWidth(10F)
            map.getOverlays().add(roadOverlay)
        }
    }

    fun createLocationRequest() : LocationRequest{
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(3000)
            .build()
        return locationRequest
    }

    fun createLocationCallBack() : LocationCallback{
        val locationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                if(result!=null){
                    lastLocation = result.lastLocation!!
                    if(map!=null){
                        if(destino!=null){
                            // limbia el mapa, crea los dos marcadores y centra el mapa en la ubicacion del usuario
                            if(map.overlays.size>0){
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
                            if(bandera){
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

    fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        }else{
            locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            permisosUbicacionRequestCode
        )
    }
}