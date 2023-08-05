package com.pranay.pokemon.domain.repository

import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.domain.models.PokemonSpeciesDetail
import io.reactivex.Single

interface Repository<T, Params> {
    fun getData(requestParams: Params): Single<T>
}