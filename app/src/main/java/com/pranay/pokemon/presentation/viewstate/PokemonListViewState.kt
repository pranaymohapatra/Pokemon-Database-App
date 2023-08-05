package com.pranay.pokemon.presentation.viewstate

import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.domain.models.PokemonSpeciesDetail

data class LoadListScreenUI(
    val result: List<PokemonSpecies> = emptyList(),
    val loading: Boolean,
    val isFirstPage: Boolean = false,
    val errorMessage: String? = null,
    var endReached: Boolean = false
)

data class DetailsUI(
    val id: Int = -1,
    val item: PokemonSpeciesDetail?,
    val isLoading: Boolean,
    val errorMessage: String? = null
)