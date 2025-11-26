package com.desynatuser.network

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
import com.desynatuser.utils.Constants
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

    @GET(Constants.GET_CATEGORY)
    suspend fun getCategory(): Response<GetCategoryResponse>

    @GET(Constants.GET_SUB_CATEGORY)
    suspend fun getSubCategory(
        @Query ("category_id") categoryId: String,
        @Query ("search") search: String?=null
    ): Response<GetSubCategoryResponse>

    @POST(Constants.UPDATE_PROFILE)
    suspend fun updateProfile(
        @Body body: HashMap<String, String>
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
        @Body map:HashMap<String,Any>,
    ): Response<GetSingleStoreResponse>

    @POST(Constants.ADD_TO_CART)
    suspend fun addToCart(
        @Body map:HashMap<String,Any>,
    ): Response<AddToCartResponse>

    @POST(Constants.ADD_TO_CART_QUANTITY)
    suspend fun addCartQuantity(
        @Body map:HashMap<String,Any>,
    ): Response<CartQunatityIncrementDecrementResponse>

    @GET(Constants.VIEW_CART_DETAIL)
    suspend fun viewCartDetail(): Response<GetCartResponse>

    @POST(Constants.ADD_TO_WISHLIST)
    suspend fun addToWishlist(
        @Body map:HashMap<String,Any>,
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

    @DELETE("${Constants.DELETE_ADDRESS}/{id}")
    suspend fun deleteAddress(
        @Path ("id") id: String
    ): Response<CommonResponse>


    @GET(Constants.GET_STORE_SORT_LIST)
    suspend fun getStoreSorList(): Response<GetStoreSortListResponse>


    @GET(Constants.GET_WISHLIST)
    suspend fun getWishList(): Response<CommonResponse>


    @GET(Constants.GET_SEARCH)
    suspend fun getSearch(): Response<CommonResponse>

}