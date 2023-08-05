package com.pranay.pokemon.data.remote

import com.pranay.pokemon.data.dto.GQLRequestBody
import com.pranay.pokemon.data.dto.GQLResponse
import com.pranay.pokemon.data.dto.SpeciesDetailsGQLData
import com.pranay.pokemon.data.dto.SpeciesGQLData
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface PokemonAPI {
    @POST("v1beta")
    fun getPokemonSpecies(@Body gqlRequest: GQLRequestBody): Single<GQLResponse<SpeciesGQLData>>

    @POST("v1beta")
    fun getPokemonSpeciesDetails(@Body gqlRequest: GQLRequestBody): Single<GQLResponse<SpeciesDetailsGQLData>>
}