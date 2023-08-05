package com.pranay.pokemon.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pranay.pokemon.data.db.PokemonDb.Companion.VERSION

@Database(entities = [PokeDetailEntity::class,PokeSpeciesEntity::class], version = VERSION)
abstract class PokemonDb : RoomDatabase() {
    abstract val pokemonDAO: PokemonDAO

    companion object {
        const val VERSION = 1
        const val NAME = "PokemonDb"
        const val TABLE_POKE_DETAIL = "Poke_Detail"
        const val TABLE_POKE_LIST = "Poke_List"
    }
}