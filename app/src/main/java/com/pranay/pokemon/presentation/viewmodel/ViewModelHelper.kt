package com.pranay.pokemon.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.palette.graphics.Palette
import io.reactivex.Single
import javax.inject.Inject

class ViewModelHelper @Inject constructor() {
    fun getItemBackgroundColor(drawable: Drawable): Single<Int> {
        return Single.create { emitter ->
            (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true).let {
                Palette.from(it).generate { palette ->
                    palette?.dominantSwatch?.rgb?.let { colorInt ->
                        emitter.onSuccess(colorInt)
                    }
                }
            }
        }
    }
}