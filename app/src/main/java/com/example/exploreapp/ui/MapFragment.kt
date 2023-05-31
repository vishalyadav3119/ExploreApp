package com.example.exploreapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.exploreapp.R
import com.example.exploreapp.databinding.FragmentMapBinding

class MapFragment(): Fragment(R.layout.fragment_map) {
    private lateinit var binding: FragmentMapBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)
    }
}