package com.example.mymovieapp

import com.google.gson.annotations.SerializedName

data class TopRatedResponse(
    val page: Int,
    val results: List<Movie>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)
