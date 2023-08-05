package com.pranay.pokemon.presentation.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.pranay.pokemon.domain.FetchSpeciesUseCase
import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.presentation.viewstate.LoadListScreenUI
import com.pranay.pokemon.utils.BaseSchedulerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class PokemonMainViewModel @Inject constructor(
    private val fetchSpeciesUseCase: FetchSpeciesUseCase,
    private val schedulers: BaseSchedulerProvider,
    private val viewModelHelper: ViewModelHelper
) : ViewModel() {
    private val disposable = CompositeDisposable()
    private var currentPage = 0
    private var endReached = false
    private val _uiEvents = PublishSubject.create<UIEvent>()
    private var currentItems = mutableListOf<PokemonSpecies>()
    private var scrollPosition = 0
    private val _speciesListState = BehaviorSubject.create<LoadListScreenUI>()
    private val speciesStateObservable: Observable<LoadListScreenUI> =
        _uiEvents.observeOn(schedulers.io()).flatMap { uiEvent ->
            handleUIEvent(uiEvent)
        }.onErrorResumeNext(::handleError)

    init {
        disposable.add(
            speciesStateObservable.subscribe(_speciesListState::onNext, _speciesListState::onError)
        )
        sendUIEvent()
    }

    private fun handleUIEvent(uiEvent: UIEvent): Observable<LoadListScreenUI> {
        return when (uiEvent) {
            is UIEvent.FirstLoad ->
                if (currentItems.isNotEmpty())
                    Observable.just(
                        LoadListScreenUI(
                            currentItems,
                            loading = false,
                            isFirstPage = true
                        )
                    )
                else fetchSpeciesList(currentPage, true)

            is UIEvent.EndOfPage -> { fetchSpeciesList(currentPage) }
        }
    }

    fun getViewState(): Observable<LoadListScreenUI> {
        return _speciesListState.onErrorResumeNext(::handleError).hide().observeOn(schedulers.ui())
    }

    private fun handleError(error: Throwable): Observable<LoadListScreenUI> {
        error.printStackTrace()
        return Observable.just(
            LoadListScreenUI(
                currentItems,
                false,
                currentItems.isEmpty(),
                error.message
            )
        )
    }

    fun getItemBackgroundColor(id: Int, drawable: Drawable, action: (Int) -> Unit) {
        disposable.add(viewModelHelper.getItemBackgroundColor(drawable).subscribe { colorInt ->
            currentItems.find {
                it.id == id
            }?.bgColor = colorInt
            action.invoke(colorInt)
        }
        )
    }

    fun sendUIEvent(uiEvent: UIEvent = UIEvent.FirstLoad) {
        if (!endReached)
            _uiEvents.onNext(uiEvent)
    }

    private fun fetchSpeciesList(
        page: Int,
        isFirstPage: Boolean = false
    ): Observable<LoadListScreenUI> {
        return fetchSpeciesUseCase.execute(page).toObservable().map {
            ++currentPage //Fetch Success, increment current page
            endReached = it.size < FetchSpeciesUseCase.PAGE_SIZE
            LoadListScreenUI(
                currentItems.apply { addAll(it) },
                loading = false,
                endReached = true
            )
        }.startWith(
            LoadListScreenUI(
                result = currentItems,
                loading = true,
                isFirstPage = isFirstPage
            )
        )
            .onErrorResumeNext(::handleError)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}