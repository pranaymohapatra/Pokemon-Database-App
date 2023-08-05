package com.pranay.pokemon.presentation.viewmodel

import android.graphics.drawable.Drawable
import com.pranay.pokemon.domain.FetchSpeciesUseCase
import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.utils.BaseSchedulerProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class PokemonMainViewModelTest {

    @Mock
    private lateinit var baseSchedulerProvider: BaseSchedulerProvider

    @Mock
    private lateinit var fetchSpeciesUseCase: FetchSpeciesUseCase

    @Mock
    private lateinit var viewModelHelper: ViewModelHelper

    @Mock
    private lateinit var mockDrawable: Drawable

    private lateinit var pokemonMainViewModel: PokemonMainViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(baseSchedulerProvider.io()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(baseSchedulerProvider.ui()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(fetchSpeciesUseCase.execute(anyInt())).thenReturn(Single.never())
        Mockito.`when`(viewModelHelper.getItemBackgroundColor(mockDrawable))
            .thenReturn(Single.just(-1234))
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `on init should emit loading state until result fetched`() {
        Mockito.`when`(fetchSpeciesUseCase.execute(anyInt())).thenReturn(Single.never())
        pokemonMainViewModel =
            PokemonMainViewModel(fetchSpeciesUseCase, baseSchedulerProvider, viewModelHelper)
        //There will be one value which is returned by startWith()
        pokemonMainViewModel.getViewState().test()
            .assertValueCount(1)
            .assertValueAt(0) {
                it.loading && it.isFirstPage
            }

    }

    @Test
    fun `on error for ui event should emit loading state followed by error state`() {
        pokemonMainViewModel =
            PokemonMainViewModel(fetchSpeciesUseCase, baseSchedulerProvider, viewModelHelper)
        val testObserver = pokemonMainViewModel.getViewState().test()
        Mockito.`when`(fetchSpeciesUseCase.execute(anyInt()))
            .thenReturn(Single.error(IllegalArgumentException(ERROR_MSG)))
        pokemonMainViewModel.sendUIEvent()
        //there will be 3 values 1.startWith value, 2. loading value, 3. error value.
        //we will check the 3rd value as that will be the latest value which has error state
        testObserver.assertValueCount(3).assertValueAt(2) {
            !it.loading && ERROR_MSG == it.errorMessage
        }
    }

    @Test
    fun `on success for ui event should emit loading state followed by success state`() {
        pokemonMainViewModel =
            PokemonMainViewModel(fetchSpeciesUseCase, baseSchedulerProvider, viewModelHelper)
        val testObserver = pokemonMainViewModel.getViewState().test()
        Mockito.`when`(fetchSpeciesUseCase.execute(anyInt()))
            .thenReturn(Single.just(dummyList))
        pokemonMainViewModel.sendUIEvent()
        //there will be 3 values 1.startWith value, 2. loading value, 3. success value.
        //we will check the 3rd value as that will be the latest value which has success state
        testObserver.assertValueCount(3).assertValueAt(2) {
            !it.loading && it.result.size == 3
        }
    }

    @Test
    fun `end reached true when success for ui event and items less than page size`() {
        pokemonMainViewModel =
            PokemonMainViewModel(fetchSpeciesUseCase, baseSchedulerProvider, viewModelHelper)
        val testObserver = pokemonMainViewModel.getViewState().test()
        Mockito.`when`(fetchSpeciesUseCase.execute(anyInt()))
            .thenReturn(Single.just(dummyList))
        pokemonMainViewModel.sendUIEvent(UIEvent.EndOfPage)
        testObserver.assertValueCount(3).assertValueAt(2) {
            !it.loading && it.result.size == 3 && it.endReached
        }
        pokemonMainViewModel.sendUIEvent(UIEvent.EndOfPage)
        //sending further events after end reached should not emit any more states
        testObserver.assertValueCount(3)
    }

    @Test
    fun `should set correct color value when ViewmodelHelper returns color value`() {
        val pokemonMainViewModel =
            PokemonMainViewModel(fetchSpeciesUseCase, baseSchedulerProvider, viewModelHelper)
        var testInt = 0
        pokemonMainViewModel.getItemBackgroundColor(anyInt(), mockDrawable) {
            testInt = it
        }
        assertEquals(-1234, testInt)
    }

    @Test
    fun `for successive request with first load, use case must not be called`() {
        pokemonMainViewModel =
            PokemonMainViewModel(fetchSpeciesUseCase, baseSchedulerProvider, viewModelHelper)
        val dummy = mutableListOf<PokemonSpecies>()
        for(i in 0..20){
            dummy.add(PokemonSpecies(i+1,"Pokemon #$i", i+1))
        }
        Mockito.`when`(fetchSpeciesUseCase.execute(anyInt()))
            .thenReturn(Single.just(dummy))
        //check end not reached
        pokemonMainViewModel.getViewState().test().assertValueAt(0){
            !it.endReached
        }
        pokemonMainViewModel.sendUIEvent()
        //we check for 2 invocations because one is done in vm init.
        Mockito.verify(fetchSpeciesUseCase, times(2)).execute(anyInt())
        pokemonMainViewModel.sendUIEvent()
        //count should be the same after 2nd event
        Mockito.verify(fetchSpeciesUseCase, times(2)).execute(anyInt())

    }

    companion object {
        val ERROR_MSG = "Test Failed"
        val dummyList = listOf(
            PokemonSpecies(1, "pikachu", 1),
            PokemonSpecies(2, "raichu", 2),
            PokemonSpecies(3, "squirtle", 3)
        )
    }
}