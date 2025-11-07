package com.desynatuser.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor() : ViewModel() {
    private val _location = MutableLiveData<Pair<Double, Double>>()  // lat, lng
    val location: LiveData<Pair<Double, Double>> = _location

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    fun setLocation(lat: Double, lng: Double) {
        _location.value = lat to lng
    }
}
