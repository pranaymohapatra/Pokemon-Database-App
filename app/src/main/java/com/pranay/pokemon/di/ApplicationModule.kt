package com.pranay.pokemon.di

import android.content.Context
import com.pranay.pokemon.BuildConfig
import com.pranay.pokemon.data.networkinterceptor.GQLHeaderInterceptor
import com.pranay.pokemon.data.remote.PokemonAPI
import com.pranay.pokemon.utils.BaseSchedulerProvider
import com.pranay.pokemon.utils.NetworkChecker
import com.pranay.pokemon.utils.SchedulerProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.subjects.BehaviorSubject
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun providePokemonApi(httpClient: OkHttpClient): PokemonAPI {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideCacheInstance(@ApplicationContext context: Context): Cache {
        val cacheSize = 1 * 1024 * 1024 // 2 MB Cache
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        gqlHeaderInterceptor: GQLHeaderInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(gqlHeaderInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideGQLInterceptor(): GQLHeaderInterceptor {
        return GQLHeaderInterceptor()
    }

    @Provides
    @Singleton
    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun provideNetworkStatusSubject(): BehaviorSubject<Boolean> = NetworkChecker.networkStatus


    @Provides
    @Singleton
    fun provideBaseScheduler(): BaseSchedulerProvider = SchedulerProvider()
}