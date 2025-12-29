package com.desynatuser.ui.home.myOrder

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.desynatuser.R
import com.desynatuser.adapter.OrderProductAdapter
import com.desynatuser.adapter.ProductReviewAdapter
import com.desynatuser.adapter.ReasonAdapter
import com.desynatuser.adapter.ReasonSizeAdapter
import com.desynatuser.adapter.SpecificationAdapter
import com.desynatuser.adapter.StoreReviewAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.CancelOrderDialogBinding
import com.desynatuser.databinding.ExchangeOrderDialogBinding
import com.desynatuser.databinding.FragmentOrderDetailBinding
import com.desynatuser.databinding.RateProductDialogBinding
import com.desynatuser.databinding.ReturnOrderDialogBinding
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

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment() {

    private var binding : FragmentOrderDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private var isSizeObserver = false
    private var getOrderData : GetAllOrderResponse.Data?=null
    private var orderData : GetSingleOrderSummaryResponse.Data?=null
    private var orderProductAdapter: OrderProductAdapter?=null
    private var productList = ArrayList<GetSingleOrderSummaryResponse.Data.OrderDetail>()
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
                            returnDialogBinding?.ivImage?.setImageURI(it)
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

    private fun cancelOrder(reason: String) {
        val map = HashMap<String,Any>()
        map["order_id"] = orderData?.id.toString()
        map["reason"] = reason
        viewModel.cancelOrder(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
    }

    private fun exchangeOrder(reason: String,size: String,id:String) {
        exchangeReturn = "2"
        val map = HashMap<String,Any>()
        map["order_id"] = orderData?.id.toString()
        map["orderdetails_id"] = id
        map["type"] = exchangeReturn  // 1 return 2 Exchange
        map["exchange_reason"] = reason
        map["new_size"] = size

        viewModel.returnExchangeOrder(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
    }

    private fun returnOrder(reason:String,id:String) {
        exchangeReturn = "1"
        val map = HashMap<String,Any>()
        map["order_id"] = orderData?.id.toString()
        map["orderdetails_id"] = id
        map["type"] = exchangeReturn  // 1 return 2 Exchange
        map["return_reason"] = reason
        if (returnImage!=null) {
            map["return_image"] = returnImage.toString()
        }
        viewModel.returnExchangeOrder(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
    }

    private fun rateOrder(map:HashMap<String,Any>) {
        viewModel.orderRating(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isObserver = true
    }

    fun uploadFile(image:File){
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    private fun setData() {
        binding?.apply {

            productList.clear()
            orderData?.orderDetails?.let { productList.addAll(it) }
            orderProductAdapter = OrderProductAdapter(requireActivity(),productList,orderData?.deliveryStatus.toString(),object :OrderProductAdapter.ClickOnItem{
                override fun onClickOnItem(position: Int, type: Int) {
                    when(type){
                        1->{
                            cancelOrderDialog()
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

            toolbar.tvToolbar.text = "#${getOrderData?.id}"

            tvPlacedDate.text = CommonMethod.customizeChangeDateFormat(orderData?.createdAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mma")

            tvTotalMRP.text = "${CommonMethod.formatPrice(orderData?.totalMrp?:0.0)} KD"
            tvDiscountOnMRP.text = "${CommonMethod.formatPrice(orderData?.discountMrp?:0.0)} KD"
            tvShippingFee.text = "${CommonMethod.formatPrice(orderData?.shippingFees?:0.0)} KD"
            tvTaxValue.text = "${CommonMethod.formatPrice(orderData?.tax?:0.0)} KD"

            tvDeliveryAddress.text = orderData?.deliveryDetails?.address

            if (orderData?.discountCoupon!=0.0) {
                tvCouponDiscount.isVisible = true
                tvCouponDiscountValue.isVisible = true
            }
            else{
                tvCouponDiscount.isVisible = false
                tvCouponDiscountValue.isVisible = false
            }
            tvCouponDiscountValue.text = "${CommonMethod.formatPrice(orderData?.discountCoupon?:0.0)} KD"

            tvTotalAmountValue.text = "${CommonMethod.formatPrice(orderData?.totalAmount?:0.0)} KD"

            tvSavePrice.text = "You will save ${CommonMethod.formatPrice((orderData?.discountMrp ?: 0.0) + (orderData?.discountCoupon ?: 0.0))} KD on this"


            tvAmount.text = "${CommonMethod.formatPrice(orderData?.totalAmount?:0.0)} KD"

            tvTransaction.text = "Transaction Id: ${orderData?.transactionId}"
            when(orderData?.paymentMethod){
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

            when(orderData?.orderDetails?.get(0)?.deliveryStatus){
                "1" -> {
                    btnInvoice.isVisible = false
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#17854C"))
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    if (orderData?.assignStatus=="1"){
                        tvStatus.text = "Driver Assigned"
                    }
                    else{
                        tvStatus.text = "New"
                    }
                }
                "2" -> {
                    btnInvoice.isVisible = false
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#17854C"))
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.text = "Ongoing"
                    ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                    ivReturned.setImageResource(R.drawable.ic_order_return)
                }
                "3" -> {
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#16B364"))
                    tvStatus.setTextColor(Color.parseColor("#16B364"))
                    tvStatus.text = "Delivered"

                    btnInvoice.isVisible = true
                    when (orderData?.orderDetails?.get(0)?.returnType){
                        "1"->{
                            tvPicked.text = "In Process"
                            tvOnTheWay.text = "Picked Up"
                            tvReturned.text = "Completed"
                            ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                            ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                            ivReturned.setImageResource(R.drawable.ic_order_return)
                        }
                        "2"->{
                            tvPicked.text = "In Process"
                            tvOnTheWay.text = "Pick & \n Exchange"
                            tvReturned.text = "Completed"
                            ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                            ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                            ivReturned.setImageResource(R.drawable.ic_order_return)
                        }
                        else->{
                            ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                            ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                            ivReturned.setImageResource(R.drawable.ic_order_return_selected)
                        }
                    }

                }

                "7" -> {
                    btnInvoice.isVisible = false
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#17854C"))
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.text = "Ongoing"
                    ivInProcess.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                    ivReturned.setImageResource(R.drawable.ic_order_return)

                }

                "4" -> {
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E70000"))
                    tvStatus.setTextColor(Color.parseColor("#E70000"))
                    tvStatus.text = "Cancelled"

                    btnInvoice.isVisible = false
                    tvOnTheWay.isVisible = false
                    ivOnTheWay.isVisible = false
                    ivReturned.setImageResource(R.drawable.ic_cancelled)
                    tvReturned.text = "Cancelled"
                    tvReturned.setTextColor(Color.parseColor("#D72E04"))
                }
                "5" -> {
                    btnInvoice.isVisible = false
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))

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
                }
                "9" -> {
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                    tvStatus.text = "Exchange Req.."
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))

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

            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isImageObserver||isSizeObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
                isImageObserver = false
                isSizeObserver = false
            }
        }

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    when (imageType){
                        1->{
                            returnImage = response.data.url
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
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                cancelDialog?.dismiss()
                CommonMethod.showSuccessDialog(requireActivity(),"Order Cancelled successfully!"){
                    getOrderDetail()
                }
                isObserver = false
            }
        }


        viewModel.returnExchangeOrderResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (exchangeReturn=="1"){
                    returnDialog?.dismiss()
                     CommonMethod.showSuccessDialog(requireActivity(),"Order Return Request Sent successfully!"){

       }
                }
                else if (exchangeReturn=="2") {
                    exchangeDialog?.dismiss()
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        "Order Exchange Request Sent successfully!"
                    ) {

                    }
                }
                getOrderDetail()
                exchangeReturn = ""
                isObserver = false
            }
        }

        viewModel.orderRatingResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),"Review Submitted successfully!"){
                    getOrderDetail()
                }
                isObserver = false
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
        map["order_id"] = orderId
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
        val height = (displayMetrics.heightPixels * 0.8).toInt()

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
                map["order_id"] = orderData?.id.toString()
                map["orderdetails_id"] = id
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

    private fun cancelOrderDialog() {
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
                        cancelOrder(reason)
                    }
                }
                else if(reason==""){
                    showToast("Please select reason")
                }
                else {
                    cancelOrder(reason)
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
            getReasonList(1)

            tvToolbar.setOnClickListener {
                exchangeDialog?.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(etReason.isVisible){
                    if (etReason.text?.trim().toString().isEmpty()){
                        showToast("Please enter reason")
                    }
                    else if(size==""){
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
                else if(size==""){
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

}