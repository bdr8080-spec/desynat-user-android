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
import com.desynatuser.ui.home.response.AddAddressRequest
import com.desynatuser.ui.home.response.ChangeMobileNumbeResponse
import com.desynatuser.ui.home.response.GetAddressResponse
import com.desynatuser.ui.home.response.GetCategoryResponse
import com.desynatuser.ui.home.response.GetSubCategoryResponse
import com.desynatuser.ui.home.store.response.AddToCartResponse
import com.desynatuser.ui.home.store.response.CartQunatityIncrementDecrementResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreSortListResponse
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

    fun getSubCategory(categoryId:String,search:String?=null) {
        viewModelScope.launch {
            repository.getSubCategory(categoryId,search)
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

    val changeNumberResult: LiveData<ChangeMobileNumbeResponse>
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

    val addAddressResult: LiveData<CommonResponse>
        get() = repository.addAddressLiveData

    fun addAddress(request: AddAddressRequest) {
        viewModelScope.launch {
            repository.addAddress(request)
        }
    }

    val editAddressResult: LiveData<CommonResponse>
        get() = repository.editAddressLiveData

    fun editAddress(request: AddAddressRequest) {
        viewModelScope.launch {
            repository.editAddress(request)
        }
    }

    val deleteAddressResult: LiveData<CommonResponse>
        get() = repository.deleteAddressLiveData

    fun deleteAddress(id:String) {
        viewModelScope.launch {
            repository.deleteAddress(id)
        }
    }

    val addressResult: LiveData<GetAddressResponse>
        get() = repository.addressLiveData

    fun getAddress() {
        viewModelScope.launch {
            repository.getAddress()
        }
    }

val storeListResult: LiveData<GetStoreResponse>
        get() = repository.storeListLiveData

    fun getStoreList(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getStoreList(map)
        }
    }


val storeDetailResult: LiveData<GetSingleStoreResponse>
        get() = repository.storeDetailLiveData

    fun getSingleStoreInfo(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getSingleStoreInfo(map)
        }
    }

    val addToCartResult: LiveData<AddToCartResponse>
        get() = repository.addToCartLiveData

    fun addToCart(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addToCart(map)
        }
    }


    val cartQuantityResult: LiveData<CartQunatityIncrementDecrementResponse>
        get() = repository.cartQunatityLiveData

    fun addCartQuantity(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addCartQuantity(map)
        }
    }


    val cartDetailLiveDataResult: LiveData<GetCartResponse>
        get() = repository.cartDetailLiveData

    fun viewCartDetail() {
        viewModelScope.launch {
            repository.viewCartDetail()
        }
    }


    val addToWishlistResult: LiveData<CommonResponse>
        get() = repository.addToWishlistLiveData

    fun addToWishlist(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addToWishlist(map)
        }
    }

 val storeSortResult: LiveData<GetStoreSortListResponse>
        get() = repository.storeSortListLiveData

    fun getStoreSorList() {
        viewModelScope.launch {
            repository.getStoreSorList()
        }
    }

    val wishlistResult: LiveData<CommonResponse>
        get() = repository.wishListLiveData

    fun getWishList() {
        viewModelScope.launch {
            repository.getWishList()
        }
    }


    val searchResult: LiveData<CommonResponse>
        get() = repository.searchLiveData

    fun getSearch() {
        viewModelScope.launch {
            repository.getSearch()
        }
    }


}
