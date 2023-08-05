package com.pranay.pokemon.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GQLRequestBody(
    val query: String,
    val variables: Map<String, Any>,
    val operationName: String
)
