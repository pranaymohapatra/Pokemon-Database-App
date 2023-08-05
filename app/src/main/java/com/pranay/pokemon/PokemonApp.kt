package com.pranay.pokemon

import android.app.Application
import com.pranay.pokemon.utils.NetworkChecker.monitorNetworkStatus
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PokemonApp : Application() {
    override fun onCreate() {
        super.onCreate()
        monitorNetworkStatus()
    }
}