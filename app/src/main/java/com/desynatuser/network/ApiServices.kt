package com.desynatuser.network

import com.desynatuser.ui.affiliateUser.auth.response.AddAffiliateUserAddressResponse
import com.desynatuser.ui.affiliateUser.auth.response.AddAffiliateUserBankDetail
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddAddressRequest
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserAddBankDetailRequest
import com.desynatuser.ui.affiliateUser.auth.response.AffiliateUserLoginResponse
import com.desynatuser.ui.affiliateUser.auth.response.CreateAffiliateUserResponse
import com.desynatuser.ui.affiliateUser.auth.response.CreateAffiliateUserRequest
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateAllProductResponse
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateProductDetailResponse
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
import com.desynatuser.utils.Constants
import com.desynatuser.utils.translation.TranslateRequest
import com.desynatuser.utils.translation.TranslateResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiServices {

    @Multipart
    @POST(Constants.UPLOAD)
    suspend fun uploadImage(
        @Part file: MultipartBody.Part? = null
    ): Response<FileUploadResponse>

    @POST(Constants.REGISTER)
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterAccountResponse>

    @POST(Constants.LOGIN)
    suspend fun login(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.LOGIN_VIA_EMAIL_OR_MOBILE)
    suspend fun loginViaEmailOrMobile(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.OTP_VERIFY)
    suspend fun otpVerification(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.RESEND_OTP)
    suspend fun resendOtp(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.FORGOT_PASSWORD)
    suspend fun forgotPassword(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.CREATE_PASSWORD)
    suspend fun createPassword(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Body body: HashMap<String, Any>
    ): Response<CommonResponse>

    @GET(Constants.DASHBOARD)
    suspend fun getDashBoard(
    ): Response<LoginResponse>

    @GET(Constants.LOGOUT)
    suspend fun logout(
    ): Response<CommonResponse>

    @GET(Constants.GET_COUNTRY)
    suspend fun getCountry(): Response<GetCountryResponse>

    @GET(Constants.GET_SELECTED_COUNTRY)
    suspend fun getSelectedCountry(): Response<GetCountryResponse>

    @GET(Constants.GET_CATEGORY)
    suspend fun getCategory(): Response<GetCategoryResponse>

    @GET(Constants.GET_SUB_CATEGORY)
    suspend fun getSubCategory(
        @Query("category_id") categoryId: String,
        @Query("search") search: String? = null
    ): Response<GetSubCategoryResponse>

    @POST(Constants.UPDATE_PROFILE)
    suspend fun updateProfile(
        @Body body: HashMap<String, String>
    ): Response<LoginResponse>

    @GET(Constants.VIEW_PROFILE)
    suspend fun getProfile(
    ): Response<LoginResponse>

    @POST(Constants.CHANGE_NUMBER)
    suspend fun changeNumber(
        @Body body: HashMap<String, Any>
    ): Response<ChangeMobileNumbeResponse>


    @POST(Constants.USER_OTP)
    suspend fun userOtp(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.STORE_LIST)
    suspend fun getStoreList(
        @Body body: HashMap<String, Any>
    ): Response<GetStoreResponse>

    @POST(Constants.SINGLE_STORE)
    suspend fun getSingleStoreInfo(
        @Body map: HashMap<String, Any>,
    ): Response<GetSingleStoreResponse>

    @POST(Constants.GET_PRODUCT_DETAIL)
    suspend fun getProductDetailInfo(
        @Body map: HashMap<String, Any>,
    ): Response<GetProductDetailResponse>

    @POST(Constants.ADD_TO_CART)
    suspend fun addToCart(
        @Body map: HashMap<String, Any>,
    ): Response<AddToCartResponse>

    @POST(Constants.ADD_TO_CART_QUANTITY)
    suspend fun addCartQuantity(
        @Body map: HashMap<String, Any>,
    ): Response<CartQunatityIncrementDecrementResponse>

    @GET(Constants.VIEW_CART_DETAIL)
    suspend fun viewCartDetail(): Response<GetCartResponse>

    @POST(Constants.ADD_TO_WISHLIST)
    suspend fun addToWishlist(
        @Body map: HashMap<String, Any>,
    ): Response<CommonResponse>

    @GET(Constants.GET_ADDRESS)
    suspend fun getAddress(): Response<GetAddressResponse>

    @POST(Constants.SAVE_ADDRESS)
    suspend fun addAddress(
        @Body request: AddAddressRequest
    ): Response<CommonResponse>

    @PUT(Constants.EDIT_ADDRESS)
    suspend fun editAddress(
        @Body request: AddAddressRequest
    ): Response<CommonResponse>

    @POST(Constants.DEFAULt_ADDRESS)
    suspend fun setDefaultAddress(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    @DELETE("${Constants.DELETE_ADDRESS}/{id}")
    suspend fun deleteAddress(
        @Path("id") id: String
    ): Response<CommonResponse>

    @GET(Constants.GET_STORE_SORT_LIST)
    suspend fun getStoreSorList(): Response<GetStoreSortListResponse>

    @GET(Constants.GET_WISHLIST)
    suspend fun getWishList(): Response<GetWishlistResponse>

    @GET(Constants.GET_SEARCH)
    suspend fun getSearch(): Response<CommonResponse>

    @GET(Constants.GET_COLOR_LIST)
    suspend fun getColorList(): Response<GetAllColorListResponse>

    @GET(Constants.GET_BRAND_LIST)
    suspend fun getBrandList(): Response<GetAllBrandListResponse>

    @GET(Constants.GET_SIZE_LIST)
    suspend fun getSizeList(): Response<GetSizeListResponse>


    @POST(Constants.GIFT_CARD_LIST)
    suspend fun getGiftCardList(@Body map: HashMap<String, Any>): Response<GetAllGiftCardResponse>

    @GET(Constants.GIFT_CARD_OCCASION)
    suspend fun getGiftCardOccasion(): Response<GetGiftCardOccasionTypeResponse>

    @POST(Constants.BUY_GIFT_CARD)
    suspend fun buyGiftCard(
        @Body request: AddGiftCardRequest
    ): Response<BuyGiftCardResponse>

    @POST(Constants.BUY_GIFT_CARD_PAYMENT)
    suspend fun buyGiftCardPayment(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    @POST(Constants.APPLY_GIFT_CARD)
    suspend fun applyGiftCard(@Body map: HashMap<String, Any>): Response<ApplyGiftCardResponse>

    @POST(Constants.PROMO_CODE_LIST)
    suspend fun getPromoCode(@Body map: HashMap<String, Any>): Response<GetPromoCodeResponse>

    @POST(Constants.APPLY_PROMO_CODE)
    suspend fun applyPromoCode(@Body map: HashMap<String, Any>): Response<ApplyPromoCodeResponse>

    @POST(Constants.PLACE_ORDER)
    suspend fun placeOrder(@Body request: PlaceOrderRequest): Response<PlaceOrderResponse>

    @POST(Constants.COMPLETE_ORDER_PAYMENT)
    suspend fun orderPayment(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.GET_ALL_ORDER)
    suspend fun getAllOrder(@Body map: HashMap<String, Any>): Response<GetAllOrderResponse>

    @POST(Constants.GET_ORDER_SUMMARY)
    suspend fun getSingleOrder(@Body map: HashMap<String, Any>): Response<GetSingleOrderSummaryResponse>

    @POST(Constants.GET_REASON)
    suspend fun getReasonList(@Body map: HashMap<String, Any>): Response<GetReasonListResponse>

    @POST(Constants.CANCEL_ORDER)
    suspend fun cancelOrder(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.RETURN_EXCHANGE_ORDER)
    suspend fun returnExchangeOrder(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.ORDER_RATING)
    suspend fun orderRating(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @GET(Constants.RECENT_SEARCH_LIST)
    suspend fun getRecentSearchList(): Response<GetRecentSearchListResponse>

    @GET(Constants.LATEST_SEARCH_LIST)
    suspend fun getLatestSearchList(): Response<GetRecentSearchListResponse>

    @GET(Constants.RECOMMENDED_SEARCH_LIST)
    suspend fun getRecommendedSearchList(): Response<GetRecommendedResponse>

    @POST(Constants.HOME_PAGE_SEARCH)
    suspend fun homePageSearch(@Body map: HashMap<String, Any>): Response<GetHomeSearchResponse>

    @GET(Constants.WALLET_BALANCE)
    suspend fun getWalletBalance(): Response<WalletBalanceResponse>

    @POST("${Constants.INVOICE}")
    suspend fun getInvoice(@Query("order_id") orderId: String): Response<GetInvoiceResponse>

    @POST("https://vision.googleapis.com/v1/images:annotate")
    suspend fun detectImage(
        @Query("key") apiKey: String,
        @Body request: VisionRequestBody
    ): Response<VisionResponse>

    @POST("https://translation.googleapis.com/language/translate/v2")
    suspend fun googleTranslator(
        @Query("key") apiKey: String,
        @Body request: TranslateRequest
    ): Response<TranslateResponse>

    @POST(Constants.CREATE_CUSTOM_ORDER)
    suspend fun createCustomOrder(
        @Body map: HashMap<String, Any>
    ): Response<CreateCustomOrderResponse>

    @POST(Constants.SEND_CUSTOM_ORDER_REQUEST)
    suspend fun sendCustomOrderRequest(
        @Body map: HashMap<String, Any>
    ): Response<CreateCustomOrderResponse>

    @POST(Constants.APPLY_CUSTOM_ORDER_GIFT_CARD)
    suspend fun applyCustomOrderGiftCard(
        @Body map: HashMap<String, Any>
    ): Response<AcceptCustomOrderResponse>

    @POST(Constants.CUSTOM_ORDER_COMPLETE_PAYMENT)
    suspend fun customOrderPayment(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    @POST(Constants.USER_ALL_CHAT)
    suspend fun getAllChatList(
        @Body map: HashMap<String, Any>
    ): Response<GetChatListResponse>

    @POST(Constants.USER_SINGLE_CHAT)
    suspend fun chatDetail(
        @Body map: HashMap<String, Any>
    ): Response<ChatDetailResponse>

    @POST(Constants.SEND_MESSAGE)
    suspend fun sendMessage(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    @POST(Constants.CHAT_WITH_SELLER)
    suspend fun chatWithSeller(
        @Body map: HashMap<String, Any>
    ): Response<NormalChatResponse>

    @POST(Constants.ACCEPT_REJECT_ORDER_REQUEST)
    suspend fun acceptRejectQuotation(
        @Body map: HashMap<String, Any>
    ): Response<AcceptRejectQuotationRequestResponse>

    @POST(Constants.HOME_SCREEN_BANNER)
    suspend fun getHomeScreenBanner(
    ): Response<GetHomeScreenPopUpBanner>

    @POST(Constants.HOME_SCREEN_ALL_BANNER)
    suspend fun getHomeScreenAllBanner(
        @Body map: HashMap<String, Any>
    ): Response<GetHomeScreenAllBannerResponse>

    @POST(Constants.HOME_SCREEN_ALL_BANNER)
    suspend fun getHomeScreenBottomBanner(
        @Body map: HashMap<String, Any>
    ): Response<GetHomeScreenAllBannerResponse>

    @POST(Constants.HOME_SCREEN_ALL_BANNER)
    suspend fun getHomeScreenCategoryBanner(
        @Body map: HashMap<String, Any>
    ): Response<GetHomeScreenAllBannerResponse>

    @POST(Constants.ADD_MONEY_IN_WALLET)
    suspend fun addMoneyInWallet(
        @Body map: HashMap<String, Any>
    ): Response<AddMoneyInWalletResponse>

    @POST(Constants.COMPLETE_PAYMENT_ADD_MONEY_WALLET)
    suspend fun addMoneyInWalletPayment(
        @Body map: HashMap<String, Any>
    ): Response<CompletePyamentWalletResponse>

    @POST(Constants.REDEEM_GIFT_CARD_IN_WALLET)
    suspend fun redeemGiftCardInWallet(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    @GET(Constants.WALLET_HISTORY)
    suspend fun getWalletHistory(): Response<GetWalletHistoryResponse>

    @POST(Constants.CUSTOM_ORDER_ADD_TO_CART)
    suspend fun addToCartCustomOrder(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    @POST(Constants.REMOVE_CUSTOM_ORDER_FROM_CART)
    suspend fun removeFromCartCustomOrder(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    //Affiliate User

    @POST(Constants.CREATE_AFFILIATE_USER)
    suspend fun createAffiliateUser(
        @Body request: CreateAffiliateUserRequest
    ): Response<CreateAffiliateUserResponse>

    @POST(Constants.VERIFY_OTP_AFFILIATE_USER)
    suspend fun verifyAffiliateUserOtp(
        @Body map: HashMap<String, Any>
    ): Response<AffiliateUserLoginResponse>

    @POST(Constants.ADD_ADDRESS_AFFILIATE_USER)
    suspend fun addAffiliateUserAddress(
        @Body request: AffiliateUserAddAddressRequest
    ): Response<AddAffiliateUserAddressResponse>

    @POST(Constants.ADD_BANK_DETAIL_AFFILIATE_USER)
    suspend fun addAffiliateUserBandDetail(
        @Body request: AffiliateUserAddBankDetailRequest
    ): Response<AddAffiliateUserBankDetail>

    @POST(Constants.FORGOT_PASSWORD_AFFILIATE_USER)
    suspend fun forgotPasswordAffiliateUser(
        @Body map: HashMap<String, Any>
    ): Response<AffiliateUserLoginResponse>

    @POST(Constants.RESET_OTP_AFFILIATE_USER)
    suspend fun resendOtpAffiliateUser(
        @Body map: HashMap<String, Any>
    ): Response<AffiliateUserLoginResponse>

    @POST(Constants.CREATE_PASSWORD_AFFILIATE_USER)
    suspend fun createAffiliateUserPassword(
        @Body map: HashMap<String, Any>
    ): Response<AffiliateUserLoginResponse>

    @POST(Constants.RE_VERIFY_DOCUMENT_AFFILIATE_USER)
    suspend fun reVerifyDocument(
        @Body request: CreateAffiliateUserRequest
    ): Response<AffiliateUserLoginResponse>

    @POST(Constants.LOGIN_AFFILIATE_USER)
    suspend fun loginAffiliateUser(
        @Body map: HashMap<String, Any>
    ): Response<AffiliateUserLoginResponse>

    @POST(Constants.LOGIN_WITH_OTP_AFFILIATE_USER)
    suspend fun loginWithOtpAffiliateUser(
        @Body map: HashMap<String, Any>
    ): Response<AffiliateUserLoginResponse>

    @GET(Constants.GET_AFFILIATE_PROFILE)
    suspend fun getAffiliateProfile(): Response<GetAffiliateUserProfileResponse>

    @POST(Constants.UPDATE_AFFILIATE_PROFILE)
    suspend fun updateAffiliateUserProfile(
        @Body request : UpdateAffiliateProfileRequest
    ): Response<UpdateAffiliateProfileResponse>


    @POST(Constants.UPDATE_AFFILIATE_ADDRESS)
    suspend fun updateAffiliateUserAddress(
        @Body request: AffiliateUserAddAddressRequest
    ): Response<CommonResponse>


    @POST(Constants.UPDATE_AFFILIATE_BANK)
    suspend fun updateAffiliateUserBank(
        @Body request: AffiliateUserAddBankDetailRequest
    ): Response<CommonResponse>

    @POST(Constants.UPDATE_AFFILIATE_PASSWORD)
    suspend fun updateAffiliateUserPassword(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_AFFILIATE_ALL_PRODUCT)
    suspend fun getAffiliateAllProduct(@QueryMap map: HashMap<String,Any>): Response<GetAffiliateAllProductResponse>

 @GET(Constants.GET_AFFILIATE_PRODUCT_DETAIL)
    suspend fun getAffiliateProductDetail(@QueryMap map: HashMap<String,Any>): Response<GetAffiliateProductDetailResponse>

    @GET(Constants.GET_AFFILIATE_MY_PRODUCT)
    suspend fun getAffiliateMyProduct(@QueryMap map: HashMap<String,Any>): Response<GetAffiliateAllProductResponse>

}