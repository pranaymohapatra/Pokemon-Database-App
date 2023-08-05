package com.pranay.pokemon.data.dto

import com.pranay.pokemon.domain.models.EvolvedSpecies
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class SpeciesDetailDTO(
    val capture_rate: Int,
    val description: List<Map<String, String>>,
    val evolution_chain: Map<String, List<EvolvedSpecies>>
)
@JsonClass(generateAdapter = true)
data class SpeciesDetailsGQLData(val species_details: SpeciesDetailDTO)