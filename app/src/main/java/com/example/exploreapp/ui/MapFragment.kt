package com.example.exploreapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.exploreapp.R
import com.example.exploreapp.databinding.FragmentMapBinding
import com.example.exploreapp.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL


private const val LOCATION_REQUEST_CODE = 2794
private const val TAG = "MapFragment"

@AndroidEntryPoint
class MapFragment(): Fragment(R.layout.fragment_map) {
    private lateinit var binding: FragmentMapBinding
    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    private val viewModel:MapViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)
        ///getting google map

        mapFragment= childFragmentManager.findFragmentById(R.id.google_map_fragment) as SupportMapFragment
        mapFragment.getMapAsync {map->
            this.map = map
            managePermissions()
            enableMyLocationButton()
            setMarkerClickListener()
        }
        handleViewModelEvents()
        binding.filterChipGroup.setOnCheckedStateChangeListener(object:ChipGroup.OnCheckedStateChangeListener{
            override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {
                if(checkedIds.size == 1){
                    map.clear()
                    when(checkedIds[0]){
                        R.id.gym_chip ->{
                            viewModel.getNearbyPlaces(
                                query = binding.searchBar.text.toString(),
                                type = "Gym"
                            )
                        }
                        R.id.restaurant_chip->{
                            viewModel.getNearbyPlaces(
                                query = binding.searchBar.text.toString(),
                                type = "Restaurant"
                            )
                        }
                        R.id.museum_chip->{
                            viewModel.getNearbyPlaces(
                                query = binding.searchBar.text.toString(),
                                type = "Museum"
                            )
                        }
                        R.id.park_chip->{
                            viewModel.getNearbyPlaces(
                                query = binding.searchBar.text.toString(),
                                type = "Park"
                            )
                        }
                    }
                }
            }
        })
        binding.searchBar.setOnEditorActionListener(object:OnEditorActionListener{
            override fun onEditorAction(textView: TextView?, p1: Int, event: KeyEvent?): Boolean {

                if(event!=null){
                    if(textView!!.text.isEmpty()){
                        Toast.makeText(requireActivity(), "Search Cannot be empty", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    map.clear()
                    binding.filterChipGroup.visibility = View.VISIBLE
                    val searchText = textView?.text.toString()
                    viewModel.getNearbyPlaces(searchText, type = null)
                    Log.e(TAG, "onEditorAction: searchString is : $searchText" )
                    hideSoftKeyboard()
                    binding.searchBar.clearFocus()
                }
                return true
            }
        })
        handleViewModelResponse()
    }
    fun hideSoftKeyboard(){
        val imm: InputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    @SuppressLint("PotentialBehaviorOverride")
    fun setMarkerClickListener(){
        map.setOnMarkerClickListener {marker->
            val placeItem = marker.tag as com.example.exploreapp.data.Result
            PlaceDetailFragment(placeItem).show(childFragmentManager,"bottom_sheet")
            true
        }
    }
    fun handleViewModelResponse(){
        viewModel.liveData.observe(viewLifecycleOwner){apiResponse->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                for(item in apiResponse.results){
                    val loc = item.geometry.location
                    val url = URL(item.icon)

                    val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    launch(Dispatchers.Main) {
                        map.addMarker(MarkerOptions()
                            .position(LatLng(loc.lat,loc.lng))
                            .icon(BitmapDescriptorFactory.fromBitmap(image))
                            .title(item.name))
                            ?.setTag(item)
                    }
                }
            }
        }
    }
    fun handleViewModelEvents(){
        viewLifecycleOwner.lifecycleScope.launchWhenStarted{
            viewModel.eventsFlow.collect{event->
                when(event){
                    is MapViewModel.MapEvents.AnimateCameraToUserLocation->{
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(event.location.latitude,event.location.longitude),
                                event.zoom
                            ))
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun enableMyLocationButton(){
        map.isMyLocationEnabled = true
        val fragmentView = mapFragment.view?.findViewById<View>("1".toInt())
        val locationButton =
            (fragmentView?.parent as View).findViewById<View>("2".toInt())
        val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(50, 180, 180, 50)
    }
    private fun managePermissions(){
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            viewModel.processAfterPermissionCheck()
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)){
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }else{
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted->
        if (isGranted) viewModel.processAfterPermissionCheck()
        else{
            //tell why we need it
        }
    }
}