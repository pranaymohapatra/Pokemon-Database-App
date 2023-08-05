package com.pranay.pokemon.di

import com.pranay.pokemon.data.PokemonSpeciesDetailsRepository
import com.pranay.pokemon.data.PokemonSpeciesRepository
import com.pranay.pokemon.domain.repository.PokemonSpeciesDetailsRepo
import com.pranay.pokemon.domain.repository.PokemonSpeciesRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    @ViewModelScoped
    abstract fun bindPokemonRepo(implementation: PokemonSpeciesRepository): PokemonSpeciesRepo

    @Binds
    @ViewModelScoped
    abstract fun bindPokemonDetailRepo(implementation: PokemonSpeciesDetailsRepository): PokemonSpeciesDetailsRepo
}