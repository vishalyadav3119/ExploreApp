package com.example.exploreapp.di

import android.content.Context
import android.util.Log
import com.example.exploreapp.BuildConfig
import com.example.exploreapp.dao.PlacesApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val TAG = "AppModule"
@Module
@InstallIn(SingletonComponent::class)
class AppModule{
    @Singleton
    @Provides
    fun getRetrofit(client: OkHttpClient):Retrofit = Retrofit.Builder()
        .baseUrl(PlacesApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun getPlacesApi(retrofit: Retrofit): PlacesApi = retrofit.create(PlacesApi::class.java)

    @Singleton
    @Provides
    fun getFusedLocationProviderClient(@ApplicationContext context:Context):FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Singleton
    @Provides
    fun getHttpClient():OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
                val request = chain.request()
            Log.e(TAG, "getHttpClient: $request" )
                val response = chain.proceed(request)
            Log.e(TAG, "getHttpClient: ${response.body()}")
            response
            }.build()
    @Singleton
    @Provides
    fun getPlacesClient(@ApplicationContext context: Context):PlacesClient{
        Places.initialize(context, BuildConfig.GCP_KEY)
        return Places.createClient(context)
    }
}