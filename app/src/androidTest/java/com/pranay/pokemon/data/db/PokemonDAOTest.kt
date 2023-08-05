package com.pranay.pokemon.data.db

import androidx.room.EmptyResultSetException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test

class PokemonDAOTest {

    private lateinit var pokemonDb: PokemonDb
    private lateinit var pokemonDAO: PokemonDAO

    @Before
    fun setup() {
        pokemonDb = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PokemonDb::class.java
        ).allowMainThreadQueries().build()
        pokemonDAO = pokemonDb.pokemonDAO
    }

    @After
    fun tearDown() {
        pokemonDb.close()
    }

    @Test
    fun insertList_ReturnListLong() {
        val dummyEntities = mutableListOf<PokeSpeciesEntity>()
        for (i in 0..20) {
            dummyEntities.add(PokeSpeciesEntity(i + 1, "Pokemon #$i", i + 1))
        }
        pokemonDAO.insertPokemonList(dummyEntities).test().assertValueAt(0) {
            it.size > 20
        }
    }

    @Test
    fun insertDetail_ReturnLong() {
        pokemonDAO.insertPokemonDetails(
            PokeDetailEntity(
                1, 1, 255, "Strong",
                2, 2, "Stronger"
            )
        ).test().assertValueAt(0) {
            it == 1L
        }
    }

    @Test
    fun shouldThrowError_itemNotFound() {
        pokemonDAO.insertPokemonDetails(
            PokeDetailEntity(
                1, 1, 255, "Strong",
                2, 2, "Stronger"
            )
        ).test().assertValueAt(0) {
            it == 1L
        }
        pokemonDAO.getPokemonDetails(2).test().assertError {
            it is EmptyResultSetException && it.message != null
        }
    }

    @Test
    fun shouldEmitItem_ifItemFound() {
        pokemonDAO.insertPokemonDetails(
            PokeDetailEntity(
                1, 1, 255, "Strong",
                2, 2, "Stronger"
            )
        ).test().assertValueAt(0) {
            it == 1L
        }
        pokemonDAO.getPokemonDetails(1).test().assertValueAt(0) {
            it.id == 1
        }
    }

    @Test
    fun shouldEmitEmptyList_ifItemsNotFoundInGivenRange(){
        val dummyEntities = mutableListOf<PokeSpeciesEntity>()
        for (i in 0..19) {
            dummyEntities.add(PokeSpeciesEntity(i + 1, "Pokemon #$i", i + 1))
        }
        pokemonDAO.insertPokemonList(dummyEntities).test().assertValueAt(0) {
            it.size == 20
        }
        pokemonDAO.getPokemonList(20,20).test().assertValueAt(0){
            it.isEmpty()
        }
    }
}