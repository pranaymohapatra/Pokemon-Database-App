package com.pranay.pokemon.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = PokemonDb.TABLE_POKE_LIST,indices = [Index(value = ["id"])])
data class PokeSpeciesEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val order: Int
)