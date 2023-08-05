package com.pranay.pokemon.presentation.ui.screens

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava2.subscribeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.integration.compose.rememberGlidePreloadingData
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.pranay.pokemon.R
import com.pranay.pokemon.domain.models.PokemonSpecies
import com.pranay.pokemon.presentation.ui.Constants.IMAGE_SIZE
import com.pranay.pokemon.presentation.ui.Screen
import com.pranay.pokemon.presentation.viewmodel.PokemonMainViewModel
import com.pranay.pokemon.presentation.viewmodel.UIEvent
import com.pranay.pokemon.presentation.viewstate.LoadListScreenUI
import java.util.Locale

@Composable
fun SpeciesListScreen(
    navController: NavController,
    pokemonMainViewModel: PokemonMainViewModel = hiltViewModel()
) {
    val loadedResult by pokemonMainViewModel.getViewState().subscribeAsState(
        initial = LoadListScreenUI(loading = true, isFirstPage = true)
    )

    val retryAction: () -> Unit = remember {
        { pokemonMainViewModel.sendUIEvent() }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val loadingSpan: (LazyGridItemSpanScope) -> GridItemSpan = remember {
            {
                GridItemSpan(2)
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.welcome_text),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (loadedResult.isFirstPage && (loadedResult.loading || loadedResult.errorMessage != null))
                ShowLoaderOrErrorRetry(
                    loading = loadedResult.loading,
                    errorMessage = loadedResult.errorMessage,
                    retryAction = retryAction
                )
            else
                SpeciesLazyGrid(
                    navController = navController,
                    speciesList = loadedResult.result,
                    isLoading = loadedResult.loading,
                    error = loadedResult.errorMessage,
                    loadingSpan = loadingSpan,
                    viewModel = pokemonMainViewModel
                )

        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PokemonSpeciesItem(
    navController: NavController,
    viewModel: PokemonMainViewModel,
    item: PokemonSpecies,
    modifier: Modifier,
    glideRequestBuilder: RequestBuilder<Drawable>
) {
    val defBgColor = MaterialTheme.colorScheme.surface
    var bgColor by remember {
        mutableStateOf(
            if (item.bgColor != -1)
                Color(item.bgColor)
            else
                defBgColor
        )
    }
    val glideListener = remember<RequestListener<Drawable>> {
        object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, m: Any?,
                t: Target<Drawable>?, isF: Boolean
            ): Boolean {
                e?.printStackTrace()
                return false
                //handle error here using vm
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                t: Target<Drawable>?,
                ds: DataSource?,
                isF: Boolean
            ): Boolean {
                if (item.bgColor == -1)
                    resource?.let {
                        viewModel.getItemBackgroundColor(item.id, it) { colorInt ->
                            bgColor = Color(colorInt)
                        }
                    }
                return false
            }
        }
    }
    PokemonGlideImageItem(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .aspectRatio(1.0f, true)
            .background(brush = Brush.verticalGradient(listOf(bgColor, defBgColor)))
            .clickable {
                navController.navigate(
                    Screen.PokeDetailScreen.setArgs(
                        item.name, item.id.toString(),
                        item.order.toString(), bgColor.toArgb().toString()
                    )
                )
            },
        imageURL = item.imageURL,
        name = item.name,
        glideListener = glideListener,
        thumbnailRequestBuilder = glideRequestBuilder
    )
}

@Composable
fun SpeciesLazyGrid(
    navController: NavController,
    speciesList: List<PokemonSpecies>,
    isLoading: Boolean,
    error: String?,
    loadingSpan: (LazyGridItemSpanScope) -> GridItemSpan,
    viewModel: PokemonMainViewModel
) {
    val remeberedList by remember {
        mutableStateOf(speciesList)
    }
    val retryAction: () -> Unit = remember {
        { viewModel.sendUIEvent(UIEvent.EndOfPage) }
    }
    val requestBuilderTransform =
        { item: PokemonSpecies, requestBuilder: RequestBuilder<Drawable> ->
            requestBuilder.load(item.imageURL)
        }

    val preloadingData =
        rememberGlidePreloadingData(
            remeberedList,
            Size(50f, 50f),
            requestBuilderTransform = requestBuilderTransform,
        )
    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(4.dp)) {
        items(preloadingData.size, key = { remeberedList[it].name }) { index ->
            if (index >= preloadingData.size - 1 && !isLoading) {
                LaunchedEffect(key1 = true) {
                    retryAction.invoke()
                }
            }
            PokemonSpeciesItem(
                navController = navController,
                viewModel = viewModel,
                item = remeberedList[index],
                modifier = Modifier.padding(4.dp),
                glideRequestBuilder = preloadingData[index].second
            )
        }
        if (isLoading || error != null)
            item(span = loadingSpan) {
                ShowLoaderOrErrorRetry(
                    loading = isLoading,
                    errorMessage = error,
                    retryAction = retryAction
                )
            }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PokemonGlideImageItem(
    modifier: Modifier, imageURL: String, name: String,
    glideListener: RequestListener<Drawable>? = null,
    thumbnailRequestBuilder: RequestBuilder<Drawable>? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        GlideImage(
            model = imageURL,
            contentDescription = name,
            modifier = Modifier.size(IMAGE_SIZE.dp),
            loading = placeholder { CircularProgressIndicator(modifier = Modifier.size(IMAGE_SIZE.dp)) }
        ) {
            it.addListener(glideListener).thumbnail(
                thumbnailRequestBuilder ?: it.clone().sizeMultiplier(0.5f)
            )
        }
        Text(
            text = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}