package com.pranay.pokemon.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava2.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.pranay.pokemon.R
import com.pranay.pokemon.domain.models.CaptureDifficulty
import com.pranay.pokemon.domain.models.PokemonSpeciesDetail
import com.pranay.pokemon.presentation.ui.Constants
import com.pranay.pokemon.presentation.viewmodel.PokemonDetailViewModel
import com.pranay.pokemon.presentation.viewstate.DetailsUI
import java.util.Locale

@Composable
fun PokemonDetailScreen(
    pokemonDetailViewModel: PokemonDetailViewModel = hiltViewModel(),
    navController: NavController,
    name: String,
    id: Int,
    order: Int,
    bgColor: Int
) {
    val detailState by pokemonDetailViewModel.detailsState.subscribeAsState(
        initial = DetailsUI(item = null, isLoading = true)
    )
    ScaffoldSection(
        name = name,
        id = id,
        order = order,
        bgColor = bgColor,
        isLoading = detailState.isLoading,
        errorMessage = detailState.errorMessage,
        item = detailState.item,
        navController = navController,
        pokemonDetailViewModel = pokemonDetailViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldSection(
    name: String,
    id: Int,
    order: Int,
    bgColor: Int,
    isLoading: Boolean,
    errorMessage: String?,
    item: PokemonSpeciesDetail?,
    navController: NavController,
    pokemonDetailViewModel: PokemonDetailViewModel
) {
    val retryAction: () -> Unit = remember {
        { pokemonDetailViewModel.getSpeciesDetailState(id, order) }
    }
    remember {
        retryAction.invoke()
        -1
    }
    val remberedBgColor = remember {
        Color(bgColor)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopSection(
                navController = navController,
                modifier = Modifier.fillMaxWidth(),
                bgColor = remberedBgColor
            )
        },
        containerColor = remberedBgColor
    ) {
        if (isLoading || errorMessage != null)
            ShowLoaderOrErrorRetry(
                loading = isLoading,
                errorMessage = errorMessage,
                retryAction = retryAction,
                modifier = Modifier.fillMaxSize()
            ) else
            PokemonDetailSection(
                id = id,
                name = name,
                imgUrl = item!!.imageURL,
                speciesDetail = item,
                modifier = Modifier.padding(it)
            )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PokemonDetailSection(
    id: Int,
    name: String,
    imgUrl: String,
    speciesDetail: PokemonSpeciesDetail,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val captureTextColor = remember {
        if (speciesDetail.captureTextColor == CaptureDifficulty.GREEN)
            Color.Green
        else
            Color.Red
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        GlideImage(
            model = imgUrl,
            contentDescription = name,
            modifier = Modifier.size(Constants.IMAGE_SIZE.dp),
            loading = placeholder { CircularProgressIndicator(modifier = Modifier.size(Constants.IMAGE_SIZE.dp)) }
        ) {
            it.thumbnail(it.clone().sizeMultiplier(0.5f))
        }
        Text(
            text = "#$id ${name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }}",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp + (Constants.IMAGE_SIZE / 2f).dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            ) {
                Text(
                    text = speciesDetail.description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
                speciesDetail.evolvedSpecies?.let { evolvedSpecies ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Text(
                            text = stringResource(R.string.evolves_to),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            color = captureTextColor
                        )
                        PokemonGlideImageItem(
                            modifier = Modifier
                                .shadow(4.dp, RoundedCornerShape(8.dp)),
                            imageURL = evolvedSpecies.imageUrl,
                            name = evolvedSpecies.name
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = buildString {
                            append(stringResource(R.string.capture_rate_difference))
                            append(speciesDetail.capture_rate - evolvedSpecies.capture_rate)
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = captureTextColor,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                    )
                } ?: Text(
                    text = stringResource(R.string.final_evolution_text),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun TopSection(
    navController: NavController,
    modifier: Modifier = Modifier,
    bgColor: Color = Color.Transparent
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.scrim,
                        bgColor,
                        Color.Transparent
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}