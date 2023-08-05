package com.pranay.pokemon.data

import com.pranay.pokemon.FileHelper
import com.pranay.pokemon.data.db.PokeDetailEntity
import com.pranay.pokemon.data.db.PokemonDAO
import com.pranay.pokemon.data.dto.GQLRequestBody
import com.pranay.pokemon.data.dto.GQLResponse
import com.pranay.pokemon.data.dto.SpeciesDetailDTO
import com.pranay.pokemon.data.dto.SpeciesDetailsGQLData
import com.pranay.pokemon.data.remote.PokemonAPI
import com.pranay.pokemon.utils.BaseSchedulerProvider
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class PokemonSpeciesDetailsRepositoryTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var pokemonAPI: PokemonAPI
    private lateinit var detailsRepository: PokemonSpeciesDetailsRepository
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
        Mockito.`when`(mockedAPI.getPokemonSpeciesDetails(any(GQLRequestBody::class.java)))
            .thenReturn(
                Single.just(
                    GQLResponse(
                        SpeciesDetailsGQLData(
                            SpeciesDetailDTO(
                                1, listOf(),
                                mapOf()
                            )
                        )
                    )
                )
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
    fun `must call db first and if error then call api`() {
        detailsRepository =
            PokemonSpeciesDetailsRepository(mockedAPI, baseSchedulerProvider, pokemonDAO)
        Mockito.`when`(pokemonDAO.getPokemonDetails(anyInt()))
            .thenReturn(Single.error(NullPointerException("Item not found")))
        detailsRepository.getData(mapOf("id" to 1, "order" to 1)).test().assertError {
            it.message != null
        }
        Mockito.verify(pokemonDAO).getPokemonDetails(anyInt())
        Mockito.verify(mockedAPI).getPokemonSpeciesDetails(any(GQLRequestBody::class.java))
    }

    @Test
    fun `when db returns error, should call api to fetch details and return success`() {
        Mockito.`when`(pokemonDAO.getPokemonDetails(anyInt()))
            .thenReturn(Single.error(NullPointerException("Item not found")))
        Mockito.`when`(pokemonDAO.insertPokemonDetails(any(PokeDetailEntity::class.java)))
            .thenReturn(Single.just(11111))
        val mockResponse = MockResponse()
        mockResponse.setBody(fileHelper.readFile("/speciesdetails.json"))
        mockWebServer.enqueue(mockResponse)
        detailsRepository =
            PokemonSpeciesDetailsRepository(pokemonAPI, baseSchedulerProvider, pokemonDAO)
        val testObserver = detailsRepository.getData(mapOf("id" to 1, "order" to 1)).test()
        testObserver.assertValueCount(1).assertValueAt(0) {
            it.capture_rate == 255
        }
    }

    @Test
    fun `should emit parsing error if mocked response is incorrect`() {
        Mockito.`when`(pokemonDAO.getPokemonDetails(anyInt()))
            .thenReturn(Single.error(NullPointerException("Item not found")))
        Mockito.`when`(pokemonDAO.insertPokemonDetails(any(PokeDetailEntity::class.java)))
            .thenReturn(Single.just(11111))
        val mockResponse = MockResponse()
        mockResponse.setBody("{invalid response}")
        mockWebServer.enqueue(mockResponse)
        detailsRepository =
            PokemonSpeciesDetailsRepository(pokemonAPI, baseSchedulerProvider, pokemonDAO)
        val testObserver = detailsRepository.getData(mapOf("id" to 1, "order" to 1)).test()
        testObserver.assertError {
            (it is JsonEncodingException || it is JsonDataException) && it.message != null
        }
    }

    @Test
    fun `should emit IllegalArgumentException if params doesnt contain all keys`() {
        Mockito.`when`(pokemonDAO.getPokemonDetails(anyInt()))
            .thenReturn(Single.error(NullPointerException("Item not found")))
        Mockito.`when`(pokemonDAO.insertPokemonDetails(any(PokeDetailEntity::class.java)))
            .thenReturn(Single.just(11111))
        val mockResponse = MockResponse()
        mockResponse.setBody("{invalid response}")
        mockWebServer.enqueue(mockResponse)
        detailsRepository =
            PokemonSpeciesDetailsRepository(pokemonAPI, baseSchedulerProvider, pokemonDAO)
        val testObserver = detailsRepository.getData(mapOf("order" to 1)).test()
        testObserver.assertError {
            it is IllegalArgumentException && it.message != null
        }
    }

    @Test
    fun `should not hit api if local has data`() {
        Mockito.`when`(pokemonDAO.getPokemonDetails(anyInt()))
            .thenReturn(
                Single.just(
                    PokeDetailEntity(
                        1, 1, 255, "Strong",
                        2, 2, "Stronger"
                    )
                )
            )
        Mockito.`when`(pokemonDAO.insertPokemonDetails(any(PokeDetailEntity::class.java)))
            .thenReturn(Single.just(11111))
        detailsRepository =
            PokemonSpeciesDetailsRepository(mockedAPI, baseSchedulerProvider, pokemonDAO)
        val testObserver = detailsRepository.getData(mapOf("id" to 1, "order" to 1)).test()
        Mockito.verify(pokemonDAO).getPokemonDetails(anyInt())
        Mockito.verify(mockedAPI, never()).getPokemonSpeciesDetails(any(GQLRequestBody::class.java))
        testObserver.assertValueCount(1).assertValueAt(0) {
            it.capture_rate == 255
        }
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}