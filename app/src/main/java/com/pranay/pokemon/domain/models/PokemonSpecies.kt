package com.pranay.pokemon.domain.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonSpecies(
    val id: Int,
    val name: String,
    val order: Int
) {
    var imageURL: String = ""
    var bgColor: Int = -1
}

@JsonClass(generateAdapter = true)
data class PokemonSpeciesDetail(
    val capture_rate: Int,
    val description: String,
    val evolvedSpecies: EvolvedSpecies?
) {
    var captureTextColor: CaptureDifficulty = CaptureDifficulty.GREEN
    var imageURL: String = ""
}

@JsonClass(generateAdapter = true)
data class EvolvedSpecies(
    val capture_rate: Int,
    val id: Int,
    val name: String
) {
    var imageUrl = ""
}

enum class CaptureDifficulty {
    RED,
    GREEN
}