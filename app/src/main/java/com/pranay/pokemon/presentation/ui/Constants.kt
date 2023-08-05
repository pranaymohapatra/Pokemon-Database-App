package com.pranay.pokemon.presentation.ui

import com.pranay.pokemon.presentation.ui.Constants.ARG_BG_COLOR
import com.pranay.pokemon.presentation.ui.Constants.ARG_SPECIES_ID
import com.pranay.pokemon.presentation.ui.Constants.ARG_SPECIES_NAME
import com.pranay.pokemon.presentation.ui.Constants.ARG_SPECIES_ORDER

object Constants {
    const val ARG_SPECIES_NAME = "name"
    const val ARG_SPECIES_ID = "id"
    const val ARG_SPECIES_ORDER = "order"
    const val ARG_BG_COLOR = "bgcolor"
    const val IMAGE_SIZE = 100f
}

sealed class Screen(val route: String, val argPath: String = "") {
    object PokeListScreen : Screen("POKE_LIST_SCREEN")
    object PokeDetailScreen : Screen(
        "POKE_DETAIL_SCREEN",
        "/{$ARG_SPECIES_NAME}/{$ARG_SPECIES_ID}/{$ARG_SPECIES_ORDER}/{$ARG_BG_COLOR}"
    )

    fun setArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}