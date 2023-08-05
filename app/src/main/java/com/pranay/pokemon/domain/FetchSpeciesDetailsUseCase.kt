package com.pranay.pokemon.domain

import com.pranay.pokemon.domain.models.CaptureDifficulty
import com.pranay.pokemon.domain.models.PokemonSpeciesDetail
import com.pranay.pokemon.domain.repository.PokemonSpeciesDetailsRepo
import io.reactivex.Single
import javax.inject.Inject

class FetchSpeciesDetailsUseCase @Inject constructor(private val detailsRepo: PokemonSpeciesDetailsRepo) :
    SingleUseCase<PokemonSpeciesDetail, FetchSpeciesDetailsUseCase.Params>() {

    override fun executeInternal(requestParams: Params): Single<PokemonSpeciesDetail> {
        requestMap["id"] = requestParams.id
        requestMap["order"] = requestParams.order
        return detailsRepo.getData(requestMap).compose { upstream ->
            upstream.doOnSuccess { item ->
                if (item.capture_rate - (item.evolvedSpecies?.capture_rate?:0) < 0)
                    item.captureTextColor = CaptureDifficulty.RED
                item.imageURL = String.format(imageURLHolder, requestParams.id)
                item.evolvedSpecies?.let {
                    it.imageUrl = String.format(imageURLHolder, it.id)
                }
            }
        }
    }

    companion object {
        val requestMap = hashMapOf<String, Int>()
        val imageURLHolder =
            """https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/%d.png"""
    }

    data class Params(val id: Int, val order: Int)

}