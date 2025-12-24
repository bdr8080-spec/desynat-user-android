package com.desynatuser.ui.home.myOrder

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentOrderPaymentBinding
import com.desynatuser.ui.home.PaymentResultEvent
import com.desynatuser.ui.home.giftCard.response.ApplyGiftCardResponse
import com.desynatuser.ui.home.giftCard.response.BuyGiftCardResponse
import com.desynatuser.ui.home.profile.response.ApplyPromoCodeResponse
import com.desynatuser.ui.home.profile.response.GetAddressResponse
import com.desynatuser.ui.home.store.response.GetCartResponse
import com.desynatuser.ui.home.store.response.PlaceOrderRequest
import com.desynatuser.ui.home.store.response.PlaceOrderResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import com.myfatoorah.sdk.entity.MFBoxShadow
import com.myfatoorah.sdk.entity.MFCardViewError
import com.myfatoorah.sdk.entity.MFCardViewInput
import com.myfatoorah.sdk.entity.MFCardViewLabel
import com.myfatoorah.sdk.entity.MFCardViewPlaceHolder
import com.myfatoorah.sdk.entity.MFCardViewStyle
import com.myfatoorah.sdk.entity.MFCardViewText
import com.myfatoorah.sdk.entity.executepayment.MFExecutePaymentRequest
import com.myfatoorah.sdk.entity.googlepay.GooglePayRequest
import com.myfatoorah.sdk.entity.initiatepayment.MFInitiatePaymentRequest
import com.myfatoorah.sdk.entity.initiatepayment.MFInitiatePaymentResponse
import com.myfatoorah.sdk.entity.initiatesession.MFInitiateSessionRequest
import com.myfatoorah.sdk.entity.paymentstatus.MFGetPaymentStatusResponse
import com.myfatoorah.sdk.enums.MFAPILanguage
import com.myfatoorah.sdk.enums.MFCountry
import com.myfatoorah.sdk.enums.MFCurrencyISO
import com.myfatoorah.sdk.enums.MFFontFamily
import com.myfatoorah.sdk.enums.MFFontWeight
import com.myfatoorah.sdk.views.MFResult
import com.myfatoorah.sdk.views.MFSDK
import com.myfatoorah.sdk.views.embeddedpayment.googlepay.MFGooglePayHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class OrderPaymentFragment : BaseFragment() {
    private var binding: FragmentOrderPaymentBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var isPayment = false
    private var from = ""
    private var paymetMehod = ""
    private var walletBalance = 0.0
    private var transactionId :String?=null
    private var giftCardData : BuyGiftCardResponse.Data?=null
    private var applyGiftCard : ApplyGiftCardResponse.Data?=null
    private var cartData : GetCartResponse.Data?=null
    private var selectedAddress : GetAddressResponse.Data?=null
    private var promoCode : ApplyPromoCodeResponse.Data?=null
    var totalAmount = 0.0
    private var orderPlacedResponse: PlaceOrderResponse.Data?=null

    private lateinit var selectedRadio: AppCompatImageView
    private var selectedMethod: String = ""
    private var mfGooglePayHelper: MFGooglePayHelper? = null
    private val googlePayRequestCODE: Int = 991
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderPaymentBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        onPaymentResultEvent(null)
        initiateSession()
        cardStyling()
        binding?.apply {
            from = arguments?.getString("from").toString()
            parentFragmentManager.setFragmentResultListener(
                "gift_card_request",
                viewLifecycleOwner
            ) { _, bundle ->
                val giftCardJson = bundle.getString("giftCard")
                val giftCardNumber = bundle.getString("giftCardNumber")
                applyGiftCard = Gson().fromJson(giftCardJson, ApplyGiftCardResponse.Data::class.java)
                tvHaveGiftCard.isVisible = false
                tvApply.isVisible = false
                tvGiftCardNumber.isVisible = true
                tvGiftCard.isVisible = true
                tvGiftCardNumber.text = giftCardNumber
                totalAmount = totalAmount - applyGiftCard?.giftCardAmount!!
                tvGiftCard.text = "KD ${CommonMethod.formatPrice(applyGiftCard?.giftCardAmount?.toDouble()?:0.0)} Gift Card Amount Redeemed"
            }

            toolbar.tvToolbar.text = "Payment Method"
            if(from=="gift"){
                relGiftCard.isVisible = false
                tvGiftCard.isVisible = false
                val itemGiftCardJson = arguments?.getString("giftCardData")
                if (!itemGiftCardJson.isNullOrEmpty()) {
                    giftCardData = Gson().fromJson(itemGiftCardJson, BuyGiftCardResponse.Data::class.java)
                }
            }
            else{
                relGiftCard.isVisible = true

                val itemAddressJson = arguments?.getString("address")
                val itemCartJson = arguments?.getString("cart")
                val itemPromoJson = arguments?.getString("promo")
                if (!itemAddressJson.isNullOrEmpty()) {
                    selectedAddress = Gson().fromJson(itemAddressJson, GetAddressResponse.Data::class.java)
                }
                if (!itemCartJson.isNullOrEmpty()) {
                    cartData = Gson().fromJson(itemCartJson, GetCartResponse.Data::class.java)
                    totalAmount = cartData?.totalAmount?:0.0
                    Log.e("totalAmount", "itemCartJson: $totalAmount" )
                }
                if (!itemPromoJson.isNullOrEmpty()) {
                    promoCode = Gson().fromJson(itemPromoJson, ApplyPromoCodeResponse.Data::class.java)
                    if (promoCode?.finalAmount?:0.0!=0.0) {
                        totalAmount = promoCode?.finalAmount?:0.0
                    }
                    Log.e("totalAmount", "itemPromoJson: $totalAmount" )
                }
                if (giftCardData!=null) {
                    totalAmount -= giftCardData?.amount!!
                    Log.e("totalAmount", "giftCardData: $totalAmount" )
                }
            }
        }
        getWalletBalance()
        setupPaymentSelection()

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            relGiftCard.setOnClickListener {
                findNavController().navigate(R.id.action_orderPaymentFragment_to_applyGiftFragment)
            }

            btnProceed.setOnClickListener {
                when (selectedMethod) {
                    "KNET" -> {
                        paymetMehod = "1"
                        executeRedirectPayment(1)   // PaymentMethodId = 2 (KNET)
                    }

                    "GOOGLE_PAY" -> {
                        executeRedirectPayment(32)  // Apple Pay
                    }

                    "VISA" -> {
                        paymetMehod = "1"
                        executeRedirectPayment(2)  // Visa/MasterCard
                    }

                    "SAMSUNG_PAY" -> {
                        executeRedirectPayment(23) // Samsung Pay
                    }
                    "CREDIT_DEBIT" -> {
                        paymetMehod = "1"
                        executeRedirectPayment(2)
                          //  executeEmbeddedPayment()
                    }
                    "WALLET" -> {
                        paymetMehod = "2"
                        if (walletBalance>=cartData?.totalAmount?:0.0) {
                            createOrder()
                        }
                        else{
                            showToast("Wallet balance is low")
                        }
                    }
                    "COD" -> {
                        paymetMehod = "3"
                        createOrder()
                    }
                }

            }

        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.walletBalanceResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                walletBalance = response.data.balance
                lifecycleScope.launch(Dispatchers.Main) {
                    binding?.tvBalance?.text = "Available Balance : ${CommonMethod.formatPrice(walletBalance?:0.0)} KD"
                }
                isObserver = false
            }
        }

        viewModel.buyGiftCardPaymentResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                CommonMethod.showSuccessDialog(requireActivity(),"Gift card purchased successfully!"){
                    findNavController().navigate(
                        R.id.giftCard,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.home_navigation_graph, true)
                            .build()
                    )
                }
                isObserver = false
            }
        }

        viewModel.placeOrderResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                orderPlacedResponse = response.data
                    orderPayment()

                isObserver = false
            }
        }

        viewModel.orderPaymentResult.observe(viewLifecycleOwner) { response ->
            if (isPayment) {
                ProgressDialogUtils.getInstance().hideProgress()
                val bundle = Bundle().apply {
                    putString("amount", orderPlacedResponse?.totalAmount)
                    putString("address", Gson().toJson(selectedAddress))
                    putString("payment", selectedMethod)
                }
                findNavController().navigate(R.id.action_orderPaymentFragment_to_orderPlacedFragment,bundle)
                isPayment = false
            }
        }

    }

    private fun createOrder(){
        val request = PlaceOrderRequest(deliveryAddress = selectedAddress?.id.toString(), discountMrp = cartData?.discountOnMrp.toString(), giftcardAmount = if (applyGiftCard!=null) applyGiftCard?.giftCardAmount.toString() else null , paymentMethod = paymetMehod , promocodeId = if (promoCode!=null) promoCode?.id else null, promocodeStatus = if (promoCode==null) "no" else "yes"
            , promocodeValue = if (promoCode!=null) promoCode?.applyDiscountValue.toString() else null,
            shippingFees = cartData?.shippingFees.toString(),
            tax =cartData?.tax.toString() , totalMrp = cartData?.totalMrp.toString(), totalAmount = totalAmount.toString())
        viewModel.placeOrder(request)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    private fun orderPayment(){
        val map = HashMap<String,Any>()
        map["order_id"] = orderPlacedResponse?.orderId.toString()
        map["Payment_method"] = paymetMehod//1 myfatoorah 2 wallet 3 Cod
        map["payment_status"] = "1" // 0 pending 1 paid 2 cancel or reject 3 refund
        if (transactionId!=null){
            map["transaction_id"] = transactionId.toString()
        }
        viewModel.orderPayment(map)
        isPayment = true
    }

    private fun buyGiftCardPayment(transactionId: String) {
        val map = HashMap<String,Any>()
        map["order_id"] = giftCardData?.orderId.toString()
        map["transaction_id"] = transactionId
        map["payment_status"] = "1" // 0 pending 1 paid 2 cancel or reject 3 refund
        viewModel.buyGiftCardPayment(map)
        isPayment = true
    }

    private fun cardStyling() {
        binding?.mfPaymentView?.cardStyle = MFCardViewStyle(
            hideCardIcons = false,
            direction = "ltr",
            cardHeight = 230,
            input = MFCardViewInput(
                color = requireActivity().getColor(R.color.black),
                fontSize = 15f,
                fontFamily = MFFontFamily.CourierNew,
                inputHeight = 35f,
                inputMargin = 6f,
                borderColor = requireActivity().getColor(R.color.grey_stroke),
                borderWidth = 1f,
                borderRadius = 8f,
                boxShadow = MFBoxShadow(10, 10, 5, 0, requireActivity().getColor(R.color.white)),
                placeHolder = MFCardViewPlaceHolder(
                    holderName = "Name On Card",
                    cardNumber = "Number",
                    expiryDate = "MM / YY",
                    securityCode = "CVV"
                )
            ),
            label = MFCardViewLabel(
                display = true,
                color = requireActivity().getColor(R.color.black),
                fontSize = 14f,
                fontFamily = MFFontFamily.CourierNew,
                fontWeight = MFFontWeight.Bold,
                text = MFCardViewText(
                    holderName = "Card Holder Name",
                    cardNumber = "Card Number",
                    expiryDate = "Expiry Date",
                    securityCode = "Security Code"
                )
            ),
            error = MFCardViewError(
                borderColor = requireActivity().getColor(R.color.red),
                borderRadius = 8f,
                boxShadow = MFBoxShadow(10, 10, 5, 0, requireActivity().getColor(R.color.white))
            )
        )
    }

    private fun executeEmbeddedPayment() {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        val request = MFExecutePaymentRequest(totalAmount).apply {
            customerReference = if (from=="gift"){giftCardData?.orderId} else {orderPlacedResponse?.orderId}
        }
        binding?.mfPaymentView?.validate { result: MFResult<String> ->
            when (result) {
                is MFResult.Success -> {
                    Log.d("executeEmbeddedPayment123", "Response: " + Gson().toJson(result.response))
                    ProgressDialogUtils.getInstance().hideProgress()
                    binding?.mfPaymentView?.pay(
                        requireActivity(),
                        request,
                        MFAPILanguage.EN,
                        onInvoiceCreated = { Log.d("executeEmbeddedPayment", "invoiceId: $it") })
                    { invoiceId: String, result: MFResult<MFGetPaymentStatusResponse> ->
                        when (result) {
                            is MFResult.Success -> {
                                Log.d("executeEmbeddedPayment", "Response: " + Gson().toJson(result.response))
                                val paymentData =  result.response
                                paymentData.invoiceTransactions?.get(0)?.let {
                                    createOrder()
                                }
                            }
                            is MFResult.Fail -> {
                                result.error.message?.let { showToast(it) }
                                Log.d("executeEmbeddedPayment", "Fail: " + Gson().toJson(result.error))
                            }

                            MFResult.Loading -> {
                            }
                        }
                    }
                }
                is MFResult.Fail -> {
                    Log.d("executeEmbeddedPayment123", "Fail: " + Gson().toJson(result.error))
                    ProgressDialogUtils.getInstance().hideProgress()
                    result.error.message?.let { showToast(it) }
                }

                MFResult.Loading -> {

                }
            }
        }
    }

    private fun initiateSession(){
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        MFSDK.initiateSession {
            when (it) {
                is MFResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    binding?.mfPaymentView?.load(
                        it.response,
                        onCardBinChanged = { bin ->
                            Log.d("initiateSession", "bin: $bin")
                        }
                    )
                }
                is MFResult.Fail -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    it.error.message?.let { it1 -> showToast(it1) }
                    Log.d("initiateSession", "Fail: " + Gson().toJson(it.error))
                }

                MFResult.Loading -> {

                }
            }
        }

    }

    private fun setupPaymentSelection() {
        binding?.apply {
            val radioList = listOf(
                ivRadioKnet,
                ivRadioApplePay,
                ivRadioVisa,
                ivSamsungPay,
                ivCredit,
                ivWallet,
                ivCod
            )

            val layoutList = listOf(
                llKnet,
                llApplePay,
                llVisa,
                llSamsungPay,
                llCredit,
                llWallet,
                llCod
            )

            layoutList.forEachIndexed { index, layout ->
                layout.setOnClickListener {
                    radioList.forEach { it.setImageResource(R.drawable.ic_radio_unselected) }

                    val selected = radioList[index]
                    selected.setImageResource(R.drawable.ic_radio_selected)
                    selectedRadio = selected

                   when (index) {
                        0 -> { selectedMethod =  "KNET"
                            paymetMehod = "1"}
                        1 -> { selectedMethod =  "GOOGLE_PAY"
                            paymetMehod = "1"
                           /* initiateGooglePaySession()
                            btnProceed.isVisible = false*/
                        }
                        2 -> { selectedMethod =  "VISA"
                            paymetMehod = "1"}
                        3 -> { selectedMethod =  "SAMSUNG_PAY"
                            paymetMehod = "1"}
                        4 -> { selectedMethod =  "CREDIT_DEBIT"
                         paymetMehod = "1"}
                        5 -> { selectedMethod =  "WALLET"
                        paymetMehod = "2"}
                        6 -> { selectedMethod =  "COD"
                           paymetMehod = "3"}
                        else ->{ selectedMethod =   ""}
                    }

                    if (selectedMethod == "CREDIT_DEBIT") {
                        mfPaymentView.visibility = View.GONE
                        btnProceed.isVisible = true
                        mfGooglePayButton.isVisible = false
                    }
                    else if (selectedMethod == "GOOGLE_PAY") {
                        btnProceed.isVisible = true
                        mfGooglePayButton.isVisible = false
                        mfPaymentView.visibility = View.GONE
                    }
                    else {
                        btnProceed.isVisible = true
                        mfGooglePayButton.isVisible = false
                        mfPaymentView.visibility = View.GONE
                    }
                }
            }
        }
    }

