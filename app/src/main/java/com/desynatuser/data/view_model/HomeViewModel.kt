package com.desynatuser.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatdriver.ui.home.cms.response.GetCmsResponse
import com.desynatdriver.ui.home.cms.response.GetFaqResponse
import com.desynatuser.data.repo.MainRepository
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddAddressRequest
import com.desynatuser.ui.affiliateUser.home.payment.response.GetAllAffiliateOrderResponse
import com.desynatuser.ui.affiliateUser.home.payment.response.GetWithdrawalRequestResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateAllProductResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateProductDetailResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateMyProductResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateProductLinkResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateMyProductDetailResponse
import com.desynatuser.ui.affiliateUser.home.profile.response.AffiliateChangeUserOtpVerifyResponse
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.ui.affiliateUser.home.profile.response.UpdateAffiliateProfileRequest
import com.desynatuser.ui.affiliateUser.home.profile.response.UpdateAffiliateProfileResponse
import com.desynatuser.ui.affiliateUser.home.response.GetHomeDashboardResponse
import com.desynatuser.ui.auth.response.FileUploadResponse
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.ui.home.card.response.GetSavedCardResponse
import com.desynatuser.ui.home.chat.response.AcceptCustomOrderResponse
import com.desynatuser.ui.home.chat.response.AcceptRejectQuotationRequestResponse
import com.desynatuser.ui.home.chat.response.ChatDetailResponse
import com.desynatuser.ui.home.chat.response.CreateCustomOrderResponse
import com.desynatuser.ui.home.chat.response.GetChatListResponse
import com.desynatuser.ui.home.chat.response.NormalChatResponse
import com.desynatuser.ui.home.cms.response.GetAllRaisedTicketResponse
import com.desynatuser.ui.home.cms.response.GetContactResponse
import com.desynatuser.ui.home.cms.response.GetTicketTypeResponse
import com.desynatuser.ui.home.fragment.response.GetAffiliateNotificationCountResponse
import com.desynatuser.ui.home.fragment.response.GetAllSuggestStoreResponse
import com.desynatuser.ui.home.fragment.response.GetBestSellingProductResponse
import com.desynatuser.ui.home.fragment.response.GetDealBannerResponse
import com.desynatuser.ui.home.fragment.response.GetHomeSearchResponse
import com.desynatuser.ui.home.fragment.response.GetNewestProductResponse
import com.desynatuser.ui.home.fragment.response.GetNotificationCountResponse
import com.desynatuser.ui.home.fragment.response.GetNotificationResponse
import com.desynatuser.ui.home.fragment.response.GetRecentSearchListResponse
import com.desynatuser.ui.home.fragment.response.GetRecommendedResponse
import com.desynatuser.ui.home.fragment.response.GetTopRatedProductResponse
import com.desynatuser.ui.home.giftCard.response.AddGiftCardRequest
import com.desynatuser.ui.home.giftCard.response.ApplyGiftCardResponse
import com.desynatuser.ui.home.giftCard.response.BuyGiftCardResponse
import com.desynatuser.ui.home.giftCard.response.GetAllGiftCardResponse
import com.desynatuser.ui.home.giftCard.response.GetGiftCardOccasionTypeResponse
import com.desynatuser.ui.home.giftCard.response.WalletBalanceResponse
import com.desynatuser.ui.home.imageDetector.VisionRequestBody
import com.desynatuser.ui.home.imageDetector.VisionResponse
import com.desynatuser.ui.home.myOrder.response.GetAllOrderResponse
import com.desynatuser.ui.home.myOrder.response.GetInvoiceResponse
import com.desynatuser.ui.home.myOrder.response.GetReasonListResponse
import com.desynatuser.ui.home.myOrder.response.GetSingleOrderSummaryResponse
import com.desynatuser.ui.home.myWallet.response.AddMoneyInWalletResponse
import com.desynatuser.ui.home.myWallet.response.CompletePyamentWalletResponse
import com.desynatuser.ui.home.myWallet.response.GetWalletHistoryResponse
import com.desynatuser.ui.home.profile.response.AddAddressRequest
import com.desynatuser.ui.home.profile.response.ApplyPromoCodeResponse
import com.desynatuser.ui.home.profile.response.ChangeMobileNumbeResponse
import com.desynatuser.ui.home.profile.response.GetAddressResponse
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetPromoCodeResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.ui.home.store.response.AddToCartResponse
import com.desynatuser.ui.home.store.response.CartQunatityIncrementDecrementResponse
import com.desynatuser.ui.home.store.response.GetAllBrandListResponse
import com.desynatuser.ui.home.store.response.GetAllColorListResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.ui.home.store.response.GetProductDetailResponse
import com.desynatuser.ui.home.store.response.GetSharedReelLinkResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetSizeListResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreSortListResponse
import com.desynatuser.ui.home.store.response.GetWishlistResponse
import com.desynatuser.ui.home.store.response.GetWishlistedReelResponse
import com.desynatuser.ui.home.store.response.PlaceOrderRequest
import com.desynatuser.ui.home.store.response.PlaceOrderResponse
import com.desynatuser.ui.home.storyHighlights.response.GetAgoraTokenResponse
import com.desynatuser.ui.home.storyHighlights.response.GetLiveStreamingRoomResponse
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse
import com.desynatuser.ui.response.CommonResponse
import com.desynatuser.ui.response.GetHomeScreenAllBannerResponse
import com.desynatuser.ui.response.GetHomeScreenPopUpBanner
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
   val getCurrencyListResult: LiveData<GetCountryResponse>
        get() = repository.getCurrencyListLiveData

    fun getCurrencyList() {
        viewModelScope.launch {
            repository.getCurrencyList()
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

    fun getSubCategory(categoryId:String?=null,search:String?=null) {
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

    val getSelectedCountryResult: LiveData<GetCountryResponse>
        get() = repository.getSelectedCountryLiveData

    fun getSelectedCountry() {
        viewModelScope.launch {
            repository.getSelectedCountry()
        }
    }

    val updateProfiledResult: LiveData<LoginResponse>
        get() = repository.updateProfileLiveData

    fun updateProfile(map: HashMap<String,String>) {
        viewModelScope.launch {
            repository.updateProfile(map)
        }
    }

    val getProfiledResult: LiveData<LoginResponse>
        get() = repository.getProfileLiveData

    fun getProfile() {
        viewModelScope.launch {
            repository.getProfile()
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

 val cartHomeDetailLiveDataResult: LiveData<GetCartResponse>
        get() = repository.cartHomeDetailLiveData

    fun viewHomeCartDetail() {
        viewModelScope.launch {
            repository.viewHomeCartDetail()
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
    val getLatestSearchListResult: LiveData<GetRecentSearchListResponse>
        get() = repository.getLatestSearchListLiveData

    fun getLatestSearchList() {
        viewModelScope.launch {
            repository.getLatestSearchList()
        }
    }
    val getRecommendedSearchListResult: LiveData<GetRecommendedResponse>
        get() = repository.getRecommendedSearchListLiveData

    fun getRecommendedSearchList() {
        viewModelScope.launch {
            repository.getRecommendedSearchList()
        }
    }

    val detectImageResult: LiveData<VisionResponse>
        get() = repository.detectImageLiveData

    fun detectImage(key:String,request: VisionRequestBody) {
        viewModelScope.launch {
            repository.detectImage(key,request)
        }
    }

    val invoiceResult: LiveData<GetInvoiceResponse>
        get() = repository.invoiceLiveData

    fun getInvoice(id:String) {
        viewModelScope.launch {
            repository.getInvoice(id)
        }
    }

    val createCustomOrderResult: LiveData<CreateCustomOrderResponse>
        get() = repository.createCustomOrderLiveData

    fun createCustomOrder(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.createCustomOrder(map)
        }
    }
    val sendCustomOrderRequestResult: LiveData<CreateCustomOrderResponse>
        get() = repository.sendCustomOrderRequestLiveData

    fun sendCustomOrderRequest(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.sendCustomOrderRequest(map)
        }
    }

    val applyCustomOrderGiftCardResult: LiveData<AcceptCustomOrderResponse>
        get() = repository.applyCustomOrderGiftCardLiveData

    fun applyCustomOrderGiftCard(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.applyCustomOrderGiftCard(map)
        }
    }
    val customOrderPaymentResult: LiveData<CommonResponse>
        get() = repository.customOrderPaymentLiveData

    fun customOrderPayment(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.customOrderPayment(map)
        }
    }

    val getAllChatListResult: LiveData<GetChatListResponse>
        get() = repository.getAllChatListLiveData

    fun getAllChatList(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAllChatList(map)
        }
    }
    val markAllReadResult: LiveData<CommonResponse>
        get() = repository.markAllReadLiveData

    fun markAllRead(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.markAllRead(map)
        }
    }

    val sendNotificationResult: LiveData<CommonResponse>
        get() = repository.sendNotificationLiveData

    fun sendNotification(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.sendNotification(map)
        }
    }
    val chatDetailResult: LiveData<ChatDetailResponse>
        get() = repository.chatDetailLiveData

    fun chatDetail(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.chatDetail(map)
        }
    }

    val sendMessageResult: LiveData<CommonResponse>
        get() = repository.sendMessageLiveData

    fun sendMessage(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.sendMessage(map)
        }
    }

    val chatWithSellerResult: LiveData<NormalChatResponse>
        get() = repository.chatWithSellerLiveData

    fun chatWithSeller(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.chatWithSeller(map)
        }
    }

    val acceptRejectQuotationResult: LiveData<AcceptRejectQuotationRequestResponse>
        get() = repository.acceptRejectQuotationLiveData

    fun acceptRejectQuotation(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.acceptRejectQuotation(map)
        }
    }

