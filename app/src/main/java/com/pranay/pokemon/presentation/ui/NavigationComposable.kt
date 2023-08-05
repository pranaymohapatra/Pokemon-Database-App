package com.pranay.pokemon.presentation.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pranay.pokemon.presentation.ui.Constants.ARG_BG_COLOR
import com.pranay.pokemon.presentation.ui.Constants.ARG_SPECIES_ID
import com.pranay.pokemon.presentation.ui.Constants.ARG_SPECIES_NAME
import com.pranay.pokemon.presentation.ui.Constants.ARG_SPECIES_ORDER
import com.pranay.pokemon.presentation.ui.screens.PokemonDetailScreen
import com.pranay.pokemon.presentation.ui.screens.SpeciesListScreen

@Composable
fun NavigationHelper() {
    Log.d("PranayNavigationHelper", "Composition")
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.PokeListScreen.route) {
        composable(Screen.PokeListScreen.route) {
            Log.d("NavigationHelper", "SpeciesListScreen")
            SpeciesListScreen(navController)
        }
        composable(
            route = Screen.PokeDetailScreen.route + Screen.PokeDetailScreen.argPath,
            arguments = listOf(navArgument(ARG_SPECIES_NAME) {
                type = NavType.StringType
            }, navArgument(ARG_SPECIES_ID) {
                type = NavType.IntType
            }, navArgument(ARG_SPECIES_ORDER) {
                type = NavType.IntType
            }, navArgument(ARG_BG_COLOR) {
                type = NavType.IntType
            }
            )) { navEntry ->
            navEntry.arguments?.let { args ->
                with(args) {
                    Log.d("NavigationHelper", "PokemonDetailScreen")
                    PokemonDetailScreen(
                        navController = navController,
                        name = getString(ARG_SPECIES_NAME)!!,
                        id = getInt(ARG_SPECIES_ID),
                        order = getInt(ARG_SPECIES_ORDER),
                        bgColor = getInt(ARG_BG_COLOR)
                    )
                }
            }

        }
    }
}