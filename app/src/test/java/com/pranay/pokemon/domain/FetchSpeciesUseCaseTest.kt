package com.pranay.pokemon.domain

import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.domain.repository.PokemonSpeciesRepo
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class FetchSpeciesUseCaseTest {

    @Mock
    private lateinit var pokemonSpeciesRepo: PokemonSpeciesRepo

    private lateinit var fetchSpeciesUseCase: FetchSpeciesUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `when repo returns success, all img urls must be set`() {
        val dummy = mutableListOf<PokemonSpecies>()
        for (i in 0..20) {
            dummy.add(PokemonSpecies(i + 1, "Pokemon #$i", i + 1))
        }
        Mockito.`when`(pokemonSpeciesRepo.getData(anyMap())).thenReturn(Single.just(dummy))
        fetchSpeciesUseCase = FetchSpeciesUseCase(pokemonSpeciesRepo)
        fetchSpeciesUseCase.execute(0).test().assertValueCount(1).assertValueAt(0) {
            var test = true
            it.forEachIndexed { index, item ->
                test = test && item.imageURL == String.format(
                    FetchSpeciesUseCase.imageURLHolder,
                    index + 1
                )
            }
            test
        }
    }

    @Test
    fun `usecase throw error when repo throw error`() {
        Mockito.`when`(pokemonSpeciesRepo.getData(anyMap())).thenReturn(Single.error(NullPointerException("Error")))
        fetchSpeciesUseCase = FetchSpeciesUseCase(pokemonSpeciesRepo)
        val testObserver = fetchSpeciesUseCase.execute(0).test().assertErrorMessage("Error")
        assert(testObserver.errorCount() == 1)
    }
}