package com.desynatuser.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatuser.data.repo.MainRepository
import com.desynatuser.ui.auth.response.FileUploadResponse
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.ui.auth.response.RegisterAccountResponse
import com.desynatuser.ui.auth.response.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val progress: LiveData<Boolean> get() = repository.progress
    val error: LiveData<String> get() = repository.error
    fun reset() {
        repository.error.value = null
        repository.progress.value = false
    }


    val registerResult: LiveData<RegisterAccountResponse>
        get() = repository.registerLiveData

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            repository.register(request)
        }
    }


    val uploadImageResult: LiveData<FileUploadResponse>
        get() = repository.responseUploadImage

    fun uploadImage(
        image: File?=null
    ) {
        viewModelScope.launch {
            repository.uploadImage(image)
        }
    }

 val loginResult: LiveData<LoginResponse>
        get() = repository.loginLiveData

    fun login(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.login(map)
        }
    }
 val loginEmailMobileResult: LiveData<LoginResponse>
        get() = repository.loginEmailMobileLiveData

    fun loginViaEmailOrMobile(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.loginViaEmailOrMobile(map)
        }
    }

 val otpVerifyResult: LiveData<LoginResponse>
        get() = repository.otpVerifyLiveData

    fun otpVerification(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.otpVerification(map)
        }
    }


    val resendOtpResult: LiveData<LoginResponse>
        get() = repository.resendOtpLiveData

    fun resendOtp(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.resendOtp(map)
        }
    }


    val forgotPasswordResult: LiveData<LoginResponse>
        get() = repository.forgotPasswordLiveData

    fun forgotPassword(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.forgotPassword(map)
        }
    }



    val createPasswordResult: LiveData<LoginResponse>
        get() = repository.createPasswordLiveData

    fun createPassword(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.createPassword(map)
        }
    }

    val countryResult: LiveData<GetCountryResponse>
        get() = repository.countryLiveData

    fun getCountry() {
        viewModelScope.launch {
            repository.getCountry()
        }
    }

}
