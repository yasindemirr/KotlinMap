package com.demir.kotlinmap.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.demir.kotlinmap.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.demir.kotlinmap.databinding.ActivityMapsBinding
import com.demir.kotlinmap.model.Place
import com.demir.kotlinmap.roomDb.PlaceDao
import com.demir.kotlinmap.roomDb.PlaceDataBase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManagaer:LocationManager
    private lateinit var locationListener:LocationListener
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private var trakeBoolean:Boolean?=null
    private var selectedLatitude:Double?=null
    private var selectedLongitude:Double?=null
    private lateinit var db:PlaceDataBase
    private lateinit var dao: PlaceDao
    private  val compositeDisposable=CompositeDisposable()
    var placeFromMainActivity:Place?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()
        sharedPreferences=this.getSharedPreferences("com.demir.kotlinmap", MODE_PRIVATE)
        trakeBoolean=false
        selectedLatitude=0.0
        selectedLongitude=0.0
        db= Room.databaseBuilder(applicationContext,PlaceDataBase::class.java,"Places").build()
        dao=db.placeDao()
        binding.saveButton.isEnabled=false

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        val intent=intent
        val info=intent.getStringExtra("info")
        if (info=="new"){
            binding.saveButton.visibility=View.VISIBLE
            binding.deleteButton.visibility=View.GONE

            locationManagaer=this.getSystemService(LOCATION_SERVICE) as LocationManager
            //kullanıcının konumu değiştiğinde bildir.
            locationListener=object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    trakeBoolean= sharedPreferences.getBoolean("yasin",false)
                    if (trakeBoolean==false){
                        val userLocation=LatLng(location.latitude,location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                        sharedPreferences.edit().putBoolean("yasin",true).apply()
                    }


                }

            }
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.root,"Permission  needed for Location",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission"){
                            //request permission
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }.show()

                } else{
                    //request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }

            }else{
                //permission granted
                locationManagaer.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                val lastLocation=locationManagaer.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                lastLocation?.let {
                    val lastUserLocation= LatLng(lastLocation.latitude,lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))

                }
                mMap.isMyLocationEnabled=true
            }

        }else {
            mMap.clear()
            placeFromMainActivity=intent.getSerializableExtra("place",) as? Place
            placeFromMainActivity?.let {
                val ltlng= LatLng(it.latitude,it.longitude)
                mMap.addMarker(MarkerOptions().position(ltlng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltlng,14f))
                binding.placeNameText.setText(it.name)
                binding.saveButton.visibility=View.GONE
                binding.deleteButton.visibility=View.VISIBLE
            }

        }


        // Add a marker in Sydney and move the camera
        //53.2905185,-6.2819045
       /*
        val myHomeInIreland= LatLng(53.2905185,-6.2819045)
       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myHomeInIreland, 14F))
        mMap.addMarker(MarkerOptions().position(myHomeInIreland).title("yasin was here")
        */
        //casting yani bana bu sınıfı ayarla gibi




    }
    private fun registerLauncher(){
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                if (ContextCompat.checkSelfPermission(this ,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManagaer.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)

                }
                //permission granted


            }else   {
                //permission denied
                Toast.makeText(this,"Permission Needed",Toast.LENGTH_LONG).show()
                val lastLocation=locationManagaer.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                lastLocation?.let {
                    val lastUserLocation= LatLng(lastLocation.latitude,lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))

                }
                mMap.isMyLocationEnabled=true


            }
        }

    }

    override fun onMapLongClick(p0: LatLng) {

        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))
        selectedLatitude=p0.latitude
        selectedLongitude=p0.longitude
        binding.saveButton.isEnabled =true

    }
    fun delete(view: View?){
        placeFromMainActivity?.let {
            compositeDisposable.add(
                dao.deleteDb(it).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::HandlerResponse)
            )
        }

    }
    fun save(view: View){
        if (selectedLatitude!=null && selectedLongitude!=null&& binding.placeNameText.text.isNotEmpty()){
            val place=Place(binding.placeNameText.text.toString(),selectedLatitude!!,selectedLongitude!!)

            compositeDisposable.add(
                dao.insert(place).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::HandlerResponse)//işlem bittiğinde ne olacak
            )

        }else{
            Snackbar.make(view,"Place name is cannot be empty!!",Snackbar.LENGTH_SHORT).show()
        }


    }
    private fun HandlerResponse (){
        val intent= Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

}