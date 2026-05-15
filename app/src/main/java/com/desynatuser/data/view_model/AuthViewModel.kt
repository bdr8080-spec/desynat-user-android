package com.desynatuser.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatuser.data.repo.MainRepository
import com.desynatuser.ui.affiliateUser.auth.response.AddAffiliateUserAddressResponse
import com.desynatuser.ui.affiliateUser.auth.response.AddAffiliateUserBankDetail
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddAddressRequest
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddBankDetailRequest
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserLoginResponse
import com.desynatuser.ui.affiliateUser.auth.response.CreateAffiliateUserResponse
import com.desynatuser.ui.affiliateUser.auth.response.CreateAffiliateUserRequest
import com.desynatuser.ui.affiliateUser.home.profile.response.AddAnotherBankResponse
import com.desynatuser.ui.auth.response.FileUploadResponse
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.ui.auth.response.RegisterAccountResponse
import com.desynatuser.ui.auth.response.RegisterRequest
import com.desynatuser.ui.response.CommonResponse
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
        image: File? = null
    ) {
        viewModelScope.launch {
            repository.uploadImage(image)
        }
    }

    val loginResult: LiveData<LoginResponse>
        get() = repository.loginLiveData

    fun login(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.login(map)
        }
    }

    val socialLoginResult: LiveData<LoginResponse>
        get() = repository.socialLoginLiveData

    fun socialLogin(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.socialLogin(map)
        }
    }

    val loginEmailMobileResult: LiveData<LoginResponse>
        get() = repository.loginEmailMobileLiveData

    fun loginViaEmailOrMobile(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.loginViaEmailOrMobile(map)
        }
    }

    val otpVerifyResult: LiveData<LoginResponse>
        get() = repository.otpVerifyLiveData

    fun otpVerification(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.otpVerification(map)
        }
    }

    val resendOtpResult: LiveData<LoginResponse>
        get() = repository.resendOtpLiveData

    fun resendOtp(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.resendOtp(map)
        }
    }

    val forgotPasswordResult: LiveData<LoginResponse>
        get() = repository.forgotPasswordLiveData

    fun forgotPassword(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.forgotPassword(map)
        }
    }

    val createPasswordResult: LiveData<LoginResponse>
        get() = repository.createPasswordLiveData

    fun createPassword(map: HashMap<String, Any>) {
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

    val getSelectedCountryResult: LiveData<GetCountryResponse>
        get() = repository.getSelectedCountryLiveData

    fun getSelectedCountry() {
        viewModelScope.launch {
            repository.getSelectedCountry()
        }
    }

    val createAffiliateUserResult: LiveData<CreateAffiliateUserResponse>
        get() = repository.createAffiliateUserLiveData

    fun createAffiliateUser(request: CreateAffiliateUserRequest) {
        viewModelScope.launch {
            repository.createAffiliateUser(request)
        }
    }

    val addAddressAffiliateUserResult: LiveData<AddAffiliateUserAddressResponse>
        get() = repository.addAddressAffiliateUserLiveData

    fun addAffiliateUserAddress(request: AffiliateUserAddAddressRequest) {
        viewModelScope.launch {
            repository.addAffiliateUserAddress(request)
        }
    }

    val addBankAffiliateUserResult: LiveData<AddAffiliateUserBankDetail>
        get() = repository.bankDetailAffiliateUserLiveData

    fun addAffiliateUserBankDetail(request: AffiliateUserAddBankDetailRequest) {
        viewModelScope.launch {
            repository.addAffiliateUserBankDetail(request)
        }
    }

    val updateAffiliateUserBankResult: LiveData<CommonResponse>
        get() = repository.updateAffiliateUserBankLiveData

    fun updateAffiliateUserBank(request: AffiliateUserAddBankDetailRequest) {
        viewModelScope.launch {
            repository.updateAffiliateUserBank(request)
        }
    }

    val addAffiliateUserBankResult: LiveData<AddAnotherBankResponse>
        get() = repository.addAffiliateUserBankLiveData

    fun addAffiliateUserBank(request: AffiliateUserAddBankDetailRequest) {
        viewModelScope.launch {
            repository.addAffiliateUserBank(request)
        }
    }

    val otpVerifyAffiliateUserResult: LiveData<AffiliateUserLoginResponse>
        get() = repository.otpVerifyAffiliateUserLiveData

    fun verifyAffiliateUserOtp(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.verifyAffiliateUserOtp(map)
        }
    }

    val resendOtpAffiliateUserResult: LiveData<AffiliateUserLoginResponse>
        get() = repository.resendOtpAffiliateUserLiveData

    fun resendOtpAffiliateUser(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.resendOtpAffiliateUser(map)
        }
    }

    val forgotPasswordAffiliateUserResult: LiveData<AffiliateUserLoginResponse>
        get() = repository.forgotPasswordAffiliateUserLiveData

    fun forgotPasswordAffiliateUser(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.forgotPasswordAffiliateUser(map)
        }
    }

    val createPasswordAffiliateUserResult: LiveData<AffiliateUserLoginResponse>
        get() = repository.createPasswordAffiliateUserLiveData

    fun createAffiliateUserPassword(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.createAffiliateUserPassword(map)
        }
    }

    val reVerifyDocumentResult: LiveData<AffiliateUserLoginResponse>
        get() = repository.reVerifyDocumentLiveData

    fun reVerifyDocument(request: CreateAffiliateUserRequest) {
        viewModelScope.launch {
            repository.reVerifyDocument(request)
        }
    }

    val loginAffiliateUserResult: LiveData<AffiliateUserLoginResponse>
        get() = repository.loginAffiliateUserLiveData

    fun loginAffiliateUser(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.loginAffiliateUser(map)
        }
    }

    val loginWithOtpAffiliateUserResult: LiveData<AffiliateUserLoginResponse>
        get() = repository.loginWithOtpAffiliateUserLiveData

    fun loginWithOtpAffiliateUser(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.loginWithOtpAffiliateUser(map)
        }
    }
}
