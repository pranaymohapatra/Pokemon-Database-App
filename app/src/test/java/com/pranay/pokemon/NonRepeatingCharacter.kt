package com.pranay.pokemon

import android.util.SparseArray
import androidx.core.util.forEach
import org.junit.Assert
import org.junit.Test

class NonRepeatingCharacter {
    @Test
    fun findRepeatingElement() {
        val string = "paytm"
        val sparseArray = SparseArray<Int>() // keep the count
        val charArray = string.toCharArray()

        sparseArray.forEach { key, value ->
            println(value)
        }

//        for(char in charArray){
//            val index = char.toInt()
//            sparseArray[index] +=1
//        }
//
//        var targetIndex = -1
//        sparseArray.forEach { key, value ->
//            if(value == 1)
//                targetIndex = key
//        }
//        println(targetIndex)

    }
}