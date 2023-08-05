package com.pranay.pokemon.data

import com.pranay.pokemon.data.db.PokeDetailEntity
import com.pranay.pokemon.data.db.PokemonDAO
import com.pranay.pokemon.data.remote.PokemonAPI
import com.pranay.pokemon.domain.models.EvolvedSpecies
import com.pranay.pokemon.domain.models.PokemonSpeciesDetail
import com.pranay.pokemon.domain.repository.PokemonSpeciesDetailsRepo
import com.pranay.pokemon.utils.BaseSchedulerProvider
import io.reactivex.Single
import javax.inject.Inject

class PokemonSpeciesDetailsRepository @Inject constructor(
    private val pokemonAPI: PokemonAPI,
    private val baseSchedulerProvider: BaseSchedulerProvider,
    private val pokemonDAO: PokemonDAO
) : PokemonSpeciesDetailsRepo, GQLRepository() {
    override fun getData(requestParams: Map<String, Int>): Single<PokemonSpeciesDetail> {
        return if (!requestParams.containsKey("id") || !requestParams.containsKey("order"))
            Single.error { IllegalArgumentException("Request params doesn't contain correct keys") }
        else getDataFromLocal(requestParams).subscribeOn(baseSchedulerProvider.io())
    }

    private fun getDataFromNetwork(requestParams: Map<String, Int>): Single<PokemonSpeciesDetail> {
        val id = requestParams["id"]!!
        val order = requestParams["order"]!!
        return pokemonAPI.getPokemonSpeciesDetails(getRequestBody(requestParams))
            .map { gqlResponse ->
                gqlResponse.responseData.mapToDomain()
            }.subscribeOn(baseSchedulerProvider.io())
            .doOnSuccess {
                val innerDisposable =
                    insertIntoLocal(id, order, it).subscribeOn(baseSchedulerProvider.io())
                        .subscribe { _, error ->
                            error?.printStackTrace()
                        }
            }
    }

    private fun insertIntoLocal(id: Int, order: Int, detail: PokemonSpeciesDetail): Single<Long> {
        detail.let {
            val pokeDetailEntity =
                PokeDetailEntity(
                    id, order, it.capture_rate, it.description,
                    it.evolvedSpecies?.capture_rate, it.evolvedSpecies?.id, it.evolvedSpecies?.name
                )
            return pokemonDAO.insertPokemonDetails(pokeDetailEntity)
        }
    }

    private fun getDataFromLocal(requestParams: Map<String, Int>): Single<PokemonSpeciesDetail> {
        return pokemonDAO.getPokemonDetails(requestParams["id"]!!).map {
            PokemonSpeciesDetail(
                capture_rate = it.captureRate, description = it.description,
                evolvedSpecies = if (it.evolvedId != null)
                    EvolvedSpecies(it.evolvedCaptureRate!!, it.evolvedId, it.evolvedName!!)
                else null
            )
        }.subscribeOn(baseSchedulerProvider.io()).onErrorResumeNext {
            getDataFromNetwork(requestParams)
        }
    }

    override val gqlQuery: String
        get() = """
            query getPokeSpeciesDetailsQuery(${'$'}id: Int!, ${'$'}order: Int!) {
              species_details: pokemon_v2_pokemonspecies_by_pk(id: ${'$'}id) {
                capture_rate
                description: pokemon_v2_pokemonspeciesflavortexts(limit: 1, where: {language_id: {_eq: 9}}) {
                  flavor_text
                }
                evolution_chain: pokemon_v2_evolutionchain {
                  evolved_species: pokemon_v2_pokemonspecies(limit: 1,order_by: {id: asc}, where: {order: {_gt: ${'$'}order}}) {
                    name
                    capture_rate
                    id
                  }
                }
              }
            }
        """
    override val operationName: String
        get() = "getPokeSpeciesDetailsQuery"
}