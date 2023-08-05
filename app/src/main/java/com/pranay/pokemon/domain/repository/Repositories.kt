package com.pranay.pokemon.domain.repository

import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.domain.models.PokemonSpeciesDetail

interface PokemonSpeciesRepo : Repository<List<PokemonSpecies>, Map<String, Int>>

interface PokemonSpeciesDetailsRepo : Repository<PokemonSpeciesDetail, Map<String, Int>>