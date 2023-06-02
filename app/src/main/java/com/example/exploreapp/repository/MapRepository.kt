package com.example.exploreapp.repository

import android.location.Location
import com.example.exploreapp.dao.PlacesApi
import com.example.exploreapp.data.ApiResponse
import javax.inject.Inject


class MapRepository @Inject constructor(
    private val api: PlacesApi
) {
    suspend fun getNearbyPlaces(keyword:String, location: String, radius:Int, type:String?):ApiResponse{
        return api.getNearbyPlaces(
            keyword = keyword,
            location = location,
            radius = radius,
            type = type
        )
    }
}