/*
    private fun startPayment(paymentMethodId: Int) {

        val amount = cartData?.totalAmount?.toDouble() ?: 0.0
        ProgressDialogUtils.getInstance().showProgress(requireActivity())

        val request = MFExecutePaymentRequest(paymentMethodId, amount)

        MFSDK.executePayment(requireActivity(), request, MFAPILanguage.EN) { invoiceId, result ->

            ProgressDialogUtils.getInstance().hideProgress()

            when (result) {
                is MFResult.Success -> {
                    val status = result.response

                    status.invoiceTransactions?.getOrNull(0)?.let {
                        orderPayment()
                    }
                }
                is MFResult.Fail -> {
                    result.error.message?.let { showToast(it) }
                    Log.e("HostedPayment", "Error: ${Gson().toJson(result.error)}")
                }
                MFResult.Loading -> {}
            }
        }
    }
*/

    private fun initiateGooglePaySession() {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        MFSDK.initiateSession(MFInitiateSessionRequest(customerIdentifier = "user_${giftCardData?.orderId}")) {
            when (it) {
                is MFResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    setupGooglePayHelper(it.response.sessionId)
                }
                is MFResult.Fail -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    it.error.message?.let { msg -> showToast(msg) }
                    Log.d("GooglePay", "Session initiation failed: ${Gson().toJson(it.error)}")
                }
                MFResult.Loading -> { }
            }
        }
    }

    private fun setupGooglePayHelper(sessionId: String) {
        val googlePayRequest = GooglePayRequest(
            totalPrice = giftCardData?.amount.toString(),
            merchantId = Constants.MERCHANT_ID,
            merchantName = Constants.MERCHANT_NAME,
            countryCode = MFCountry.UNITED_ARAB_EMIRATES_UAE.code,
            currencyIso = MFCurrencyISO.UAE_AED
        )

        mfGooglePayHelper = MFGooglePayHelper(
            activity = requireActivity(),
            googlePayRequestCODE = googlePayRequestCODE,
            sessionId = sessionId,
            googlePayRequest = googlePayRequest,
            onInvoiceCreated = { invoiceId ->
                Log.d("GooglePay", "Invoice created: $invoiceId")
            },
            callback = { invoiceId, result ->
                when (result) {
                    is MFResult.Success -> {
                        Log.d("GooglePay", "Payment success: " + Gson().toJson(result.response))
                        val paymentData = result.response
                        createOrder()

                     /*   paymentData.invoiceTransactions?.get(0)?.let {
                            createOrder()
                        }*/
                    }

                    is MFResult.Fail -> {
                        Log.d("GooglePay", "Payment failed: " + Gson().toJson(result.error))
                        showToast("Google Pay failed: ${result.error.message}")
                    }

                    else -> {}
                }
            }
        )
        binding?.mfGooglePayButton?.let { mfGooglePayHelper?.setGooglePayButton(it) }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPaymentResultEvent(event: PaymentResultEvent?=null) {
        if (event?.requestCode == 991) {
            when (event.resultCode) {
                Activity.RESULT_OK -> {
                    mfGooglePayHelper?.onActivityResult(event.requestCode, event.resultCode, event.data)
                    Log.d("PaymentFlow", "Payment Success!")
                }
                Activity.RESULT_CANCELED -> {
                    showToast("Payment Cancelled")
                    Log.d("PaymentFlow", "Payment Cancelled")
                }
                else -> {
                    showToast("Payment Failed or Unknown")
                    Log.d("PaymentFlow", "Payment Failed or Unknown")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Constants.currentScreen = "orderPayment"
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        Constants.currentScreen = ""
        EventBus.getDefault().unregister(this)

    }


        private fun executeRedirectPayment(paymentMethodId:Int) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            val requestInitiate = MFInitiatePaymentRequest(totalAmount, MFCurrencyISO.KUWAIT_KWD)
            MFSDK.initiatePayment(
                requestInitiate,
                MFAPILanguage.EN
            ) { result: MFResult<MFInitiatePaymentResponse> ->
                when (result) {
                    is MFResult.Success ->
                        Log.d("executeRedirectPayment", "Response: " + Gson().toJson(result.response))
                    is MFResult.Fail ->
                        Log.d("executeRedirectPayment", "Fail: " + Gson().toJson(result.error))

                    else -> {}
                }
            }

            val request = MFExecutePaymentRequest(paymentMethodId, cartData?.totalAmount).apply {
               /* customerReference = subscriptionData?.id.toString()
                customerName = "${profileData?.firstName} ${profileData?.lastName}"
                mobileCountryCode = profileData?.countryCode
                customerMobile = profileData?.mobile
                customerEmail = profileData?.email*/
            }

            MFSDK.executePayment(
                requireActivity(),
                request,
                MFAPILanguage.EN
            ) { invoiceId: String, result: MFResult<MFGetPaymentStatusResponse> ->
                ProgressDialogUtils.getInstance().hideProgress()

                when (result) {
                    is MFResult.Success -> {
                        Log.d("executeRedirectPayment", "Success: ${Gson().toJson(result.response)}")
                        val paymentData =  result.response
                        transactionId =  paymentData.invoiceTransactions?.get(0)?.transactionId
                        createOrder()
                    }
                    is MFResult.Fail -> {
                        Log.d("executeRedirectPayment", "Fail: ${Gson().toJson(result.error)}")
                        result.error.message?.let { showToast(it) }
                      //  showToast("Payment failed")
                    }
                    MFResult.Loading -> {
                    }
                }
            }
        }

    private fun getWalletBalance(){
        viewModel.getWalletBalance()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }


}