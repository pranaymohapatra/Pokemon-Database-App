package com.pranay.pokemon.domain

import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.domain.repository.PokemonSpeciesRepo
import dagger.hilt.android.scopes.ViewModelScoped
import io.reactivex.Single
import javax.inject.Inject

@ViewModelScoped
class FetchSpeciesUseCase @Inject constructor(private val pokemonSpeciesRepo: PokemonSpeciesRepo) :
    SingleUseCase<List<PokemonSpecies>, Int>() {

    override fun executeInternal(requestParams: Int): Single<List<PokemonSpecies>> {
        requestMap["offset"] = requestParams * PAGE_SIZE
        return pokemonSpeciesRepo.getData(requestMap).compose { upstream ->
            upstream.doOnSuccess { items ->
                items.forEach { it.imageURL = String.format(imageURLHolder, it.id) }
            }
        }
    }
    companion object {
        const val PAGE_SIZE = 20
        val requestMap = hashMapOf("limit" to PAGE_SIZE, "offset" to 0)
        val imageURLHolder =
            """https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/%d.png"""
    }

}