val getHomeScreenBannerResult: LiveData<GetHomeScreenPopUpBanner>
        get() = repository.getHomeScreenBannerLiveData

    fun getHomeScreenBanner() {
        viewModelScope.launch {
            repository.getHomeScreenBanner()
        }
    }

val getHomeScreenAllBannerResult: LiveData<GetHomeScreenAllBannerResponse>
        get() = repository.getHomeScreenAllBannerLiveData

    fun getHomeScreenAllBanner(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getHomeScreenAllBanner(map)
        }
    }

val getHomeScreenBottomBannerResult: LiveData<GetHomeScreenAllBannerResponse>
        get() = repository.getHomeScreenBottomBannerLiveData

    fun getHomeScreenBottomBanner(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getHomeScreenBottomBanner(map)
        }
    }

val getHomeScreenCategoryBannerResult: LiveData<GetHomeScreenAllBannerResponse>
        get() = repository.getHomeScreenCategoryBannerLiveData

    fun getHomeScreenCategoryBanner(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getHomeScreenCategoryBanner(map)
        }
    }

val addMoneyInWalletResult: LiveData<AddMoneyInWalletResponse>
        get() = repository.addMoneyInWalletLiveData

    fun addMoneyInWallet(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addMoneyInWallet(map)
        }
    }

