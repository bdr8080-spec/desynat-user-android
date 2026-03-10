package com.desynatuser.data.repo


import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.desynatuser.network.ApiServices
import com.desynatuser.ui.affiliateUser.auth.response.AddAffiliateUserAddressResponse
import com.desynatuser.ui.affiliateUser.auth.response.AddAffiliateUserBankDetail
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddAddressRequest
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddBankDetailRequest
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserLoginResponse
import com.desynatuser.ui.affiliateUser.auth.response.CreateAffiliateUserResponse
import com.desynatuser.ui.affiliateUser.auth.response.CreateAffiliateUserRequest
import com.desynatuser.ui.affiliateUser.home.payment.response.GetAllAffiliateOrderResponse
import com.desynatuser.ui.affiliateUser.home.payment.response.GetWithdrawalRequestResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateAllProductResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateProductDetailResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateMyProductResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateProductLinkResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateMyProductDetailResponse
import com.desynatuser.ui.affiliateUser.home.profile.response.AddAnotherBankResponse
import com.desynatuser.ui.affiliateUser.home.profile.response.AffiliateChangeUserOtpVerifyResponse
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.ui.affiliateUser.home.profile.response.UpdateAffiliateProfileRequest
import com.desynatuser.ui.affiliateUser.home.profile.response.UpdateAffiliateProfileResponse
import com.desynatuser.ui.auth.response.FileUploadResponse
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.ui.auth.response.RegisterAccountResponse
import com.desynatuser.ui.auth.response.RegisterRequest
import com.desynatuser.ui.home.chat.response.AcceptCustomOrderResponse
import com.desynatuser.ui.home.chat.response.AcceptRejectQuotationRequestResponse
import com.desynatuser.ui.home.chat.response.ChatDetailResponse
import com.desynatuser.ui.home.chat.response.CreateCustomOrderResponse
import com.desynatuser.ui.home.chat.response.GetChatListResponse
import com.desynatuser.ui.home.chat.response.NormalChatResponse
import com.desynatuser.ui.home.fragment.response.GetHomeSearchResponse
import com.desynatuser.ui.home.fragment.response.GetRecentSearchListResponse
import com.desynatuser.ui.home.fragment.response.GetRecommendedResponse
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
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetSizeListResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreSortListResponse
import com.desynatuser.ui.home.store.response.GetWishlistResponse
import com.desynatuser.ui.home.store.response.PlaceOrderRequest
import com.desynatuser.ui.home.store.response.PlaceOrderResponse
import com.desynatuser.ui.response.CommonResponse
import com.desynatuser.ui.response.GetHomeScreenAllBannerResponse
import com.desynatuser.ui.response.GetHomeScreenPopUpBanner
import com.desynatuser.utils.toMultipartPart
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val api: ApiServices
) : BaseRepository(context) {

    val registerLiveData = SingleLiveEvent<RegisterAccountResponse>()
    suspend fun register(
       request:RegisterRequest
    ) {
        handleApiCall(
            api = {
                api.register(request)
            },
            responseLiveData = registerLiveData
        )
    }

    val responseUploadImage = SingleLiveEvent<FileUploadResponse>()
    suspend fun uploadImage(
        imageFile: File? = null
    ) {
        handleApiCall(
            api = {
                api.uploadImage(imageFile?.toMultipartPart("image")
                )
            },
            responseLiveData = responseUploadImage
        )
    }

    val loginLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun login(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.login(map)
            },
            responseLiveData = loginLiveData
        )
    }

 val loginEmailMobileLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun loginViaEmailOrMobile(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.loginViaEmailOrMobile(map)
            },
            responseLiveData = loginEmailMobileLiveData
        )
    }

    val otpVerifyLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun otpVerification(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.otpVerification(map)
            },
            responseLiveData = otpVerifyLiveData
        )
    }

    val resendOtpLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun resendOtp(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.resendOtp(map)
            },
            responseLiveData = resendOtpLiveData
        )
    }

    val forgotPasswordLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun forgotPassword(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.forgotPassword(map)
            },
            responseLiveData = forgotPasswordLiveData
        )
    }

    val createPasswordLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun createPassword(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.createPassword(map)
            },
            responseLiveData = createPasswordLiveData
        )
    }

val changePasswordLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun changePassword(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.changePassword(map)
            },
            responseLiveData = changePasswordLiveData
        )
    }


    val dashBoardLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun getDashBoard(
    ) {
        handleApiCall(
            api = {
                api.getDashBoard()
            },
            responseLiveData = dashBoardLiveData
        )
    }

    val logoutLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun logout(
    ) {
        handleApiCall(
            api = {
                api.logout()
            },
            responseLiveData = logoutLiveData
        )
    }

    val countryLiveData = SingleLiveEvent<GetCountryResponse>()
    suspend fun getCountry(
    ) {
        handleApiCall(
            api = {
                api.getCountry()
            },
            responseLiveData = countryLiveData
        )
    }

    val getSelectedCountryLiveData = SingleLiveEvent<GetCountryResponse>()
    suspend fun getSelectedCountry(
    ) {
        handleApiCall(
            api = {
                api.getSelectedCountry()
            },
            responseLiveData = getSelectedCountryLiveData
        )
    }

    val categoryLiveData = SingleLiveEvent<GetCategoryResponse>()
    suspend fun getCategory(
    ) {
        handleApiCall(
            api = {
                api.getCategory()
            },
            responseLiveData = categoryLiveData
        )
    }

    val subCategoryLiveData = SingleLiveEvent<GetSubCategoryResponse>()
    suspend fun getSubCategory(categoryId:String?=null,search:String?=null
    ) {
        handleApiCall(
            api = {
                api.getSubCategory(categoryId = categoryId,search)
            },
            responseLiveData = subCategoryLiveData
        )
    }


    val updateProfileLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun updateProfile(
        map: HashMap<String,String>
    ) {
        handleApiCall(
            api = {
                api.updateProfile(map)
            },
            responseLiveData = updateProfileLiveData
        )
    }

    val getProfileLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun getProfile(
    ) {
        handleApiCall(
            api = {
                api.getProfile()
            },
            responseLiveData = getProfileLiveData
        )
    }


    val changeNumberLiveData = SingleLiveEvent<ChangeMobileNumbeResponse>()
    suspend fun changeNumber(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.changeNumber(map)
            },
            responseLiveData = changeNumberLiveData
        )
    }
    val userOtpLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun userOtp(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.userOtp(map)
            },
            responseLiveData = userOtpLiveData
        )
    }
    val addAddressLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addAddress(
        request: AddAddressRequest
    ) {
        handleApiCall(
            api = {
                api.addAddress(request)
            },
            responseLiveData = addAddressLiveData
        )
    }

    val editAddressLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun editAddress(
        request: AddAddressRequest
    ) {
        handleApiCall(
            api = {
                api.editAddress(request)
            },
            responseLiveData = editAddressLiveData
        )
    }

  val defaultAddressLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun setDefaultAddress(
       map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.setDefaultAddress(map)
            },
            responseLiveData = defaultAddressLiveData
        )
    }

    val deleteAddressLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteAddress(
        id:String
    ) {
        handleApiCall(
            api = {
                api.deleteAddress(id)
            },
            responseLiveData = deleteAddressLiveData
        )
    }

    val addressLiveData = SingleLiveEvent<GetAddressResponse>()
    suspend fun getAddress()
    {
        handleApiCall(
            api = {
                api.getAddress()
            },
            responseLiveData = addressLiveData
        )
    }

    val storeListLiveData = SingleLiveEvent<GetStoreResponse>()
    suspend fun getStoreList(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getStoreList(map)
            },
            responseLiveData = storeListLiveData
        )
    }

    val storeDetailLiveData = SingleLiveEvent<GetSingleStoreResponse>()
    suspend fun getSingleStoreInfo(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getSingleStoreInfo(map)
            },
            responseLiveData = storeDetailLiveData
        )
    }

    val productDetailLiveData = SingleLiveEvent<GetProductDetailResponse>()
    suspend fun getProductDetailInfo(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getProductDetailInfo(map)
            },
            responseLiveData = productDetailLiveData
        )
    }

    val addToCartLiveData = SingleLiveEvent<AddToCartResponse>()
    suspend fun addToCart(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.addToCart(map)
            },
            responseLiveData = addToCartLiveData
        )
    }

    val cartQunatityLiveData = SingleLiveEvent<CartQunatityIncrementDecrementResponse>()
    suspend fun addCartQuantity(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.addCartQuantity(map)
            },
            responseLiveData = cartQunatityLiveData
        )
    }

    val cartDetailLiveData = SingleLiveEvent<GetCartResponse>()
    suspend fun viewCartDetail()
    {
        handleApiCall(
            api = {
                api.viewCartDetail()
            },
            responseLiveData = cartDetailLiveData
        )
    }

    val cartHomeDetailLiveData = SingleLiveEvent<GetCartResponse>()
    suspend fun viewHomeCartDetail()
    {
        handleApiCall(
            api = {
                api.viewCartDetail()
            },
            responseLiveData = cartHomeDetailLiveData
        )
    }

    val addToWishlistLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addToWishlist(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.addToWishlist(map)
            },
            responseLiveData = addToWishlistLiveData
        )
    }

    val storeSortListLiveData = SingleLiveEvent<GetStoreSortListResponse>()
    suspend fun getStoreSorList()
    {
        handleApiCall(
            api = {
                api.getStoreSorList()
            },
            responseLiveData = storeSortListLiveData
        )
    }

    val wishListLiveData = SingleLiveEvent<GetWishlistResponse>()
    suspend fun getWishList()
    {
        handleApiCall(
            api = {
                api.getWishList()
            },
            responseLiveData = wishListLiveData
        )
    }

    val searchLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun getSearch()
    {
        handleApiCall(
            api = {
                api.getSearch()
            },
            responseLiveData = searchLiveData
        )
    }

    val colorListLiveData = SingleLiveEvent<GetAllColorListResponse>()
    suspend fun getColorList()
    {
        handleApiCall(
            api = {
                api.getColorList()
            },
            responseLiveData = colorListLiveData
        )
    }

    val brandLiveData = SingleLiveEvent<GetAllBrandListResponse>()
    suspend fun getBrandList()
    {
        handleApiCall(
            api = {
                api.getBrandList()
            },
            responseLiveData = brandLiveData
        )
    }

  val sizeLiveData = SingleLiveEvent<GetSizeListResponse>()
    suspend fun getSizeList()
    {
        handleApiCall(
            api = {
                api.getSizeList()
            },
            responseLiveData = sizeLiveData
        )
    }

  val giftCardLiveData = SingleLiveEvent<GetAllGiftCardResponse>()
    suspend fun getGiftCardList(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getGiftCardList(map)
            },
            responseLiveData = giftCardLiveData
        )
    }

  val giftCardOccasionLiveData = SingleLiveEvent<GetGiftCardOccasionTypeResponse>()
    suspend fun getGiftCardOccasion()
    {
        handleApiCall(
            api = {
                api.getGiftCardOccasion()
            },
            responseLiveData = giftCardOccasionLiveData
        )
    }

 val buyGiftCardLiveData = SingleLiveEvent<BuyGiftCardResponse>()
    suspend fun buyGiftCard(request:AddGiftCardRequest)
    {
        handleApiCall(
            api = {
                api.buyGiftCard(request)
            },
            responseLiveData = buyGiftCardLiveData
        )
    }

 val buyGiftCardPaymentLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun buyGiftCardPayment(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.buyGiftCardPayment(map)
            },
            responseLiveData = buyGiftCardPaymentLiveData
        )
    }

 val applyGiftCardLiveData = SingleLiveEvent<ApplyGiftCardResponse>()
    suspend fun applyGiftCard(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.applyGiftCard(map)
            },
            responseLiveData = applyGiftCardLiveData
        )
    }

  val promoCodeLiveData = SingleLiveEvent<GetPromoCodeResponse>()
    suspend fun getPromoCode(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getPromoCode(map)
            },
            responseLiveData = promoCodeLiveData
        )
    }


  val applyPromoCodeLiveData = SingleLiveEvent<ApplyPromoCodeResponse>()
    suspend fun applyPromoCode(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.applyPromoCode(map)
            },
            responseLiveData = applyPromoCodeLiveData
        )
    }

  val placeOrderLiveData = SingleLiveEvent<PlaceOrderResponse>()
    suspend fun placeOrder(request: PlaceOrderRequest)
    {
        handleApiCall(
            api = {
                api.placeOrder(request)
            },
            responseLiveData = placeOrderLiveData
        )
    }

  val orderPaymentLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun orderPayment(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.orderPayment(map)
            },
            responseLiveData = orderPaymentLiveData
        )
    }

  val orderDetailLiveData = SingleLiveEvent<GetSingleOrderSummaryResponse>()
    suspend fun getOrderDetailInfo(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getSingleOrder(map)
            },
            responseLiveData = orderDetailLiveData
        )
    }

  val getReasonListLiveData = SingleLiveEvent<GetReasonListResponse>()
    suspend fun getReasonList(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getReasonList(map)
            },
            responseLiveData = getReasonListLiveData
        )
    }

  val cancelOrderLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun cancelOrder(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.cancelOrder(map)
            },
            responseLiveData = cancelOrderLiveData
        )
    }

 val returnExchangeOrderLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun returnExchangeOrder(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.returnExchangeOrder(map)
            },
            responseLiveData = returnExchangeOrderLiveData
        )
    }

 val orderRatingLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun orderRating(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.orderRating(map)
            },
            responseLiveData = orderRatingLiveData
        )
    }

  val orderListLiveData = SingleLiveEvent<GetAllOrderResponse>()
    suspend fun getAllOrder(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getAllOrder(map)
            },
            responseLiveData = orderListLiveData
        )
    }

  val walletBalanceLiveData = SingleLiveEvent<WalletBalanceResponse>()
    suspend fun getWalletBalance()
    {
        handleApiCall(
            api = {
                api.getWalletBalance()
            },
            responseLiveData = walletBalanceLiveData
        )
    }


  val getRecentSearchListLiveData = SingleLiveEvent<GetRecentSearchListResponse>()
    suspend fun getRecentSearchList()
    {
        handleApiCall(
            api = {
                api.getRecentSearchList()
            },
            responseLiveData = getRecentSearchListLiveData
        )
    }


  val getLatestSearchListLiveData = SingleLiveEvent<GetRecentSearchListResponse>()
    suspend fun getLatestSearchList()
    {
        handleApiCall(
            api = {
                api.getLatestSearchList()
            },
            responseLiveData = getLatestSearchListLiveData
        )
    }

  val getRecommendedSearchListLiveData = SingleLiveEvent<GetRecommendedResponse>()
    suspend fun getRecommendedSearchList()
    {
        handleApiCall(
            api = {
                api.getRecommendedSearchList()
            },
            responseLiveData = getRecommendedSearchListLiveData
        )
    }

  val homePageSearchLiveData = SingleLiveEvent<GetHomeSearchResponse>()
    suspend fun homePageSearch(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.homePageSearch(map)
            },
            responseLiveData = homePageSearchLiveData
        )
    }

    val detectImageLiveData = SingleLiveEvent<VisionResponse>()
    suspend fun detectImage(key:String,request: VisionRequestBody)
    {
        handleApiCall(
            api = {
                api.detectImage(key,request)
            },
            responseLiveData = detectImageLiveData
        )
    }

    val invoiceLiveData = SingleLiveEvent<GetInvoiceResponse>()
    suspend fun getInvoice(id:String)
    {
        handleApiCall(
            api = {
                api.getInvoice(id)
            },
            responseLiveData = invoiceLiveData
        )
    }

    val createCustomOrderLiveData = SingleLiveEvent<CreateCustomOrderResponse>()
    suspend fun createCustomOrder(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.createCustomOrder(map)
            },
            responseLiveData = createCustomOrderLiveData
        )
    }

