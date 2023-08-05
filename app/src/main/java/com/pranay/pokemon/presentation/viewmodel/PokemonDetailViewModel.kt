package com.pranay.pokemon.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pranay.pokemon.domain.FetchSpeciesDetailsUseCase
import com.pranay.pokemon.presentation.viewstate.DetailsUI
import com.pranay.pokemon.utils.BaseSchedulerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val useCase: FetchSpeciesDetailsUseCase,
    private val schedulers: BaseSchedulerProvider
) : ViewModel() {
    private val disposable = CompositeDisposable()
    private var currentState = DetailsUI(item = null, isLoading = true)
    private val _detailsState = BehaviorSubject.create<DetailsUI>()
    val detailsState: Observable<DetailsUI> = _detailsState.observeOn(schedulers.ui()).hide()
    private fun getSpeciesDetails(id: Int, order: Int): Observable<DetailsUI> {
        return useCase.execute(FetchSpeciesDetailsUseCase.Params(id, order)).map { item ->
            currentState =
                currentState.copy(id = id, item = item, isLoading = false, errorMessage = null)
            currentState
        }.onErrorResumeNext { error ->
            currentState = currentState.copy(isLoading = false, errorMessage = error.message)
            Single.just(currentState)
        }.toObservable().startWith(currentState.copy(isLoading = true, errorMessage = null))
    }

    fun getSpeciesDetailState(id: Int, order: Int) {
        if (currentState.id != id)
            disposable.add(
                getSpeciesDetails(id, order).subscribe(
                    _detailsState::onNext,
                    _detailsState::onError
                )
            )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}