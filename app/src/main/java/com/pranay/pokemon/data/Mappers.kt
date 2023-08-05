package com.pranay.pokemon.data

import com.pranay.pokemon.data.dto.SpeciesDetailsGQLData
import com.pranay.pokemon.data.dto.SpeciesGQLData
import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.domain.models.PokemonSpeciesDetail

fun SpeciesGQLData.mapToDomain(): List<PokemonSpecies> = species

fun SpeciesDetailsGQLData.mapToDomain(): PokemonSpeciesDetail {
    return with(species_details) {
        PokemonSpeciesDetail(
            capture_rate = capture_rate,
            description = description[0].values.first().replace("\n"," ").replace("\u000c"," "),
            evolvedSpecies = evolution_chain.values.first().getOrNull(0)
        )
    }
}