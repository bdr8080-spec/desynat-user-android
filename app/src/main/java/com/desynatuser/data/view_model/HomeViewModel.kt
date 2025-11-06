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
import com.desynatuser.ui.home.response.GetCategoryResponse
import com.desynatuser.ui.home.response.GetSubCategoryResponse
import com.desynatuser.ui.response.CommonResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val progress: LiveData<Boolean> get() = repository.progress
    val error: LiveData<String> get() = repository.error
    fun reset() {
        repository.error.value = null
        repository.progress.value = false
    }

    val dashBoardResult: LiveData<LoginResponse>
        get() = repository.dashBoardLiveData

    fun getDashBoard() {
        viewModelScope.launch {
            repository.getDashBoard()
        }
    }
    val logoutResult: LiveData<CommonResponse>
        get() = repository.logoutLiveData

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

   val countryResult: LiveData<GetCountryResponse>
        get() = repository.countryLiveData

    fun getCountry() {
        viewModelScope.launch {
            repository.getCountry()
        }
    }

    val changePasswordResult: LiveData<CommonResponse>
        get() = repository.changePasswordLiveData

    fun changePassword(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.changePassword(map)
        }
    }

    val categoryResult: LiveData<GetCategoryResponse>
        get() = repository.categoryLiveData

    fun getCategory() {
        viewModelScope.launch {
            repository.getCategory()
        }
    }

    val subCategoryResult: LiveData<GetSubCategoryResponse>
        get() = repository.subCategoryLiveData

    fun getSubCategory(categoryId:String) {
        viewModelScope.launch {
            repository.getSubCategory(categoryId)
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

    val updateProfiledResult: LiveData<LoginResponse>
        get() = repository.updateProfileLiveData

    fun updateProfile(map: HashMap<String,String>) {
        viewModelScope.launch {
            repository.updateProfile(map)
        }
    }

    val changeNumberResult: LiveData<LoginResponse>
        get() = repository.changeNumberLiveData

    fun changeNumber(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.changeNumber(map)
        }
    }

    val userOtpResult: LiveData<LoginResponse>
        get() = repository.userOtpLiveData

    fun userOtp(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.userOtp(map)
        }
    }



}
