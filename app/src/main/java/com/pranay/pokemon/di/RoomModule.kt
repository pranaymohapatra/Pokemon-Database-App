package com.pranay.pokemon.di

import android.content.Context
import androidx.room.Room
import com.pranay.pokemon.data.db.PokemonDAO
import com.pranay.pokemon.data.db.PokemonDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): PokemonDb {
        return Room.databaseBuilder(
            context,
            PokemonDb::class.java,
            PokemonDb.NAME
        ).build()
    }

    @Singleton
    @Provides
    fun providesFavMovieDao(pokemonDb: PokemonDb): PokemonDAO {
        return pokemonDb.pokemonDAO
    }

}