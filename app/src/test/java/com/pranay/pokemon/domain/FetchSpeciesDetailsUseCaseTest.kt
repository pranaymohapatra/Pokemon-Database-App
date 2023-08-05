package com.pranay.pokemon.domain

import com.pranay.pokemon.domain.models.EvolvedSpecies
import com.pranay.pokemon.domain.models.PokemonSpeciesDetail
import com.pranay.pokemon.domain.repository.PokemonSpeciesDetailsRepo
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class FetchSpeciesDetailsUseCaseTest {
    @Mock
    private lateinit var pokemonRepo: PokemonSpeciesDetailsRepo

    private lateinit var detailsUseCase: FetchSpeciesDetailsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `when repo returns success, should also return success and species img url is set`() {
        Mockito.`when`(pokemonRepo.getData(mapOf("id" to 1, "order" to 1)))
            .thenReturn(Single.just(dummyResponseWithoutEvolved))
        detailsUseCase = FetchSpeciesDetailsUseCase(pokemonRepo)
        detailsUseCase.execute(FetchSpeciesDetailsUseCase.Params(1, 1)).test().assertValueCount(1)
            .assertValueAt(0) {
                it.imageURL == String.format(FetchSpeciesDetailsUseCase.imageURLHolder, 1)
            }
    }

    @Test
    fun `when repo returns success with evolved species, should also return success and both img url is set`() {
        Mockito.`when`(pokemonRepo.getData(mapOf("id" to 1, "order" to 1)))
            .thenReturn(Single.just(dummyResponse))
        detailsUseCase = FetchSpeciesDetailsUseCase(pokemonRepo)
        detailsUseCase.execute(FetchSpeciesDetailsUseCase.Params(1, 1)).test().assertValueCount(1)
            .assertValueAt(0) {
                it.evolvedSpecies != null && String.format(
                    FetchSpeciesDetailsUseCase.imageURLHolder,
                    it.evolvedSpecies!!.id
                ) == it.evolvedSpecies!!.imageUrl
            }
    }

    @Test
    fun `usecase throw error when repo throw error`() {
        Mockito.`when`(pokemonRepo.getData(anyMap()))
            .thenReturn(Single.error(NullPointerException("Error")))
        detailsUseCase = FetchSpeciesDetailsUseCase(pokemonRepo)
        val testObserver = detailsUseCase.execute(FetchSpeciesDetailsUseCase.Params(1, 1)).test()
            .assertErrorMessage("Error")
        assert(testObserver.errorCount() == 1)
    }

    companion object {
        val dummyResponse = PokemonSpeciesDetail(12, "Strong", EvolvedSpecies(32, 2, "Stronger"))
        val dummyResponseWithoutEvolved = PokemonSpeciesDetail(12, "Strongest", null)
    }
}