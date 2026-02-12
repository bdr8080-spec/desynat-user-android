package com.desynatuser.ui.home.myOrder

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatuser.R
import com.desynatuser.adapter.OrderProductAdapter
import com.desynatuser.adapter.OtherOrderProductAdapter
import com.desynatuser.adapter.ProductReviewAdapter
import com.desynatuser.adapter.ReasonAdapter
import com.desynatuser.adapter.ReasonSizeAdapter
import com.desynatuser.adapter.SpecificationAdapter
import com.desynatuser.adapter.StoreReviewAdapter
import com.desynatuser.adapter.UploadMsgImageAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.CancelOrderDialogBinding
import com.desynatuser.databinding.ExchangeOrderDialogBinding
import com.desynatuser.databinding.FragmentOrderDetailBinding
import com.desynatuser.databinding.RateProductDialogBinding
import com.desynatuser.databinding.ReturnOrderDialogBinding
import com.desynatuser.ui.home.chat.UploadImageType
import com.desynatuser.ui.home.myOrder.response.GetAllOrderResponse
import com.desynatuser.ui.home.myOrder.response.GetReasonListResponse
import com.desynatuser.ui.home.myOrder.response.GetSingleOrderSummaryResponse
import com.desynatuser.ui.home.store.response.GetSizeListResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.text.toDoubleOrNull

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment() {

    private var binding : FragmentOrderDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private var isReturnObserver = false
    private var isOrderRatingObserver = false
    private var isSizeObserver = false
    private var getOrderData : GetAllOrderResponse.Data?=null
    private var orderData : GetSingleOrderSummaryResponse.Data?=null
    private var orderProductAdapter: OrderProductAdapter?=null
    private var otherOrderProductAdapter: OtherOrderProductAdapter?=null
    private var productList = ArrayList<GetSingleOrderSummaryResponse.Data>()
    private var otherProductList = ArrayList<GetSingleOrderSummaryResponse.Data.AllOtherOrderVarient>()
    private var orderId = ""

    private var sizeList = ArrayList<GetSizeListResponse.Data>()
    private var reasonList = ArrayList<GetReasonListResponse.Data>()
    private var sizeAdapter: ReasonSizeAdapter? = null
    private var reasonAdapter: ReasonAdapter? = null

    private var returnDialogBinding : ReturnOrderDialogBinding?=null

    var isImageObserver = false
    var exchangeReturn = ""
    var imageType = 0//1-return,2-productrview,3-storereview
    private lateinit var imagePicker: ImagePicker
    private var productReviewImage = ArrayList<String>()
    private var storeReviewImage = ArrayList<String>()
    private var returnImage:String?=null
    private var imageUri: Uri? = null

    private var uploadMsgImageAdapter: UploadMsgImageAdapter?=null
    private var uploadImageList = ArrayList<UploadImageType>()


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            imagePicker.onCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePicker.onGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            imagePicker.onPermissionResult(granted)
        }

    private var exchangeDialog: Dialog?=null
    private var returnDialog: Dialog?=null
    private var cancelDialog: Dialog?=null

    private var productReviewAdapter: ProductReviewAdapter?=null
    private var storeReviewAdapter: StoreReviewAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ProgressDialogUtils.getInstance().hideProgress()
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            val itemJson = arguments?.getString("orderData")
            if (!itemJson.isNullOrEmpty()) {
                getOrderData = Gson().fromJson(itemJson, GetAllOrderResponse.Data::class.java)
                orderId = getOrderData?.order?.orderId.toString()
            }

            toolbar.tvToolbar.text = "My Order"

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    when (imageType){
                        1->{
                            imageUri = uri
                        //    returnDialogBinding?.ivImage?.setImageURI(it)
                        }
                        2->{

                        }
                        3->{

                        }
                    }

                    uploadFile(CommonMethod.uriToFile(requireActivity(), it))
                }
            }

            getOrderDetail()
        }
    }

    private fun cancelOrder(reason: String,id:String) {
        val map = HashMap<String,Any>()
        map["order_id"] = orderData?.orderId.toString()
        map["orderdetails_id"] = id
        map["reason"] = reason
        viewModel.cancelOrder(map)
        isReturnObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
    }

    private fun exchangeOrder(reason: String,size: String,id:String) {
        exchangeReturn = "2"
        val map = HashMap<String,Any>()
        map["order_id"] = orderData?.orderId.toString()
        map["orderdetails_id"] = id
        map["type"] = exchangeReturn  // 1 return 2 Exchange
        map["exchange_reason"] = reason
        map["new_size"] = size

        viewModel.returnExchangeOrder(map)
        isReturnObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
    }

    private fun returnOrder(reason:String,id:String) {
        var tempList = ArrayList<String>()
        exchangeReturn = "1"
        val map = HashMap<String,Any>()
        map["order_id"] = orderData?.orderId.toString()
        map["orderdetails_id"] = id
        map["type"] = exchangeReturn  // 1 return 2 Exchange
        map["return_reason"] = reason
        if (uploadImageList.size!=0) {
            for(i in 0 until uploadImageList.size){
                tempList.add(uploadImageList[i].imageUrl)
            }
            map["return_image"] = tempList
        }
        viewModel.returnExchangeOrder(map)
        isReturnObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
    }

    private fun rateOrder(map:HashMap<String,Any>) {
        viewModel.orderRating(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isOrderRatingObserver = true
    }

    fun uploadFile(image:File){
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    private fun setData() {
        binding?.apply {

            productList.clear()
            orderData?.let { productList.add(it) }

            otherProductList.clear()
            orderData?.allOtherOrderVarient?.let { otherProductList.addAll(it) }

            orderProductAdapter = OrderProductAdapter(requireActivity(),productList,orderData?.deliveryStatus.toString(),object :OrderProductAdapter.ClickOnItem{
                override fun onClickOnItem(position: Int, type: Int) {
                    when(type){
                        1->{
                            cancelOrderDialog(productList[position].id)
                        }
                        2->{
                            exchangeOrderDialog(productList[position].id)
                        }
                        3->{
                            returnOrderDialog(productList[position].id)
                        }
                        4->{
                            rateProductDialog(productList[position].id)
                        }
                    }
                }

            })
            rvProduct.adapter = orderProductAdapter

            if (otherProductList.isNotEmpty()){
                relOtherProduct.isVisible = true
            }
            else{
                relOtherProduct.isVisible = false
            }

            otherOrderProductAdapter = OtherOrderProductAdapter(requireActivity(),otherProductList,object :OtherOrderProductAdapter.ClickOnItem{
                override fun onClickOnItem(position: Int, type: Int) {
                    when(type){
                        1->{
                            cancelOrderDialog(productList[position].id)
                        }
                        2->{
                            exchangeOrderDialog(productList[position].id)
                        }
                        3->{
                            returnOrderDialog(productList[position].id)
                        }
                        4->{
                            rateProductDialog(productList[position].id)
                        }
                    }
                }

            })
            rvOtherProduct.adapter = otherOrderProductAdapter

            toolbar.tvToolbar.text = "#${getOrderData?.id}"

            tvPlacedDate.text = "Placed On "+CommonMethod.customizeChangeDateFormat(orderData?.createdAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mma")

            tvTotalMRP.text = "${CommonMethod.formatPrice(orderData?.order?.totalMrp?.toDoubleOrNull()?:0.0)} KD"

            val mainCustomPrice =
                if (orderData?.orderType == "2") {
                    val price = orderData?.amount?.toDoubleOrNull() ?: 0.0
                    val qty = orderData?.quantity ?: 1
                    price * qty
                } else {
                    0.0
                }

            val customOtherItems = otherProductList.filter { it.orderType == "2" }
            val otherCustomPrice = customOtherItems.sumOf {
                val price = it.amount?.toDoubleOrNull() ?: 0.0
                val qty = it.quantity ?: 1
                price * qty
            }

            val totalCustomPrice = mainCustomPrice + otherCustomPrice

            val mainIsCustom = orderData?.orderType == "2"
            val hasOtherProducts = otherProductList.isNotEmpty()
            val isOtherCustom = customOtherItems.isNotEmpty()
            val isOtherNormal = otherProductList.any { it.orderType == "1" }

            if (mainIsCustom && !hasOtherProducts) {
                clCustomBillingDetail.isVisible = true
                clBillingData.isVisible = false
            }
            else if (mainIsCustom && isOtherCustom && !isOtherNormal) {
                clCustomBillingDetail.isVisible = true
                clBillingData.isVisible = false
            }
            else if (mainIsCustom && isOtherNormal) {
                clCustomBillingDetail.isVisible = false
                clBillingData.isVisible = true
                tvCustomItem.isVisible = true
                tvCustomItemPrice.isVisible = true
                tvCustomItemPrice.text = "${CommonMethod.formatPrice(totalCustomPrice)} KD"
            }
            else {
                clCustomBillingDetail.isVisible = false
                clBillingData.isVisible = true

                if (isOtherCustom||mainIsCustom) {
                    tvCustomItem.isVisible = true
                    tvCustomItemPrice.isVisible = true
                    tvCustomItemPrice.text = "${CommonMethod.formatPrice(totalCustomPrice)} KD"
                } else {
                    tvCustomItem.isVisible = false
                    tvCustomItemPrice.isVisible = false
                }
            }

            if (orderData?.order?.discountMrp?.toDoubleOrNull()==0.0){
                tvDiscountOnMrp.isVisible = false
                tvDiscountOnMRP.isVisible = false
            }
            else{
                tvDiscountOnMrp.isVisible = true
                tvDiscountOnMRP.isVisible = true
                tvDiscountOnMRP.text = "${CommonMethod.formatPrice(orderData?.order?.discountMrp?.toDoubleOrNull()?:0.0)} KD"
            }

            if (CommonMethod.formatPrice(orderData?.order?.shippingFees?.toDoubleOrNull()?:0.0)=="0"){
                tvShippingFee.text = "Free Delivery"
            }
            else{
                tvShippingFee.text = "${CommonMethod.formatPrice(orderData?.order?.shippingFees?.toDoubleOrNull()?:0.0)} KD"
            }
            tvTaxValue.text = "${CommonMethod.formatPrice(orderData?.order?.tax?.toDoubleOrNull()?:0.0)} KD"


            val itemPrice = orderData?.amount?.toDoubleOrNull() ?: 0.0
            val quantity = orderData?.quantity?: 1
            val tax = orderData?.order?.tax?.toDoubleOrNull() ?: 0.0
            val shippingFees = orderData?.order?.shippingFees?.toDoubleOrNull() ?: 0.0

            val subTotal = itemPrice * quantity
            val totalAmount = totalCustomPrice + tax + shippingFees

            if (otherProductList.size!=0){
                tvPerItemPrice.text = "Custom Items Price"
            }
            else{
                tvPerItemPrice.text = "Item Price (${CommonMethod.formatPrice(itemPrice)} KD X $quantity)"
            }

            tvPerItemPriceValue.text = "${CommonMethod.formatPrice(totalCustomPrice)} KD"

            if (CommonMethod.formatPrice(shippingFees)=="0"){
                tvShippingFeesValue.text = "Free Delivery"
            }
            else{
                tvShippingFeesValue.text =
                    "${CommonMethod.formatPrice(shippingFees)} KD"
            }

            tvCustomTaxValue.text =
                "${CommonMethod.formatPrice(tax)} KD"

            tvCustomTotalAmountValue.text =
                "${CommonMethod.formatPrice(totalAmount)} KD"


            if (orderData?.order?.promocodeStatus=="yes"){
                tvPromoCode.isVisible = true
                tvPromoCodeValue.isVisible = true
                tvPromoCode.text = "Coupon Discount (${orderData?.order?.promocodeDetails?.nameEnglish})"
                tvPromoCodeValue.text = "${CommonMethod.formatPrice(orderData?.order?.promocodeValue?.toDoubleOrNull()?:0.0)} KD"

            }
            else{
                tvPromoCode.isVisible = false
                tvPromoCodeValue.isVisible = false
            }

            tvDeliveryAddress.text = orderData?.order?.deliveryDetails?.address+" "+orderData?.order?.deliveryDetails?.state

            tvTotalAmountValue.text = "${CommonMethod.formatPrice(orderData?.order?.totalAmount?.toDoubleOrNull()?:0.0)} KD"
            var savedAmount = CommonMethod.formatPrice((orderData?.order?.discountMrp?.toDoubleOrNull() ?: 0.0) + (orderData?.order?.discountCoupon?.toDoubleOrNull() ?: 0.0))
            if (savedAmount!="0"){
                tvSavePrice.text = "You will save ${CommonMethod.formatPrice((orderData?.order?.discountMrp?.toDoubleOrNull() ?: 0.0) + (orderData?.order?.discountCoupon?.toDoubleOrNull() ?: 0.0))} KD on this"
            }
            else{
                tvSavePrice.isVisible = false
            }

            tvAmount.text = "${CommonMethod.formatPrice(orderData?.order?.totalAmount?.toDoubleOrNull()?:0.0)} KD"

            tvTransaction.text = "Transaction Id: ${orderData?.order?.transactionId}"
            when(orderData?.order?.paymentMethod){
                "1" ->{
                    tvTransaction.isVisible = true
                    ivPay.setImageResource(R.drawable.ic_visa)
                    tvPayBy.text = "Paid by Card"
                }
                "2" ->{
                    tvTransaction.isVisible = false
                    ivPay.setImageResource(R.drawable.ic_wallet)
                    tvPayBy.text = "Paid by Wallet"
                }
                "3" ->{
                    tvTransaction.isVisible = false
                    ivPay.setImageResource(R.drawable.ic_cod)
                    tvPayBy.text = "Cash On Delivery"
                }
            }

            if (orderData?.paymentStatus=="2"||orderData?.paymentStatus=="3"){
                tvStatus.text = "Cancelled"
                tvStatus.setTextColor(Color.parseColor("#D53B36"))
                relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D53B36"))
                ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                ivOnTheWay.isVisible = false
                tvOnTheWay.isVisible = false
                tvDeliveryDate.isVisible = false
                ivReturned.setImageResource(R.drawable.ic_cancelled)
                tvPicked.text = "In Process"
                tvOrderStatus.text = "Cancel Status"
                tvReturned.text = "Cancelled"
                tvReturned.setTextColor(Color.parseColor("#E70000"))
                viewOnTheWay.setBackgroundColor(Color.parseColor("#80D72E04"))
                viewPicked.setBackgroundColor(Color.parseColor("#80D72E04"))
            }
            else{
                when(orderData?.deliveryStatus){
                    "0" -> {

                        when (orderData?.returnType){
                            "1"->{
                                returnData()
                            }
                            "2"->{
                                exchangeData()
                            }
                        }

                    }
                    "1" -> {
                        if (orderData?.rEApprove == "1" && orderData?.returnType == "1" && orderData?.order?.assignStatus == "0") {
                            tvStatus.text = "Confirmed"
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                            relStatus.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#17854C"))
                            returnData()
                        } else if (orderData?.rEApprove == "1" && orderData?.returnType == "1" && orderData?.order?.assignStatus == "1") {
                            tvStatus.text = "Confirmed"
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                            relStatus.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#17854C"))
                            returnData()
                        }else {
                            btnInvoice.isVisible = false
                            tvStatus.text = "Confirmed"
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                            relStatus.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#17854C"))
                        }
                    }
                    "2" -> {
                        btnInvoice.isVisible = false
                        relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#17854C"))
                        tvStatus.setTextColor(Color.parseColor("#17854C"))
                        tvStatus.text = "Confirmed"
                        ivInProcess.setImageResource(R.drawable.ic_order_picked)
                        ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                        ivReturned.setImageResource(R.drawable.ic_order_return)
                    }
                    "2.1" -> {
                        btnInvoice.isVisible = false
                        relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        tvStatus.text = "Ongoing"
                        ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                        ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                        ivReturned.setImageResource(R.drawable.ic_order_return)
                    }
                    "2.2" -> {
                       exchangeData()
                    }
                    "3" -> {
                            when (orderData?.returnType) {
                                "1" -> {
                                    returnData()
                                }

                                "2" -> {
                                    exchangeData()
                                }

                                else -> {
                                    relStatus.backgroundTintList =
                                        ColorStateList.valueOf(Color.parseColor("#16B364"))
                                    tvStatus.setTextColor(Color.parseColor("#16B364"))
                                    tvStatus.text = "Delivered"
                                    btnInvoice.isVisible = true
                                    ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                                    ivReturned.setImageResource(R.drawable.ic_order_return_selected)
                                    tvDeliveryDate.text = "Delivery Date ${
                                        CommonMethod.customizeChangeDateFormat(
                                            orderData?.updatedAt.toString(),
                                            outputFormat = "dd/MM/yyyy"
                                        )
                                    }"
                                }
                            }
                        }

                    "7" -> {
                        when (orderData?.returnType){
                            "1"->{
                                returnData()
                            }
                            "2"->{
                                exchangeData()
                            }
                            else-> {
                                btnInvoice.isVisible = false
                                relStatus.backgroundTintList =
                                    ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                                tvStatus.text = "Ongoing"
                                ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                                ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                                ivReturned.setImageResource(R.drawable.ic_order_return)
                            }
                            }
                        ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                        ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                        ivReturned.setImageResource(R.drawable.ic_order_return)
                    }

                    "4" -> {
                        cancelData()
                    }
                    "5" -> {
                        returnData()
                    }

                    "6" -> {
                        relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                        tvStatus.text = "Confirmed"
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }
                    "8" -> {
                        relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                        tvStatus.text = "Not Delivered"
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                        ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                        ivReturned.setImageResource(R.drawable.ic_order_return)
                        tvReturned.text = "Not Delivered"
                        ivReturned.setImageResource(R.drawable.ic_cancelled)
                        tvReturned.setTextColor(Color.parseColor("#E70000"))
                        viewOnTheWay.setBackgroundColor(Color.parseColor("#80D72E04"))
                        tvDeliveryDate.isVisible = false
                    }
                    "9" -> {
                        exchangeData()

                    }
                }
            }

        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnInvoice.setOnClickListener {
                CommonMethod.openPdfInBrowser(requireActivity(),"http://157.241.119.72/Invoice/${orderData?.order?.orderId.toString()}")
               // getInvoice()
            }
        }
    }

    private fun getInvoice() {
        viewModel.getInvoice(orderData?.order?.orderId.toString())
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isImageObserver||isSizeObserver||isReturnObserver||isOrderRatingObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
                isImageObserver = false
                isSizeObserver = false
                isReturnObserver = false
                isOrderRatingObserver = false
            }
        }

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    when (imageType){
                        1->{
                            returnImage = response.data.url
                            imageUri?.let { it1 -> UploadImageType(it1,response.data.url) }
                                ?.let { it2 -> uploadImageList.add(it2) }
                            uploadMsgImageAdapter?.notifyDataSetChanged()

                            imageUri = null
                        }
                        2->{
                            productReviewImage.add(response.data.url)
                            productReviewAdapter?.notifyDataSetChanged()
                        }
                        3->{
                            storeReviewImage.add(response.data.url)
                            storeReviewAdapter?.notifyDataSetChanged()
                        }
                    }

                    imageType = 0
                }
                isImageObserver = false
            }
        }

        viewModel.cancelOrderResult.observe(viewLifecycleOwner) { response ->
            if (isReturnObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                cancelDialog?.dismiss()
                CommonMethod.showSuccessDialog(requireActivity(),"Order Cancelled successfully!"){
                    getOrderDetail()
                }
                isReturnObserver = false
            }
        }

        viewModel.invoiceResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.downloadPdf(requireActivity(),response?.data.toString(),orderData?.order?.orderId.toString())
                isObserver = false
            }
        }

        viewModel.returnExchangeOrderResult.observe(viewLifecycleOwner) { response ->
            if (isReturnObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (exchangeReturn=="1"){
                    returnDialog?.dismiss()
                     CommonMethod.showSuccessDialog(requireActivity(),"Order Return Request Sent successfully!"){
                         getOrderDetail()
                         exchangeReturn = ""
       }
                }
                else if (exchangeReturn=="2") {
                    exchangeDialog?.dismiss()
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        "Order Exchange Request Sent successfully!"
                    ) {
                        getOrderDetail()
                        exchangeReturn = ""
                    }
                }

                isReturnObserver = false
            }
        }

        viewModel.orderRatingResult.observe(viewLifecycleOwner) { response ->
            if (isOrderRatingObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),"Review Submitted successfully!"){
                    getOrderDetail()
                }
                isOrderRatingObserver = false
            }
        }

        viewModel.getReasonListResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    reasonList.clear()
                    reasonList.addAll(response.data)
                    reasonList.add(
                        GetReasonListResponse.Data(
                            reasonEnglish = "Other"
                        )
                    )

                    reasonAdapter?.notifyDataSetChanged()
                }
                isObserver = false
            }
        }

        viewModel.orderDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                orderData = response.data
                setData()
                isObserver = false
            }
        }

        viewModel.sizeListResult.observe(viewLifecycleOwner) { response ->
            if (isSizeObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                sizeList.clear()
                sizeList.addAll(response.data)
                sizeAdapter?.notifyDataSetChanged()
                isSizeObserver = false
            }
        }
    }

    private fun getOrderDetail(){
        val map = HashMap<String,Any>()
        map["order_id"] = getOrderData?.orderId.toString()
        map["orderdetails_id"] = getOrderData?.id.toString()
      //  map["order_type"] = getOrderData?.orderType.toString()
        viewModel.getOrderDetailInfo(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getReasonList(type:Int){
        val map = HashMap<String,Any>()
        map["type"] = type //	1 - exchange, 2 - return, 3 - cancel
        viewModel.getReasonList(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun rateProductDialog(id: String) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RateProductDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!

        val displayMetrics = resources.displayMetrics
        val height = (displayMetrics.heightPixels * 0.9).toInt()

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            height
        )

        /*window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )*/
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            productReviewAdapter = ProductReviewAdapter(requireActivity(),productReviewImage,object :ProductReviewAdapter.Click{
                override fun onClick(position: Int) {
                    productReviewImage.removeAt(position)
                    productReviewAdapter?.notifyDataSetChanged()
                }

            })
            rvProductReview.adapter = productReviewAdapter

            storeReviewAdapter = StoreReviewAdapter(requireActivity(),storeReviewImage,object :StoreReviewAdapter.Click{
                override fun onClick(position: Int) {
                    storeReviewImage.removeAt(position)
                    storeReviewAdapter?.notifyDataSetChanged()
                }

            })
            rvStoreReview.adapter = storeReviewAdapter

            rbProduct.setOnRatingBarChangeListener { _, rating, _ ->
                if (rating > 0f) {
                    tvSubmit.isVisible = true
                } else {
                    tvSubmit.isVisible = false
                }
            }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            ivPickProduct.setOnClickListener {
                imageType=2
                imagePicker.showImageSourceDialog()
            }

            ivPickStore.setOnClickListener {
                imageType=3
                imagePicker.showImageSourceDialog()
            }

            tvSubmit.setOnClickListener {
                val map = HashMap<String,Any>()
                map["order_id"] = orderData?.orderId.toString()
                map["orderdetails_id"] = orderData?.id.toString()
                map["product_rating"] = rbProduct.rating
                map["product_review"] = etProductReview.text?.trim().toString()
                if (productReviewImage.size!=0) {
                   // map["product_image"] = productReviewImage.toString()
                    map["product_image"] = productReviewImage[0]
                }
                map["store_rating"] = rbStore.rating
                map["store_review"] = etStoreReview.text?.trim().toString()
                if (storeReviewImage.size!=0) {
                 //   map["store_image"] = storeReviewImage.toString()
                    map["store_image"] = storeReviewImage[0]
                }
                rateOrder(map)
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun cancelOrderDialog(id:String) {
        var reason = ""
        cancelDialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = CancelOrderDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { cancelDialog?.setContentView(it) }

        val window: Window = cancelDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        cancelDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            reasonList.clear()
            reasonAdapter = ReasonAdapter(requireActivity(), reasonList,object :
                ReasonAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    if (position==reasonList.size-1){
                        etReason.isVisible = true
                        reason = ""
                    }
                    else{
                        etReason.isVisible = false
                        reason = reasonList[position].reasonEnglish.toString()
                    }
                }

            })
            rvReason.adapter = reasonAdapter

            getReasonList(3)

            tvToolbar.setOnClickListener {
                cancelDialog?.dismiss()
            }

            btnSubmit.setOnClickListener {
                if (etReason.isVisible){
                    if (etReason.text?.trim().toString().isEmpty()){
                        showToast("Please enter reason")
                    }
                    else{
                        reason = etReason.text?.trim().toString()
                        cancelOrder(reason,id)
                    }
                }
                else if(reason==""){
                    showToast("Please select reason")
                }
                else {
                    cancelOrder(reason,id)
                }
            }
        }

        cancelDialog?.show()
    }

    private fun returnOrderDialog(id: String) {
        var reason = ""
         returnDialog = Dialog(requireActivity(), R.style.CustomDialog)
        returnDialogBinding = ReturnOrderDialogBinding.inflate(LayoutInflater.from(context))
        returnDialogBinding?.root.let {
            if (it != null) {
                returnDialog?.setContentView(it)
            }
        }

        val window: Window = returnDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        returnDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        returnDialogBinding?.apply {
            uploadImageList.clear()
            uploadMsgImageAdapter = UploadMsgImageAdapter(requireActivity(),uploadImageList,object :
                UploadMsgImageAdapter.Click{
                override fun onClick(position: Int) {
                    uploadImageList.removeAt(position)
                    uploadMsgImageAdapter?.notifyDataSetChanged()
                }

            })
            rvUploadImg.adapter = uploadMsgImageAdapter

            reasonList.clear()
            reasonAdapter = ReasonAdapter(requireActivity(), reasonList,object :
                ReasonAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    if (position==reasonList.size-1){
                        etReason.isVisible = true
                        reason = ""
                    }
                    else{
                        etReason.isVisible = false
                        reason = reasonList[position].reasonEnglish.toString()
                    }
                }

            })
            rvReason.adapter = reasonAdapter
            getReasonList(2)

            ivImage.setOnClickListener {

            }

            tvToolbar.setOnClickListener {
                returnDialog?.dismiss()
            }

            btnSubmit.setOnClickListener {
                if (etReason.isVisible){
                    if (etReason.text?.trim().toString().isEmpty()){
                        showToast("Please enter reason")
                    }
                    else{
                        reason = etReason.text?.trim().toString()
                        returnOrder(reason, id)
                    }
                }
                else if(reason==""){
                    showToast("Please select reason")
                }
                else {
                    returnOrder(reason, id)
                }
            }

            ivImage.setOnClickListener {
                imageType = 1
                imagePicker.showImageSourceDialog()
            }
        }

        returnDialog?.show()
    }

    private fun exchangeOrderDialog(id: String) {
        var reason = ""
        var size = ""
        exchangeDialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ExchangeOrderDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { exchangeDialog?.setContentView(it) }

        val window: Window = exchangeDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        exchangeDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

             sizeAdapter = ReasonSizeAdapter(requireActivity(), sizeList,object :
                ReasonSizeAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    size = sizeList[position].id.toString()
                }

            })
            rvSize.adapter = sizeAdapter

            reasonList.clear()
            reasonAdapter = ReasonAdapter(requireActivity(), reasonList,object :
                ReasonAdapter.OnClick {
                /*      override fun onClickItem(position: Int) {
                    if (position==reasonList.size-1){
                        etReason.isVisible = true
                        reason = ""
                    }
                    else{
                        etReason.isVisible = false
                        reason = reasonList[position].reasonEnglish.toString()
                    }
                }

            }*/
                override fun onClickItem(position: Int) {
                    if (position == reasonList.size - 1) {
                        etReason.isVisible = true
                        reason = ""
                        tvAvailableSize.isVisible = false
                        clSize.isVisible = false
                    } else {
                        etReason.isVisible = false
                        reason = reasonList[position].reasonEnglish.toString()
                            if(isSizeRelatedReason(reason)){
                                tvAvailableSize.isVisible = true
                                clSize.isVisible = true
                            }
                        else{
                                tvAvailableSize.isVisible = false
                                clSize.isVisible = false
                            }
                    }
                }
            }
            )
            rvReason.adapter = reasonAdapter
            getReasonList(1)

            tvToolbar.setOnClickListener {
                exchangeDialog?.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(etReason.isVisible){
                    if (etReason.text?.trim().toString().isEmpty()){
                        showToast("Please enter reason")
                    }
                    else if (rvSize.isVisible && size == "") {
                        showToast("Please select size")
                    }
                    else{
                        reason = etReason.text?.trim().toString()
                        exchangeOrder(reason,size,id)
                    }
                }
                else if(reason==""){
                showToast("Please select reason")
            }
                else if (rvSize.isVisible && size == "") {
                    showToast("Please select size")
                }
            else {
                exchangeOrder(reason,size,id)
            }

            }
            if (sizeList.isEmpty()) {
                viewModel.getSizeList()
                ProgressDialogUtils.getInstance().showProgress(requireActivity())
                isSizeObserver = true
            }
        }

        exchangeDialog?.show()
    }

    private fun isSizeRelatedReason(reason: String?): Boolean {
        if (reason.isNullOrEmpty()) return false

        return reason.contains("size", ignoreCase = true) ||
                reason.contains("fit", ignoreCase = true)
    }

    private fun cancelData(){
        binding?.apply {
            relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E70000"))
            tvStatus.setTextColor(Color.parseColor("#E70000"))
            tvStatus.text = "Cancelled"

            btnInvoice.isVisible = false
            tvOnTheWay.isVisible = false
            ivOnTheWay.isVisible = false
            ivReturned.setImageResource(R.drawable.ic_cancelled)
            tvReturned.text = "Cancelled"
            tvReturned.setTextColor(Color.parseColor("#D72E04"))
            llReturnDetail.isVisible = true
            tvExchangeSize.isVisible = false
            tvExchangeSizeValue.isVisible = false
            tvUploadedImage.isVisible = false
            relUploadImage.isVisible = false
            tvReturnDetails.text = "Cancellation details"
            tvReturnOn.text = "Cancelled on"
            tvReturnRequestId.text = "Cancelled Request ID"
            tvReturnRequestIdValue.text = "#"+orderData?.id
            tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderData?.updatedAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mm a")
            tvReturnReasonValue.text = orderData?.cancelReason
        }
    }

    private fun returnData(){
        binding?.apply {
            if (orderData?.deliveryStatus=="1"){
                if (orderData?.rEApprove == "1" && orderData?.returnType == "1" && orderData?.order?.assignStatus == "0") {
                    ivInProcess.setImageResource(R.drawable.ic_order_picked)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                    ivReturned.setImageResource(R.drawable.ic_order_return)
                } else if (orderData?.rEApprove == "1" && orderData?.returnType == "1" && orderData?.order?.assignStatus == "1") {
                    ivInProcess.setImageResource(R.drawable.ic_order_picked)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                    ivReturned.setImageResource(R.drawable.ic_order_return)
                }
            }
           else  if (orderData?.deliveryStatus=="7"){
                btnInvoice.isVisible = false
                relStatus.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                tvStatus.text = "Ongoing"
                ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                ivReturned.setImageResource(R.drawable.ic_order_return)
            }
            else{

             if (orderData?.r_e_type=="0"&&orderData?.p_orderdetails_id=="0"&&orderData?.rEApprove=="0"&&orderData?.returnType=="1"){
                 clOrderStatus.isVisible = false
                 tvStatus.text = "Return Req.."
                 tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                 relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                 ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                 ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                 ivReturned.setImageResource(R.drawable.ic_order_return)

            }

            else if (orderData?.r_e_type=="0"&&orderData?.p_orderdetails_id=="0"&&orderData?.rEApprove=="1"&&orderData?.returnType=="1"){
                 tvStatus.text = "Returned"
                 tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                 relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))

                 ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                 ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                 ivReturned.setImageResource(R.drawable.ic_order_return_selected)
            }
                else{
                 clOrderStatus.isVisible = false
                 tvStatus.text = "Return Req.."
                 tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                 relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                 ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                 ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                 ivReturned.setImageResource(R.drawable.ic_order_return)
             }

            }
            btnInvoice.isVisible = false


            tvOrderStatus.text = "Return Status"
            tvPicked.text = "In Process"
            tvOnTheWay.text = "Picked Up"
            tvReturned.text = "Completed"

            llReturnDetail.isVisible = true
            tvExchangeSize.isVisible = false
            tvExchangeSizeValue.isVisible = false
            tvReturnRequestIdValue.text = "#"+orderData?.id
            tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderData?.updatedAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mm a")
            tvReturnReasonValue.text = orderData?.returnReason
            if (orderData?.returnImage?.size!=0
            ) {
                relUploadImage.isVisible = true
                tvUploadedImage.isVisible = true
                ivReturnImage.load(orderData?.returnImage?.get(0)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
            } else {
                tvUploadedImage.isVisible = false
                relUploadImage.isVisible = false
            }
        }
    }

    private fun exchangeData(){
        binding?.apply {

 if (orderData?.r_e_type=="0"&&orderData?.p_orderdetails_id=="0"&&orderData?.rEApprove=="1"&&orderData?.returnType=="2"){

         if (orderData?.exchangeOrderDetails?.deliveryStatus=="7"){
                    relStatus.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#17854C"))
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.text = "Ongoing"
                    ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                    ivReturned.setImageResource(R.drawable.ic_order_return)
                }
                else if (orderData?.exchangeOrderDetails?.deliveryStatus=="3"){
                    tvStatus.text = "Exchanged"
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
             relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                    ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                    ivReturned.setImageResource(R.drawable.ic_order_return_selected)
                }
                else{
                    tvStatus.text = "Ongoing"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#17854C"))
                    ivInProcess.setImageResource(R.drawable.ic_order_picked)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                    ivReturned.setImageResource(R.drawable.ic_order_return)
                }


            }

            else if (orderData?.r_e_type=="0"&&orderData?.p_orderdetails_id=="0"&&orderData?.rEApprove=="0"&&orderData?.returnType=="2"){
     clOrderStatus.isVisible = false
     relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
     tvStatus.text = "Exchange Req.."
     tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
     ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
     ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
     ivReturned.setImageResource(R.drawable.ic_order_return)

            }

            else if (orderData?.r_e_type=="1"&&orderData?.p_orderdetails_id=="1"&&orderData?.rEApprove=="1"&&orderData?.returnType=="2"){
     tvStatus.text = "Exchanged"
     tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
     relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
     ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
     ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
     ivReturned.setImageResource(R.drawable.ic_order_return_selected)

            }
            else{
     clOrderStatus.isVisible = false
     relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
     tvStatus.text = "Exchange Req.."
     tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
     ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
     ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
     ivReturned.setImageResource(R.drawable.ic_order_return)
 }

            tvPicked.text = "In Process"
            tvOnTheWay.text = "Pick & \n Exchange"
            tvReturned.text = "Completed"

            tvOrderStatus.text = "Exchange Status"
            llReturnDetail.isVisible = true
            tvExchangeSize.isVisible = true
            tvExchangeSizeValue.isVisible = true
            tvUploadedImage.isVisible = false
            relUploadImage.isVisible = false
            tvReturnDetails.text = "Exchange Details"
            tvReturnOn.text = "Exchange on"
            tvReturnRequestId.text = "Exchange Request ID"
            tvReturnRequestIdValue.text = "#"+orderData?.id
            tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderData?.updatedAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mm a")
            tvExchangeSizeValue.text =  orderData?.exchangeNewSize
            tvReturnReasonValue.text =  orderData?.exchangeReason

        }
    }

}