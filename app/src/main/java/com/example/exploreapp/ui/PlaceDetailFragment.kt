package com.example.exploreapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.exploreapp.R
import com.example.exploreapp.databinding.FragmentPlaceDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PlaceDetailFragment(private val placeItem:com.example.exploreapp.data.Result):BottomSheetDialogFragment(){
    private lateinit var binding: FragmentPlaceDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_place_detail,container,false)
        binding.item= this.placeItem
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .load(placeItem.icon)
            .into(binding.imageView)
        binding.getDirectionsButton.setOnClickListener{view->
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=${placeItem.geometry.location.lat},${placeItem.geometry.location.lng}")
            )
            startActivity(intent)
        }
    }
}