package com.example.exploreapp.dao

import com.example.exploreapp.BuildConfig
import com.example.exploreapp.data.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface PlacesApi {
    companion object{
        const val BASE_URL = "https://maps.googleapis.com/maps/api/"
    }
    @GET("place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location:String,
        @Query("radius") radius:Int = 1500,
        @Query("type") type:String? = null,
        @Query("keyword") keyword:String = "restaurant",
        @Query("key") key:String = BuildConfig.GCP_KEY
    ):ApiResponse
}