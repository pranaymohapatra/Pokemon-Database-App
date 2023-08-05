package com.pranay.pokemon.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat.getSystemService
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

object NetworkChecker {
    val networkStatus: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun Context.monitorNetworkStatus() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkStatus.onNext(true)
            }

            // Network capabilities have changed for the network
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                with(networkCapabilities) {
                    if (hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        && hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    )
                        networkStatus.onNext(true)
                    else
                        networkStatus.onNext(false)
                }
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)
                networkStatus.onNext(false)
            }
        }

        val cm = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        cm.requestNetwork(networkRequest, networkCallback)
    }

}