package com.example.pontiliveapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pontiliveapp.databinding.ActivityMapBinding
import com.example.pontiliveapp.dialogs.EmprendimientosListDialogFragment
import com.example.pontiliveapp.dialogs.InfoDialogFragment
import com.example.pontiliveapp.model.Lugar
import com.example.pontiliveapp.model.getLugarName
import com.example.pontiliveapp.model.getLugares
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay


class MapActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMapBinding
    private val permisosUbicacionRequestCode = 123 // Identificador único para la solicitud de permisos
    lateinit var map : MapView // Mapa
    lateinit var locationClient: FusedLocationProviderClient // Cliente de ubicación
    lateinit var locationRequest : LocationRequest // Solicitud de ubicación
    lateinit var locationCallback: LocationCallback // Callback de ubicación
    lateinit var lastLocation : Location // Última ubicación conocida
    lateinit var marker : Marker // Marcador
    lateinit var lugares : List<Lugar> // Base de datos
    lateinit var markers : List<Marker> // Lista de marcadores de los lugares
    val bundle = Bundle()
    val fragB = InfoDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()
        locationCallback = createLocationCallBack()
        val extras = intent.extras

        // Verificar si ya se tienen permisos. si se tienen, se inicia la actualizacion de ubicacion.
        startLocationUpdates()

        // crear la base de datos
        lugares = getLugares()

        // configurar los botones
        setButtons()

        // configurar el mapa
        setMap()

        // establecer los marcadores
        setMarkers()

        // establecer los listeners de los marcadores
        setMarkerListeners()

        // mover la camara a Bogotá
        moveCamera(4.61, -74.07)


        //Variables para sensor luz
        sensorLightManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorLightManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!
        lightEventListener = createLightSensorListener()

        // variables para usar el barometro

        pressureTextView = binding.PressureTv
        circularProgressBar = binding.circularProgressBar
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        barometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE)

    }

    // metodo onPause
    override fun onPause() {
        super.onPause()
        locationClient.removeLocationUpdates(locationCallback)
        sensorManager?.unregisterListener(this)
        sensorLightManager.unregisterListener(lightEventListener)
        map.onPause()
    }

    // metodo onResume
    override fun onResume() {
        super.onResume()
        map.onResume()
        sensorManager?.registerListener(this, barometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorLightManager.registerListener(
            lightEventListener, lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    // funcion para configurar todos listeners de los botones
    fun setButtons(){
        binding.chatButton.setOnClickListener{
            startActivity(Intent(baseContext, ChatsMenuActivity::class.java))
        }

        binding.profileButton.setOnClickListener{
            startActivity(Intent(baseContext, ProfileActivity::class.java))
        }

        binding.locationButton.setOnClickListener{
            if(map!=null){
                if(lastLocation!=null){
                    map.controller.setZoom(18.0)
                    val latitud = lastLocation.latitude
                    val longitud = lastLocation.longitude
                    val geoPoint = GeoPoint(latitud, longitud)
                    map.controller.animateTo(geoPoint)
                }
            }
            setMyLocationMarker()
        }

        binding.emprendimientos.setOnClickListener {
            EmprendimientosListDialogFragment().show(supportFragmentManager, "dialog")
        }

    }

    // funcion para establecer el mapa en la actividad
    fun setMap(){
        Configuration.getInstance().load(this,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))
        map = binding.osmMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
    }


    // Función para verificar si se tienen los permisos necesarios
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Función para solicitar permisos de ubicacion al usuario
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            permisosUbicacionRequestCode
        )
    }

    // Manejar la respuesta de la solicitud de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permisosUbicacionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
                // imrpimir un toast de que se aceptaron los permisos
                Toast.makeText(this, "Permisos aceptados", Toast.LENGTH_SHORT).show()
            } else {
                // imrpimir un toast de que se rechazaron los permisos
                Toast.makeText(this, "Permisos rechazados", Toast.LENGTH_SHORT).show()
            }
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

    // funcion que mueve la camara, dadas la latitud y longitud.
    fun moveCamera(latitude: Double, longitude: Double){
        val geoPoint = GeoPoint(latitude, longitude)
        map.controller.animateTo(geoPoint)
        map.controller.setZoom(18.0)
    }

    //funcion que establece el marcador en la ubicacion actual (JAVERIANA)
    fun setMyLocationMarker(){
        if(this::marker.isInitialized){
            map.overlays.remove(marker)
        }
        marker = Marker(map)
        marker.position = GeoPoint(lastLocation.latitude, lastLocation.longitude)
        marker.title = "Mi ubicación"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)

    }

    // funcion que establece los listeners de los marcadores.
    fun setMarkerListeners(){
        for (marker in markers){
            marker.setOnMarkerClickListener { marker, mapView ->
                val lugar = lugares[markers.indexOf(marker)]
                bundle.putString("nombre", lugar.nombre)
                fragB.arguments = bundle
                fragB.show(supportFragmentManager, "dialog")
                true
            }
        }
    }

    // funcion que establece los marcadores de los lugares
    fun setMarkers(){
        markers = listOf()
        for (lugar in lugares){
            val marker = Marker(map)
            marker.position = GeoPoint(lugar.latitud, lugar.longitud)
            marker.title = lugar.nombre
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(marker)
            markers += marker
        }
        setMarkerListeners()
    }

    ////////////////////Barometro//////////////////
    private var sensorManager: SensorManager?= null
    private var barometerSensor: Sensor?= null
    private var pressureTextView: TextView?= null
    private var circularProgressBar: CircularProgressBar?= null


    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_PRESSURE){
            val pressureValue = event.values[0]
            pressureTextView!!.text = "Pressure: $pressureValue hPa"
            val maxPressure = 1030f
            circularProgressBar?.progress = (pressureValue / maxPressure)*1000
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        
    }

    ////////Sensor Luz//////////
    // Variables de sensor
    private lateinit var sensorLightManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var lightEventListener: SensorEventListener

    fun createLightSensorListener(): SensorEventListener {
        val ret: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (map != null) {
                    if (event != null) {
                        if (event.values[0] < 5000) {
                            // Modo oscuro
                            map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
                        } else {
                            // Modo claro
                            map.overlayManager.tilesOverlay.setColorFilter(null)
                        }
                    }
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            }
        }
        return ret
    }


}