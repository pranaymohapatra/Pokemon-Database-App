package com.pranay.pokemon.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GQLResponse<T>(
    @Json(name = "data")
    val responseData: T
)