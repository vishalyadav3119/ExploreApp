package com.example.exploreapp.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exploreapp.data.ApiResponse
import com.example.exploreapp.repository.MapRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
private const val INITIAL_ZOOM = 13f
@HiltViewModel
class MapViewModel @Inject constructor (
    private val repository:MapRepository,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val placesClient: PlacesClient
):ViewModel() {

    private val _liveData = MutableLiveData<ApiResponse>()
    val liveData get() = _liveData

    sealed class MapEvents{
        data class AnimateCameraToUserLocation(val location: Location,val zoom:Float): MapEvents()
    }
    private val channel = Channel<MapEvents>()
    val eventsFlow = channel.receiveAsFlow()

    fun processAfterPermissionCheck(){
        moveToUserLocation()
    }
    @SuppressLint("MissingPermission")
    fun moveToUserLocation(){
        val locationTask = fusedLocationProviderClient.lastLocation
        locationTask.addOnSuccessListener { location->
            viewModelScope.launch {
                channel.send(MapViewModel.MapEvents.AnimateCameraToUserLocation(location, INITIAL_ZOOM))
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun getNearbyPlaces(query:String, type:String?){
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {location->
            viewModelScope.launch {
                _liveData.value = repository.getNearbyPlaces(
                    keyword = query,
                    type=type,
                    location= location.latitude.toString()+","+location.longitude.toString(),
                    radius = 3000
                )
            }
        }
    }
}