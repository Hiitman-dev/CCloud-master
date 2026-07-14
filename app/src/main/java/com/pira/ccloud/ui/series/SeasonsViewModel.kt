package com.pira.ccloud.ui.series

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pira.ccloud.data.model.Season
import com.pira.ccloud.data.repository.ISeasonsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonsViewModel @Inject constructor(
    private val repository: ISeasonsRepository
) : ViewModel() {

    var seasons by mutableStateOf<List<Season>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadSeasons(seriesId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            repository.getSeasons(seriesId).let { result ->
                if (result.isSuccess) {
                    seasons = result.getOrNull() ?: emptyList()
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                }
            }

            isLoading = false
        }
    }
}