val sendCustomOrderRequestLiveData = SingleLiveEvent<CreateCustomOrderResponse>()
    suspend fun sendCustomOrderRequest(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.sendCustomOrderRequest(map)
            },
            responseLiveData = sendCustomOrderRequestLiveData
        )
    }

    val applyCustomOrderGiftCardLiveData = SingleLiveEvent<AcceptCustomOrderResponse>()
    suspend fun applyCustomOrderGiftCard(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.applyCustomOrderGiftCard(map)
            },
            responseLiveData = applyCustomOrderGiftCardLiveData
        )
    }

    val customOrderPaymentLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun customOrderPayment(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.customOrderPayment(map)
            },
            responseLiveData = customOrderPaymentLiveData
        )
    }

    val getAllChatListLiveData = SingleLiveEvent<GetChatListResponse>()
    suspend fun getAllChatList(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getAllChatList(map)
            },
            responseLiveData = getAllChatListLiveData
        )
    }

    val chatDetailLiveData = SingleLiveEvent<ChatDetailResponse>()
    suspend fun chatDetail(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.chatDetail(map)
            },
            responseLiveData = chatDetailLiveData
        )
    }

    val sendMessageLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun sendMessage(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.sendMessage(map)
            },
            responseLiveData = sendMessageLiveData
        )
    }

    val chatWithSellerLiveData = SingleLiveEvent<NormalChatResponse>()
    suspend fun chatWithSeller(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.chatWithSeller(map)
            },
            responseLiveData = chatWithSellerLiveData
        )
    }

    val acceptRejectQuotationLiveData = SingleLiveEvent<AcceptRejectQuotationRequestResponse>()
    suspend fun acceptRejectQuotation(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.acceptRejectQuotation(map)
            },
            responseLiveData = acceptRejectQuotationLiveData
        )
    }

    val getHomeScreenBannerLiveData = SingleLiveEvent<GetHomeScreenPopUpBanner>()
    suspend fun getHomeScreenBanner()
    {
        handleApiCall(
            api = {
                api.getHomeScreenBanner()
            },
            responseLiveData = getHomeScreenBannerLiveData
        )
    }

    val getHomeScreenAllBannerLiveData = SingleLiveEvent<GetHomeScreenAllBannerResponse>()
    suspend fun getHomeScreenAllBanner(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getHomeScreenAllBanner(map)
            },
            responseLiveData = getHomeScreenAllBannerLiveData
        )
    }

    val getHomeScreenBottomBannerLiveData = SingleLiveEvent<GetHomeScreenAllBannerResponse>()
    suspend fun getHomeScreenBottomBanner(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getHomeScreenBottomBanner(map)
            },
            responseLiveData = getHomeScreenBottomBannerLiveData
        )
    }

    val getHomeScreenCategoryBannerLiveData = SingleLiveEvent<GetHomeScreenAllBannerResponse>()
    suspend fun getHomeScreenCategoryBanner(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getHomeScreenCategoryBanner(map)
            },
            responseLiveData = getHomeScreenCategoryBannerLiveData
        )
    }

    val addMoneyInWalletLiveData = SingleLiveEvent<AddMoneyInWalletResponse>()
    suspend fun addMoneyInWallet(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.addMoneyInWallet(map)
            },
            responseLiveData = addMoneyInWalletLiveData
        )
    }

    val addMoneyInWalletPaymentLiveData = SingleLiveEvent<CompletePyamentWalletResponse>()
    suspend fun addMoneyInWalletPayment(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.addMoneyInWalletPayment(map)
            },
            responseLiveData = addMoneyInWalletPaymentLiveData
        )
    }

    val redeemGiftCardInWalletLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun redeemGiftCardInWallet(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.redeemGiftCardInWallet(map)
            },
            responseLiveData = redeemGiftCardInWalletLiveData
        )
    }

    val getWalletHistoryLiveData = SingleLiveEvent<GetWalletHistoryResponse>()
    suspend fun getWalletHistory()
    {
        handleApiCall(
            api = {
                api.getWalletHistory()
            },
            responseLiveData = getWalletHistoryLiveData
        )
    }

    val addToCartCustomOrderLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addToCartCustomOrder(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.addToCartCustomOrder(map)
            },
            responseLiveData = addToCartCustomOrderLiveData
        )
    }

    val removeFromCartCustomOrderLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun removeFromCartCustomOrder(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.removeFromCartCustomOrder(map)
            },
            responseLiveData = removeFromCartCustomOrderLiveData
        )
    }

    //Affiliate User

    val createAffiliateUserLiveData = SingleLiveEvent<CreateAffiliateUserResponse>()
    suspend fun createAffiliateUser(
        request:CreateAffiliateUserRequest
    ) {
        handleApiCall(
            api = {
                api.createAffiliateUser(request)
            },
            responseLiveData = createAffiliateUserLiveData
        )
    }

    val addAddressAffiliateUserLiveData = SingleLiveEvent<AddAffiliateUserAddressResponse>()
    suspend fun addAffiliateUserAddress(
        request:AffiliateUserAddAddressRequest
    ) {
        handleApiCall(
            api = {
                api.addAffiliateUserAddress(request)
            },
            responseLiveData = addAddressAffiliateUserLiveData
        )
    }

    val bankDetailAffiliateUserLiveData = SingleLiveEvent<AddAffiliateUserBankDetail>()
    suspend fun addAffiliateUserBankDetail(
        request:AffiliateUserAddBankDetailRequest
    ) {
        handleApiCall(
            api = {
                api.addAffiliateUserBankDetail(request)
            },
            responseLiveData = bankDetailAffiliateUserLiveData
        )
    }

    val otpVerifyAffiliateUserLiveData = SingleLiveEvent<AffiliateUserLoginResponse>()
    suspend fun verifyAffiliateUserOtp(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.verifyAffiliateUserOtp(map)
            },
            responseLiveData = otpVerifyAffiliateUserLiveData
        )
    }

    val resendOtpAffiliateUserLiveData = SingleLiveEvent<AffiliateUserLoginResponse>()
    suspend fun resendOtpAffiliateUser(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.resendOtpAffiliateUser(map)
            },
            responseLiveData = resendOtpAffiliateUserLiveData
        )
    }

    val forgotPasswordAffiliateUserLiveData = SingleLiveEvent<AffiliateUserLoginResponse>()
    suspend fun forgotPasswordAffiliateUser(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.forgotPasswordAffiliateUser(map)
            },
            responseLiveData = forgotPasswordAffiliateUserLiveData
        )
    }

    val createPasswordAffiliateUserLiveData = SingleLiveEvent<AffiliateUserLoginResponse>()
    suspend fun createAffiliateUserPassword(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.createAffiliateUserPassword(map)
            },
            responseLiveData = createPasswordAffiliateUserLiveData
        )
    }

    val reVerifyDocumentLiveData = SingleLiveEvent<AffiliateUserLoginResponse>()
    suspend fun reVerifyDocument(
        request: CreateAffiliateUserRequest
    ) {
        handleApiCall(
            api = {
                api.reVerifyDocument(request)
            },
            responseLiveData = reVerifyDocumentLiveData
        )
    }

    val loginAffiliateUserLiveData = SingleLiveEvent<AffiliateUserLoginResponse>()
    suspend fun loginAffiliateUser(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.loginAffiliateUser(map)
            },
            responseLiveData = loginAffiliateUserLiveData
        )
    }

