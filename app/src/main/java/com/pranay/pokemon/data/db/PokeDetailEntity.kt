package com.pranay.pokemon.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pranay.pokemon.data.db.PokemonDb.Companion.TABLE_POKE_DETAIL

@Entity(tableName = TABLE_POKE_DETAIL,indices = [Index(value = ["id"])])
data class PokeDetailEntity(
    @PrimaryKey
    val id: Int,
    val order: Int,
    val captureRate: Int,
    val description: String,
    val evolvedCaptureRate: Int?,
    val evolvedId:Int?,
    val evolvedName:String?
)