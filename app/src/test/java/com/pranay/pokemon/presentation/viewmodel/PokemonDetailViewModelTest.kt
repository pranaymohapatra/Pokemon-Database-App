package com.pranay.pokemon.presentation.viewmodel

import com.pranay.pokemon.domain.FetchSpeciesDetailsUseCase
import com.pranay.pokemon.domain.models.EvolvedSpecies
import com.pranay.pokemon.domain.models.PokemonSpeciesDetail
import com.pranay.pokemon.utils.BaseSchedulerProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class PokemonDetailViewModelTest {
    @Mock
    private lateinit var baseSchedulerProvider: BaseSchedulerProvider

    @Mock
    lateinit var detailsUseCase: FetchSpeciesDetailsUseCase

    private var params: FetchSpeciesDetailsUseCase.Params = FetchSpeciesDetailsUseCase.Params(1, 1)

    private lateinit var pokemonDetailViewModel: PokemonDetailViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(baseSchedulerProvider.io()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(baseSchedulerProvider.ui()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(detailsUseCase.execute(params)).thenReturn(Single.never())
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `show loading state followed by error state for every fetch data call when error thrown`() {
        Mockito.`when`(detailsUseCase.execute(params))
            .thenReturn(Single.error(IllegalArgumentException(PokemonMainViewModelTest.ERROR_MSG)))
        pokemonDetailViewModel = PokemonDetailViewModel(detailsUseCase, baseSchedulerProvider)
        val testObserver = pokemonDetailViewModel.detailsState.test()
        pokemonDetailViewModel.getSpeciesDetailState(params.id, params.order)
        testObserver.assertValueCount(2).assertValueAt(0) {
            it.isLoading
        }.assertValueAt(1) {
            !it.isLoading && PokemonMainViewModelTest.ERROR_MSG == it.errorMessage
        }
    }

    @Test
    fun `show loading state followed by success state for every fetch data call when result`() {
        Mockito.`when`(detailsUseCase.execute(params)).thenReturn(Single.just(dummyResponse))
        pokemonDetailViewModel = PokemonDetailViewModel(detailsUseCase, baseSchedulerProvider)
        val testObserver = pokemonDetailViewModel.detailsState.test()
        pokemonDetailViewModel.getSpeciesDetailState(params.id, params.order)
        testObserver.assertValueCount(2).assertValueAt(0) {
            it.isLoading
        }.assertValueAt(1) {
            !it.isLoading && dummyResponse == it.item
        }
    }

    companion object {
        val dummyResponse = PokemonSpeciesDetail(12, "Strong", EvolvedSpecies(32, 2, "Stronger"))
    }
}