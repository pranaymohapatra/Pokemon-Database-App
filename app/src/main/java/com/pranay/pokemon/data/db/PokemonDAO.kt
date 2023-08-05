package com.pranay.pokemon.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pranay.pokemon.data.db.PokemonDb.Companion.TABLE_POKE_DETAIL
import com.pranay.pokemon.data.db.PokemonDb.Companion.TABLE_POKE_LIST
import io.reactivex.Single

@Dao
interface PokemonDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemonList(speciesList: List<PokeSpeciesEntity>): Single<List<Long>>

    @Query("SELECT * FROM $TABLE_POKE_LIST ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getPokemonList(limit: Int, offset: Int): Single<List<PokeSpeciesEntity>>

    @Query("SELECT * FROM $TABLE_POKE_DETAIL WHERE id=:id")
    fun getPokemonDetails(id: Int): Single<PokeDetailEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemonDetails(pokemonDetails: PokeDetailEntity): Single<Long>
}
