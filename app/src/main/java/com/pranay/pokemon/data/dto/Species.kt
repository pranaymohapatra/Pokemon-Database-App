package com.pranay.pokemon.data.dto

import com.pranay.pokemon.domain.models.PokemonSpecies
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpeciesGQLData(
    val species: List<PokemonSpecies>
)