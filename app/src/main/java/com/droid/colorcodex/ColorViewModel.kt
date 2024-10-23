package com.droid.colorcodex


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ColorViewModel(application: Application) : ViewModel() {
    private val colorRepository: ColorRepository = ColorRepository(application)

    private val _colorList = MutableStateFlow<List<ColorData>>(emptyList())
    val colorList: StateFlow<List<ColorData>> = _colorList

    init {
        viewModelScope.launch {
            colorRepository.getAllColors().collect { colors ->
                _colorList.value = colors
            }
        }
    }

    fun addColor() {
        val newColor = ColorData(
            colorCode = generateRandomColor(),
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            colorRepository.insertColor(newColor) // Method to insert color
        }
    }

    private fun generateRandomColor(): String {
        val randomColorInt = (0x000000..0xFFFFFF).random()
        return "#${randomColorInt.toString(16).padStart(6, '0')}"
    }

    fun syncColors() {
        viewModelScope.launch {
            colorRepository.syncColorsToCloud() // Method to implement sync logic
        }
    }
}
