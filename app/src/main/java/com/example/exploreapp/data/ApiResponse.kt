package com.example.exploreapp.data

data class ApiResponse(
    val html_attributions: List<Any>,
    val results: List<Result>,
    val status: String
)