package com.example.exploreapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.exploreapp.R
import com.example.exploreapp.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

private const val LOCATION_REQUEST_CODE = 2794
private const val INITIAL_ZOOM = 13f

class MapFragment(): Fragment(R.layout.fragment_map) {
    private lateinit var binding: FragmentMapBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)
        managePermissions()
        ///getting google map
        val supportMapFragment:SupportMapFragment = childFragmentManager.findFragmentById(R.id.google_map_fragment) as SupportMapFragment
        supportMapFragment.getMapAsync {map->
            this.map = map
            enableMyLocationButton()
        }
    }

    fun processAfterPermissionCheck(){
        moveToUserLocation()
    }
    @SuppressLint("MissingPermission")
    fun moveToUserLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val locationTask = fusedLocationProviderClient.lastLocation
        locationTask.addOnSuccessListener { location->
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),
                INITIAL_ZOOM))
        }
    }
    @SuppressLint("MissingPermission")
    fun enableMyLocationButton(){
        map.isMyLocationEnabled = true
    }
    fun managePermissions(){
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            processAfterPermissionCheck()
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)){
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }else{
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
        if (isGranted) processAfterPermissionCheck()
        else{
            //tell why we need it
        }
    }
}