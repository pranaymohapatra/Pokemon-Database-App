package com.pranay.pokemon.domain

import io.reactivex.Single

abstract class SingleUseCase<T, in Params>() {

    protected abstract fun executeInternal(requestParams: Params): Single<T>

    fun execute(requestParams: Params): Single<T> = executeInternal(requestParams)
}