val addMoneyInWalletPaymentResult: LiveData<CompletePyamentWalletResponse>
        get() = repository.addMoneyInWalletPaymentLiveData

    fun addMoneyInWalletPayment(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addMoneyInWalletPayment(map)
        }
    }

val redeemGiftCardInWalletResult: LiveData<CommonResponse>
        get() = repository.redeemGiftCardInWalletLiveData

    fun redeemGiftCardInWallet(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.redeemGiftCardInWallet(map)
        }
    }

val getWalletHistoryResult: LiveData<GetWalletHistoryResponse>
        get() = repository.getWalletHistoryLiveData

    fun getWalletHistory() {
        viewModelScope.launch {
            repository.getWalletHistory()
        }
    }

    val addToCartCustomOrderResult: LiveData<CommonResponse>
        get() = repository.addToCartCustomOrderLiveData

    fun addToCartCustomOrder(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addToCartCustomOrder(map)
        }
    }

    val removeFromCartCustomOrderResult: LiveData<CommonResponse>
        get() = repository.removeFromCartCustomOrderLiveData

    fun removeFromCartCustomOrder(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.removeFromCartCustomOrder(map)
        }
    }

    val getAffiliateProfileResult: LiveData<GetAffiliateUserProfileResponse>
        get() = repository.getAffiliateProfileLiveData

    fun getAffiliateProfile() {
        viewModelScope.launch {
            repository.getAffiliateProfile()
        }
    }

    val updateAffiliateUserProfileResult: LiveData<UpdateAffiliateProfileResponse>
        get() = repository.updateAffiliateUserProfileLiveData

    fun updateAffiliateUserProfile(request: UpdateAffiliateProfileRequest) {
        viewModelScope.launch {
            repository.updateAffiliateUserProfile(request)
        }
    }

    val updateAffiliateUserAddressResult: LiveData<CommonResponse>
        get() = repository.updateAffiliateUserAddressLiveData

    fun updateAffiliateUserAddress(request : AffiliateUserAddAddressRequest) {
        viewModelScope.launch {
            repository.updateAffiliateUserAddress(request)
        }
    }

    val updateAffiliateUserPasswordResult: LiveData<CommonResponse>
        get() = repository.updateAffiliateUserPasswordLiveData

    fun updateAffiliateUserPassword(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updateAffiliateUserPassword(map)
        }
    }

    val deleteBankResult: LiveData<CommonResponse>
        get() = repository.deleteBankLiveData

    fun deleteBank(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deleteBank(map)
        }
    }

