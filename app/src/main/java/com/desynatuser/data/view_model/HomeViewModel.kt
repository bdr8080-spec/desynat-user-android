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
import com.desynatuser.ui.home.fragment.response.GetHomeSearchResponse
import com.desynatuser.ui.home.fragment.response.GetRecentSearchListResponse
import com.desynatuser.ui.home.giftCard.response.AddGiftCardRequest
import com.desynatuser.ui.home.giftCard.response.ApplyGiftCardResponse
import com.desynatuser.ui.home.giftCard.response.BuyGiftCardResponse
import com.desynatuser.ui.home.giftCard.response.GetAllGiftCardResponse
import com.desynatuser.ui.home.giftCard.response.GetGiftCardOccasionTypeResponse
import com.desynatuser.ui.home.giftCard.response.WalletBalanceResponse
import com.desynatuser.ui.home.imageDetector.VisionRequestBody
import com.desynatuser.ui.home.imageDetector.VisionResponse
import com.desynatuser.ui.home.myOrder.response.GetAllOrderResponse
import com.desynatuser.ui.home.myOrder.response.GetReasonListResponse
import com.desynatuser.ui.home.myOrder.response.GetSingleOrderSummaryResponse
import com.desynatuser.ui.home.profile.response.AddAddressRequest
import com.desynatuser.ui.home.profile.response.ApplyPromoCodeResponse
import com.desynatuser.ui.home.profile.response.ChangeMobileNumbeResponse
import com.desynatuser.ui.home.profile.response.GetAddressResponse
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetPromoCodeResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.ui.home.profile.response.UpdateAddressResponse
import com.desynatuser.ui.home.store.response.AddToCartResponse
import com.desynatuser.ui.home.store.response.CartQunatityIncrementDecrementResponse
import com.desynatuser.ui.home.store.response.GetAllBrandListResponse
import com.desynatuser.ui.home.store.response.GetAllColorListResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.ui.home.store.response.GetProductDetailResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetSizeListResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreSortListResponse
import com.desynatuser.ui.home.store.response.GetWishlistResponse
import com.desynatuser.ui.home.store.response.PlaceOrderRequest
import com.desynatuser.ui.home.store.response.PlaceOrderResponse
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

    val defaultAddressResult: LiveData<CommonResponse>
        get() = repository.defaultAddressLiveData

    fun setDefaultAddress(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.setDefaultAddress(map)
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

val productDetailResult: LiveData<GetProductDetailResponse>
        get() = repository.productDetailLiveData

    fun getProductDetailInfo(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getProductDetailInfo(map)
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

    val wishlistResult: LiveData<GetWishlistResponse>
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

    val colorListResult: LiveData<GetAllColorListResponse>
        get() = repository.colorListLiveData

    fun getColorList() {
        viewModelScope.launch {
            repository.getColorList()
        }
    }

    val brandListResult: LiveData<GetAllBrandListResponse>
        get() = repository.brandLiveData

    fun getBrandList() {
        viewModelScope.launch {
            repository.getBrandList()
        }
    }

val sizeListResult: LiveData<GetSizeListResponse>
        get() = repository.sizeLiveData

    fun getSizeList() {
        viewModelScope.launch {
            repository.getSizeList()
        }
    }

val giftCardListResult: LiveData<GetAllGiftCardResponse>
        get() = repository.giftCardLiveData

    fun getGiftCardList(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getGiftCardList(map)
        }
    }

val giftCardOccasionResult: LiveData<GetGiftCardOccasionTypeResponse>
        get() = repository.giftCardOccasionLiveData

    fun getGiftCardOccasion() {
        viewModelScope.launch {
            repository.getGiftCardOccasion()
        }
    }

val buyGiftCardResult: LiveData<BuyGiftCardResponse>
        get() = repository.buyGiftCardLiveData

    fun buyGiftCard(request:AddGiftCardRequest) {
        viewModelScope.launch {
            repository.buyGiftCard(request)
        }
    }

    val buyGiftCardPaymentResult: LiveData<CommonResponse>
        get() = repository.buyGiftCardPaymentLiveData

    fun buyGiftCardPayment(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.buyGiftCardPayment(map)
        }
    }


    val applyGiftCardResult: LiveData<ApplyGiftCardResponse>
        get() = repository.applyGiftCardLiveData

    fun applyGiftCard(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.applyGiftCard(map)
        }
    }



    val promoCodeListResult: LiveData<GetPromoCodeResponse>
        get() = repository.promoCodeLiveData

    fun getPromoCode(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getPromoCode(map)
        }
    }

val applyPromoCodeResult: LiveData<ApplyPromoCodeResponse>
        get() = repository.applyPromoCodeLiveData

    fun applyPromoCode(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.applyPromoCode(map)
        }
    }


val orderPaymentResult: LiveData<CommonResponse>
        get() = repository.orderPaymentLiveData

    fun orderPayment(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.orderPayment(map)
        }
    }


val placeOrderResult: LiveData<PlaceOrderResponse>
        get() = repository.placeOrderLiveData

    fun placeOrder(request: PlaceOrderRequest) {
        viewModelScope.launch {
            repository.placeOrder(request)
        }
    }

    val orderDetailResult: LiveData<GetSingleOrderSummaryResponse>
        get() = repository.orderDetailLiveData

    fun getOrderDetailInfo(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getOrderDetailInfo(map)
        }
    }

    val getReasonListResult: LiveData<GetReasonListResponse>
        get() = repository.getReasonListLiveData

    fun getReasonList(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getReasonList(map)
        }
    }

    val cancelOrderResult: LiveData<CommonResponse>
        get() = repository.cancelOrderLiveData

    fun cancelOrder(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.cancelOrder(map)
        }
    }

    val returnExchangeOrderResult: LiveData<CommonResponse>
        get() = repository.returnExchangeOrderLiveData

    fun returnExchangeOrder(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.returnExchangeOrder(map)
        }
    }

    val orderRatingResult: LiveData<CommonResponse>
        get() = repository.orderRatingLiveData

    fun orderRating(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.orderRating(map)
        }
    }

    val orderListResult: LiveData<GetAllOrderResponse>
        get() = repository.orderListLiveData

    fun getAllOrder(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAllOrder(map)
        }
    }

    val walletBalanceResult: LiveData<WalletBalanceResponse>
        get() = repository.walletBalanceLiveData

    fun getWalletBalance() {
        viewModelScope.launch {
            repository.getWalletBalance()
        }
    }

    val homePageSearchResult: LiveData<GetHomeSearchResponse>
        get() = repository.homePageSearchLiveData

    fun homePageSearch(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.homePageSearch(map)
        }
    }

    val getRecentSearchListResult: LiveData<GetRecentSearchListResponse>
        get() = repository.getRecentSearchListLiveData

    fun getRecentSearchList() {
        viewModelScope.launch {
            repository.getRecentSearchList()
        }
    }

    val detectImageResult: LiveData<VisionResponse>
        get() = repository.detectImageLiveData

    fun detectImage(key:String,request: VisionRequestBody) {
        viewModelScope.launch {
            repository.detectImage(key,request)
        }
    }


}
