package com.pranay.pokemon.data

import com.pranay.pokemon.data.db.PokeSpeciesEntity
import com.pranay.pokemon.data.db.PokemonDAO
import com.pranay.pokemon.data.dto.GQLRequestBody
import com.pranay.pokemon.data.remote.PokemonAPI
import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.domain.repository.PokemonSpeciesRepo
import com.pranay.pokemon.utils.BaseSchedulerProvider
import io.reactivex.Single
import javax.inject.Inject

class PokemonSpeciesRepository @Inject constructor(
    private val pokemonAPI: PokemonAPI,
    private val baseSchedulerProvider: BaseSchedulerProvider,
    private val pokemonDAO: PokemonDAO
) : PokemonSpeciesRepo, GQLRepository() {
    override fun getData(requestParams: Map<String, Int>): Single<List<PokemonSpecies>> {
        return if (!requestParams.containsKey("limit") || !requestParams.containsKey("offset"))
            Single.error { IllegalArgumentException("Request params doesn't contain correct keys") }
        else {
            val limit = requestParams["limit"]!!
            return getDataFromLocal(requestParams).flatMap {
                if (it.size < limit)
                    getDataFromNetwork(getRequestBody(requestParams))
                else
                    Single.just(it)
            }.subscribeOn(baseSchedulerProvider.io())
        }
    }

    private fun getDataFromNetwork(gqlRequestBody: GQLRequestBody): Single<List<PokemonSpecies>> {
        return pokemonAPI.getPokemonSpecies(gqlRequestBody).map { gqlResponse ->
            gqlResponse.responseData.mapToDomain()
        }.subscribeOn(baseSchedulerProvider.io())
            .doOnSuccess {
                val innerDisposable = insertIntoLocal(it).subscribeOn(baseSchedulerProvider.io())
                    .subscribe { _, error ->
                        error?.printStackTrace()
                    }
            }
    }

    private fun insertIntoLocal(pokemonList: List<PokemonSpecies>): Single<List<Long>> {
        val entityList = pokemonList.map { PokeSpeciesEntity(it.id, it.name, it.order) }
        return pokemonDAO.insertPokemonList(entityList)
    }

    private fun getDataFromLocal(requestParams: Map<String, Int>): Single<List<PokemonSpecies>> {
        return pokemonDAO.getPokemonList(requestParams["limit"] ?: 0, requestParams["offset"] ?: 0)
            .map {
                it.map { item -> PokemonSpecies(item.id, item.name, item.order) }
            }.subscribeOn(baseSchedulerProvider.io())
    }

    //String literals are much faster than keeping text in raw text files
    override val gqlQuery: String
        get() = """
            query getSpeciesListQuery(${'$'}limit: Int, ${'$'}offset: Int) {
              species: pokemon_v2_pokemonspecies(limit: ${'$'}limit, offset: ${'$'}offset, order_by: {id: asc}) {
                name
                id
                order
              }
            }
        """
    override val operationName: String
        get() = """getSpeciesListQuery"""

}