val loginWithOtpAffiliateUserLiveData = SingleLiveEvent<AffiliateUserLoginResponse>()
    suspend fun loginWithOtpAffiliateUser(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.loginWithOtpAffiliateUser(map)
            },
            responseLiveData = loginWithOtpAffiliateUserLiveData
        )
    }

val getAffiliateProfileLiveData = SingleLiveEvent<GetAffiliateUserProfileResponse>()
    suspend fun getAffiliateProfile(
    ) {
        handleApiCall(
            api = {
                api.getAffiliateProfile()
            },
            responseLiveData = getAffiliateProfileLiveData
        )
    }

    val updateAffiliateUserProfileLiveData = SingleLiveEvent<UpdateAffiliateProfileResponse>()
    suspend fun updateAffiliateUserProfile(
       request: UpdateAffiliateProfileRequest
    ) {
        handleApiCall(
            api = {
                api.updateAffiliateUserProfile(request)
            },
            responseLiveData = updateAffiliateUserProfileLiveData
        )
    }

    val updateAffiliateUserAddressLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun updateAffiliateUserAddress(
        request : AffiliateUserAddAddressRequest
    ) {
        handleApiCall(
            api = {
                api.updateAffiliateUserAddress(request)
            },
            responseLiveData = updateAffiliateUserAddressLiveData
        )
    }

    val updateAffiliateUserBankLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun updateAffiliateUserBank(
        request: AffiliateUserAddBankDetailRequest
    ) {
        handleApiCall(
            api = {
                api.updateAffiliateUserBank(request)
            },
            responseLiveData = updateAffiliateUserBankLiveData
        )
    }

    val addAffiliateUserBankLiveData = SingleLiveEvent<AddAnotherBankResponse>()
    suspend fun addAffiliateUserBank(
        request: AffiliateUserAddBankDetailRequest
    ) {
        handleApiCall(
            api = {
                api.addAffiliateUserBank(request)
            },
            responseLiveData = addAffiliateUserBankLiveData
        )
    }

    val updateAffiliateUserPasswordLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun updateAffiliateUserPassword(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.updateAffiliateUserPassword(map)
            },
            responseLiveData = updateAffiliateUserPasswordLiveData
        )
    }

    val defaultBankLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun defaultBank(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.defaultBank(map)
            },
            responseLiveData = defaultBankLiveData
        )
    }

    val deleteBankLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteBank(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.deleteBank(map)
            },
            responseLiveData = deleteBankLiveData
        )
    }
    val getAffiliateAllProductLiveData = SingleLiveEvent<GetAffiliateAllProductResponse>()
    suspend fun getAffiliateAllProduct(map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getAffiliateAllProduct(map)
            },
            responseLiveData = getAffiliateAllProductLiveData
        )
    }

 val getAffiliateProductDetailLiveData = SingleLiveEvent<GetAffiliateProductDetailResponse>()
    suspend fun getAffiliateProductDetail(map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getAffiliateProductDetail(map)
            },
            responseLiveData = getAffiliateProductDetailLiveData
        )
    }

 val getAffiliateMyProductDetailLiveData = SingleLiveEvent<GetAffiliateMyProductDetailResponse>()
    suspend fun getAffiliateMyProductDetail(map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getAffiliateMyProductDetail(map)
            },
            responseLiveData = getAffiliateMyProductDetailLiveData
        )
    }

 val removeAffiliateProductLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun removeAffiliateProduct(map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.removeAffiliateProduct(map)
            },
            responseLiveData = removeAffiliateProductLiveData
        )
    }

val addAffiliateProductLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addAffiliateProduct(map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.addAffiliateProduct(map)
            },
            responseLiveData = addAffiliateProductLiveData
        )
    }


val getAffiliateProductLinkLiveData = SingleLiveEvent<GetAffiliateProductLinkResponse>()
    suspend fun getAffiliateProductLink(map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getAffiliateProductLink(map)
            },
            responseLiveData = getAffiliateProductLinkLiveData
        )
    }


    val getAffiliateMyProductLiveData = SingleLiveEvent<GetAffiliateMyProductResponse>()
    suspend fun getAffiliateMyProduct(map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getAffiliateMyProduct(map)
            },
            responseLiveData = getAffiliateMyProductLiveData
        )
    }

    val getAffiliateSorListLiveData = SingleLiveEvent<GetStoreSortListResponse>()
    suspend fun getAffiliateSorList()
    {
        handleApiCall(
            api = {
                api.getAffiliateSorList()
            },
            responseLiveData = getAffiliateSorListLiveData
        )
    }
    val affiliateProductSearchLiveData = SingleLiveEvent<GetAffiliateAllProductResponse>()
    suspend fun affiliateProductSearch(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.affiliateProductSearch(map)
            },
            responseLiveData = affiliateProductSearchLiveData
        )
    }

    val getAffiliateRecentSearchListLiveData = SingleLiveEvent<GetRecentSearchListResponse>()
    suspend fun getAffiliateRecentSearchList()
    {
        handleApiCall(
            api = {
                api.getAffiliateRecentSearchList()
            },
            responseLiveData = getAffiliateRecentSearchListLiveData
        )
    }


    val getAffiliateLatestSearchListLiveData = SingleLiveEvent<GetRecentSearchListResponse>()
    suspend fun getAffiliateLatestSearchList()
    {
        handleApiCall(
            api = {
                api.getAffiliateLatestSearchList()
            },
            responseLiveData = getAffiliateLatestSearchListLiveData
        )
    }

    val changeAffiliateNumberLiveData = SingleLiveEvent<ChangeMobileNumbeResponse>()
    suspend fun changeAffiliateNumber(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.changeAffiliateNumber(map)
            },
            responseLiveData = changeAffiliateNumberLiveData
        )
    }

    val affiliateUserOtpLiveData = SingleLiveEvent<AffiliateChangeUserOtpVerifyResponse>()
    suspend fun affiliateUserOtp(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.affiliateUserOtp(map)
            },
            responseLiveData = affiliateUserOtpLiveData
        )
    }

    val getAffiliatePaymentLiveData = SingleLiveEvent<GetAllAffiliateOrderResponse>()
    suspend fun getAffiliatePayment()
    {
        handleApiCall(
            api = {
                api.getAffiliatePayment()
            },
            responseLiveData = getAffiliatePaymentLiveData
        )
    }

    val getWithdrawalRequestLiveData = SingleLiveEvent<GetWithdrawalRequestResponse>()
    suspend fun getWithdrawalRequest(map: HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getWithdrawalRequest(map)
            },
            responseLiveData = getWithdrawalRequestLiveData
        )
    }

    val withdrawAffiliateAmountLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun withdrawAffiliateAmount(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.withdrawAffiliateAmount(map)
            },
            responseLiveData = withdrawAffiliateAmountLiveData
        )
    }

}
class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
                // Clear the value after it has been delivered
                // value = null
            }
        }
    }

    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    fun call() {
        value = null
    }

    fun clear() {
        value = null
        pending.set(false)
    }
}

