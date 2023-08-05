package com.pranay.pokemon.data

import com.pranay.pokemon.FileHelper
import com.pranay.pokemon.data.db.PokeDetailEntity
import com.pranay.pokemon.data.db.PokeSpeciesEntity
import com.pranay.pokemon.data.db.PokemonDAO
import com.pranay.pokemon.data.dto.GQLRequestBody
import com.pranay.pokemon.data.dto.GQLResponse
import com.pranay.pokemon.data.dto.SpeciesGQLData
import com.pranay.pokemon.data.remote.PokemonAPI
import com.pranay.pokemon.utils.BaseSchedulerProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class PokemonSpeciesRepositoryTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var pokemonAPI: PokemonAPI
    private lateinit var speciesRepository: PokemonSpeciesRepository
    private lateinit var okHttpClient: OkHttpClient
    private val fileHelper = FileHelper()

    @Mock
    private lateinit var pokemonDAO: PokemonDAO

    @Mock
    private lateinit var baseSchedulerProvider: BaseSchedulerProvider

    @Mock
    private lateinit var mockedAPI: PokemonAPI

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(baseSchedulerProvider.io()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(baseSchedulerProvider.ui()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(mockedAPI.getPokemonSpecies(any(GQLRequestBody::class.java))).thenReturn(
            Single.just(GQLResponse(SpeciesGQLData(emptyList())))
        )
        mockWebServer = MockWebServer()
        okHttpClient = OkHttpClient
            .Builder().build()
        pokemonAPI = Retrofit.Builder().baseUrl(mockWebServer.url("/v1beta/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build().create()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `must call db first and if list empty from db then call api`() {
        speciesRepository = PokemonSpeciesRepository(mockedAPI, baseSchedulerProvider, pokemonDAO)
        Mockito.`when`(pokemonDAO.getPokemonList(anyInt(), anyInt()))
            .thenReturn(Single.just(emptyList()))
        Mockito.`when`(pokemonDAO.insertPokemonList(anyList()))
            .thenReturn(Single.just(listOf()))
        speciesRepository.getData(mapOf("limit" to 20, "offset" to 0)).test().assertValueAt(0) {
            it.isEmpty()
        }
        Mockito.verify(pokemonDAO).getPokemonList(anyInt(), anyInt())
        Mockito.verify(mockedAPI).getPokemonSpecies(any(GQLRequestBody::class.java))
    }

    @Test
    fun `if db has data then should not hit api`() {
        val dummyEntities = mutableListOf<PokeSpeciesEntity>()
        for (i in 0..20) {
            dummyEntities.add(PokeSpeciesEntity(i + 1, "Pokemon #$i", i + 1))
        }
        speciesRepository = PokemonSpeciesRepository(mockedAPI, baseSchedulerProvider, pokemonDAO)
        Mockito.`when`(pokemonDAO.getPokemonList(anyInt(), anyInt()))
            .thenReturn(Single.just(dummyEntities))
        Mockito.`when`(pokemonDAO.insertPokemonList(anyList()))
            .thenReturn(Single.just(listOf()))
        speciesRepository.getData(mapOf("limit" to 20, "offset" to 0)).test().assertValueAt(0) {
            it.size >= 20
        }
        Mockito.verify(pokemonDAO).getPokemonList(anyInt(), anyInt())
        Mockito.verify(mockedAPI, never()).getPokemonSpecies(any(GQLRequestBody::class.java))
    }

    @Test
    fun `if db returns empty list then should call api and emit success if api success and also insert items into db`() {
        Mockito.`when`(pokemonDAO.getPokemonList(anyInt(), anyInt()))
            .thenReturn(Single.just(emptyList()))
        Mockito.`when`(pokemonDAO.insertPokemonList(anyList()))
            .thenReturn(Single.just(listOf()))
        val mockResponse = MockResponse()
        mockResponse.setBody(fileHelper.readFile("/specieslistresponse.json"))
        mockWebServer.enqueue(mockResponse)
        speciesRepository = PokemonSpeciesRepository(pokemonAPI, baseSchedulerProvider, pokemonDAO)
        val testObserver = speciesRepository.getData(mapOf("limit" to 20, "offset" to 0)).test()
        testObserver.assertValueCount(1).assertValueAt(0) {
            it.size >= 20
        }
        Mockito.verify(pokemonDAO).insertPokemonList(anyList())
    }

    @Test
    fun `should emit IllegalArgumentException if params doesnt contain all keys`() {
        speciesRepository = PokemonSpeciesRepository(pokemonAPI, baseSchedulerProvider, pokemonDAO)
        val testObserver = speciesRepository.getData(mapOf("order" to 1)).test()
        testObserver.assertError {
            it is IllegalArgumentException && it.message != null
        }
    }
    @Test
    fun sampletest(){
        var m = 1
        val x = m.apply { this+1  }.apply { this+1 }
        println(x)
        Assert.assertEquals(1,x)
        Assert.assertEquals(3,m)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}