val defaultBankResult: LiveData<CommonResponse>
        get() = repository.defaultBankLiveData

    fun defaultBank(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.defaultBank(map)
        }
    }

    val getAffiliateAllProductResult: LiveData<GetAffiliateAllProductResponse>
        get() = repository.getAffiliateAllProductLiveData

    fun getAffiliateAllProduct(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAffiliateAllProduct(map)
        }
    }

    val getAffiliateProductDetailResult: LiveData<GetAffiliateProductDetailResponse>
        get() = repository.getAffiliateProductDetailLiveData

    fun getAffiliateProductDetail(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAffiliateProductDetail(map)
        }
    }

    val getAffiliateMyProductDetailResult: LiveData<GetAffiliateMyProductDetailResponse>
        get() = repository.getAffiliateMyProductDetailLiveData

    fun getAffiliateMyProductDetail(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAffiliateMyProductDetail(map)
        }
    }

    val removeAffiliateProductResult: LiveData<CommonResponse>
        get() = repository.removeAffiliateProductLiveData

    fun removeAffiliateProduct(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.removeAffiliateProduct(map)
        }
    }

    val addAffiliateProductResult: LiveData<CommonResponse>
        get() = repository.addAffiliateProductLiveData

    fun addAffiliateProduct(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addAffiliateProduct(map)
        }
    }

    val getAffiliateProductLinkResult: LiveData<GetAffiliateProductLinkResponse>
        get() = repository.getAffiliateProductLinkLiveData

    fun getAffiliateProductLink(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAffiliateProductLink(map)
        }
    }

    val getAffiliateMyProductResult: LiveData<GetAffiliateMyProductResponse>
        get() = repository.getAffiliateMyProductLiveData

    fun getAffiliateMyProduct(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAffiliateMyProduct(map)
        }
    }

    val getAffiliateSorListResult: LiveData<GetStoreSortListResponse>
        get() = repository.getAffiliateSorListLiveData

    fun getAffiliateSorList() {
        viewModelScope.launch {
            repository.getAffiliateSorList()
        }
    }

    val affiliateProductSearchResult: LiveData<GetAffiliateAllProductResponse>
        get() = repository.affiliateProductSearchLiveData

    fun affiliateProductSearch(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.affiliateProductSearch(map)
        }
    }

    val getAffiliateRecentSearchListResult: LiveData<GetRecentSearchListResponse>
        get() = repository.getAffiliateRecentSearchListLiveData

    fun getAffiliateRecentSearchList() {
        viewModelScope.launch {
            repository.getAffiliateRecentSearchList()
        }
    }
    val getAffiliateLatestSearchListResult: LiveData<GetRecentSearchListResponse>
        get() = repository.getAffiliateLatestSearchListLiveData

    fun getAffiliateLatestSearchList() {
        viewModelScope.launch {
            repository.getAffiliateLatestSearchList()
        }
    }

    val changeAffiliateNumberResult: LiveData<ChangeMobileNumbeResponse>
        get() = repository.changeAffiliateNumberLiveData

    fun changeAffiliateNumber(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.changeAffiliateNumber(map)
        }
    }

    val affiliateUserOtpResult: LiveData<AffiliateChangeUserOtpVerifyResponse>
        get() = repository.affiliateUserOtpLiveData

    fun affiliateUserOtp(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.affiliateUserOtp(map)
        }
    }

    val getAffiliatePaymentResult: LiveData<GetAllAffiliateOrderResponse>
        get() = repository.getAffiliatePaymentLiveData

    fun getAffiliatePayment(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAffiliatePayment(map)
        }
    }

    val getWithdrawalRequestResult: LiveData<GetWithdrawalRequestResponse>
        get() = repository.getWithdrawalRequestLiveData

    fun getWithdrawalRequest(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getWithdrawalRequest(map)
        }
    }

    val withdrawAffiliateAmountResult: LiveData<CommonResponse>
        get() = repository.withdrawAffiliateAmountLiveData

    fun withdrawAffiliateAmount(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.withdrawAffiliateAmount(map)
        }
    }

    val getDealBannerResult: LiveData<GetDealBannerResponse>
        get() = repository.getDealBannerLiveData

    fun getDealBanner(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getDealBanner(map)
        }
    }

    val getUserNotificationResult: LiveData<GetNotificationResponse>
        get() = repository.getUserNotificationLiveData

    fun getUserNotification(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getUserNotification(map)
        }
    }

    val readUserNotificationResult: LiveData<CommonResponse>
        get() = repository.readUserNotificationLiveData

    fun readUserNotification(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.readUserNotification(map)
        }
    }

 val getAffiliateUserNotificationResult: LiveData<GetNotificationResponse>
        get() = repository.getAffiliateUserNotificationLiveData

    fun getAffiliateUserNotification(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAffiliateUserNotification(map)
        }
    }

 val getAffiliateDashboardResult: LiveData<GetHomeDashboardResponse>
        get() = repository.getAffiliateDashboardLiveData

    fun getAffiliateDashboard(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAffiliateDashboard(map)
        }
    }

    val readAffiliateUserNotificationResult: LiveData<CommonResponse>
        get() = repository.readAffiliateUserNotificationLiveData

    fun readAffiliateUserNotification(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.readAffiliateUserNotification(map)
        }
    }

    val userNotificationCountResult: LiveData<GetNotificationCountResponse>
        get() = repository.userNotificationCountLiveData

    fun userNotificationCount() {
        viewModelScope.launch {
            repository.userNotificationCount()
        }
    }
    val affiliateUserNotificationCountResult: LiveData<GetAffiliateNotificationCountResponse>
        get() = repository.affiliateUserNotificationCountLiveData

    fun affiliateUserNotificationCount() {
        viewModelScope.launch {
            repository.affiliateUserNotificationCount()
        }
    }


    val getSellerStoryResult: LiveData<GetSellerStoryResponse>
        get() = repository.getSellerStoryLiveData

    fun getSellerStory(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getSellerStory(map)
        }
    }

    val getSellerReelResult: LiveData<GetSellerStoryResponse>
        get() = repository.getSellerReelLiveData

    fun getSellerReel(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getSellerReel(map)
        }
    }


    val seeStoryResult : LiveData<CommonResponse>
        get() = repository.seeStoryLiveData

    fun seeStory(
        map: HashMap<String,Any>
    ) {
        viewModelScope.launch {
            repository.
            seeStory(map)
        }
    }

    val getWishlistedReelResult: LiveData<GetWishlistedReelResponse>
        get() = repository.getWishlistedReelLiveData

    fun getWishlistedReel(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getWishlistedReel(map)
        }
    }

    val getSellerHighlightsResult: LiveData<GetSellerStoryResponse>
        get() = repository.getSellerHighlightsLiveData

    fun getSellerHighlights(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getSellerHighlights(map)
        }
    }

    val getAdminStoryResult: LiveData<GetSellerStoryResponse>
        get() = repository.getAdminStoryLiveData

    fun getAdminStory(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAdminStory(map)
        }
    }

    val addRemoveReelToWishlistResult: LiveData<CommonResponse>
        get() = repository.addRemoveReelToWishlistLiveData

    fun addRemoveReelToWishlist(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addRemoveReelToWishlist(map)
        }
    }


    val shareReelResult: LiveData<GetSharedReelLinkResponse>
        get() = repository.shareReelLiveData

    fun shareReel(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.shareReel(map)
        }
    }

    val getAgoraTokenResult: LiveData<GetAgoraTokenResponse>
        get() = repository.getAgoraTokenLiveData

    fun getAgoraToken(token: String, map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAgoraToken(token,map)
        }
    }
    val getLiveStreamingRoomResult: LiveData<GetLiveStreamingRoomResponse>
        get() = repository.getLiveStreamingRoomLiveData

    fun getLiveStreamingRoom() {
        viewModelScope.launch {
            repository.getLiveStreamingRoom()
        }
    }

    val getSuggestedStoreResult: LiveData<GetAllSuggestStoreResponse>
        get() = repository.getSuggestedStoreLiveData

    fun getSuggestedStore() {
        viewModelScope.launch {
            repository.getSuggestedStore()
        }
    }

    val getNewestProductResult: LiveData<GetNewestProductResponse>
        get() = repository.getNewestProductLiveData

    fun getNewestProduct() {
        viewModelScope.launch {
            repository.getNewestProduct()
        }
    }

    val getTopRatedProductResult: LiveData<GetTopRatedProductResponse>
        get() = repository.getTopRatedProductLiveData

    fun getTopRatedProduct() {
        viewModelScope.launch {
            repository.getTopRatedProduct()
        }
    }

    val getBestSellingProductResult: LiveData<GetBestSellingProductResponse>
        get() = repository.getBestSellingProductLiveData

    fun getBestSellingProduct() {
        viewModelScope.launch {
            repository.getBestSellingProduct()
        }
    }

    val getTicketTypeResult: LiveData<GetTicketTypeResponse>
        get() = repository.getTicketTypeLiveData

    fun getTicketType() {
        viewModelScope.launch {
            repository.getTicketType()
        }
    }

    val getRaisedTicketResult: LiveData<GetAllRaisedTicketResponse>
        get() = repository.getRaisedTicketLiveData

    fun getRaisedTicket(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getRaisedTicket(map)
        }
    }

    val raiseTicketResult: LiveData<CommonResponse>
        get() = repository.raiseTicketLiveData

    fun raiseTicket(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.raiseTicket(map)
        }
    }

    val getAffiliateRaisedTicketResult: LiveData<GetAllRaisedTicketResponse>
        get() = repository.getAffiliateRaisedTicketLiveData

    fun getAffiliateRaisedTicket(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAffiliateRaisedTicket(map)
        }
    }

    val affiliateRaiseTicketResult: LiveData<CommonResponse>
        get() = repository.affiliateRaiseTicketLiveData

    fun affiliateRaiseTicket(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.affiliateRaiseTicket(map)
        }
    }
    val contactUsResult: LiveData<GetContactResponse>
        get() = repository.contactUsLiveData

    fun contactUs(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.contactUs(map)
        }
    }

    val faqResult: LiveData<GetFaqResponse>
        get() = repository.faqLiveData

    fun faq(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.faq(map)
        }
    }

    val termsAndConditionResult: LiveData<GetCmsResponse>
        get() = repository.termsAndConditionLiveData

    fun termsAndCondition(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.termsAndCondition(map)
        }
    }

    val privacyPolicyResult: LiveData<GetCmsResponse>
        get() = repository.privacyPolicyLiveData

    fun privacyPolicy(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.privacyPolicy(map)
        }
    }

    val aboutUsResult: LiveData<GetCmsResponse>
        get() = repository.aboutUsLiveData

    fun aboutUs(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.aboutUs(map)
        }
    }

    val returnPolicyResult: LiveData<GetCmsResponse>
        get() = repository.returnPolicyLiveData

    fun returnPolicy(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.returnPolicy(map)
        }
    }

    val exchangePolicyResult: LiveData<GetCmsResponse>
        get() = repository.exchangePolicyLiveData

    fun exchangePolicy(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.exchangePolicy(map)
        }
    }

    val cancellationPolicyResult: LiveData<GetCmsResponse>
        get() = repository.cancellationPolicyLiveData

    fun cancellationPolicy(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.cancellationPolicy(map)
        }
    }

    val notificationOnOffResult: LiveData<LoginResponse>
        get() = repository.notificationOnOffLiveData

    fun notificationOnOff(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.notificationOnOff(map)
        }
    }

    val appNotificationOnOffResult: LiveData<LoginResponse>
        get() = repository.appNotificationOnOffLiveData

    fun appNotificationOnOff(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.appNotificationOnOff(map)
        }
    }

    val notificationOnOffAffiliateResult: LiveData<GetAffiliateUserProfileResponse>
        get() = repository.notificationOnOffAffiliateLiveData

    fun notificationOnOffAffiliate(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.notificationOnOffAffiliate(map)
        }
    }

    val appNotificationOnOffAffiliateResult: LiveData<GetAffiliateUserProfileResponse>
        get() = repository.appNotificationOnOffAffiliateLiveData

    fun appNotificationOnOffAffiliate(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.appNotificationOnOffAffiliate(map)
        }
    }

    val getSavedCardResult: LiveData<GetSavedCardResponse>
        get() = repository.getSavedCardLiveData

    fun getSavedCard() {
        viewModelScope.launch {
            repository.getSavedCard()
        }
    }


    val addCardResult: LiveData<CommonResponse>
        get() = repository.addCardLiveData

    fun addCard(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addCard(map)
        }
    }
    val deleteCardResult: LiveData<CommonResponse>
        get() = repository.deleteCardLiveData

    fun deleteCard(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.deleteCard(map)
        }
    }

    val changeCurrencyResult: LiveData<CommonResponse>
        get() = repository.changeCurrencyLiveData

    fun changeCurrency(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.changeCurrency(map)
        }
    }

    val changeCountryResult: LiveData<CommonResponse>
        get() = repository.changeCountryLiveData

    fun changeCountry(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.changeCountry(map)
        }
    }

    val affiliateChangeCurrencyResult: LiveData<CommonResponse>
        get() = repository.affiliateChangeCurrencyLiveData

    fun affiliateChangeCurrency(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.affiliateChangeCurrency(map)
        }
    }

    val affiliateChangeCountryResult: LiveData<CommonResponse>
        get() = repository.affiliateChangeCountryLiveData

    fun affiliateChangeCountry(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.affiliateChangeCountry(map)
        }
    }

    val captureSellerStoreVisitorResult: LiveData<CommonResponse>
        get() = repository.captureSellerStoreVisitorLiveData

    fun captureSellerStoreVisitor(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.captureSellerStoreVisitor(map)
        }
    }

    val deleteAccountResult: LiveData<CommonResponse>
        get() = repository.deleteAccountLiveData

    fun deleteAccount(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.deleteAccount(map)
        }
    }

    val affiliateDeleteAccountResult: LiveData<CommonResponse>
        get() = repository.affiliateDeleteAccountLiveData

    fun affiliateDeleteAccount(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.affiliateDeleteAccount(map)
        }
    }

}
