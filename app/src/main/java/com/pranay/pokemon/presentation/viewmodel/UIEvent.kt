package com.pranay.pokemon.presentation.viewmodel

import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.domain.models.PokemonSpeciesDetail

sealed class UIEvent {
    object FirstLoad : UIEvent()
    object EndOfPage : UIEvent()
}
