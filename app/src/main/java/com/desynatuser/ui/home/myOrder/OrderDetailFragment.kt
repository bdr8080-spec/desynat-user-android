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
import com.desynatuser.adapter.ReasonAdapter
import com.desynatuser.adapter.ReasonSizeAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.CancelOrderDialogBinding
import com.desynatuser.databinding.ExchangeOrderDialogBinding
import com.desynatuser.databinding.FragmentOrderDetailBinding
import com.desynatuser.databinding.RateProductDialogBinding
import com.desynatuser.databinding.ReturnOrderDialogBinding
import com.desynatuser.ui.home.myOrder.response.GetAllOrderResponse
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
    private var orderData : GetAllOrderResponse.Data?=null
    private var orderProductAdapter: OrderProductAdapter?=null
    private var productList = ArrayList<GetAllOrderResponse.Data.OrderDetail>()
    private var orderId = ""

    private var sizeList = ArrayList<GetSizeListResponse.Data>()
    private var reasonList = ArrayList<GetSizeListResponse.Data>()
    private var sizeAdapter: ReasonSizeAdapter? = null
    private var reasonAdapter: ReasonAdapter? = null

    private var returnDialogBinding : ReturnOrderDialogBinding?=null

    var isImageObserver = false
    private lateinit var imagePicker: ImagePicker
    private var imageUrl:String?=null

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
                orderData = Gson().fromJson(itemJson, GetAllOrderResponse.Data::class.java)
                orderId = orderData?.orderId.toString()
            }

            toolbar.tvToolbar.text = "My Order"

            orderProductAdapter = OrderProductAdapter(requireActivity(),productList,object :OrderProductAdapter.ClickOnItem{
                override fun onClickOnItem(position: Int, type: Int) {
                    when(type){
                        1->{
                            cancelOrderDialog()
                        }
                        2->{
                            exchangeOrderDialog()
                        }
                        3->{
                            returnOrderDialog()
                        }
                        4->{
                            rateProductDialog()
                        }
                    }
                }

            })
            rvProduct.adapter = orderProductAdapter

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    returnDialogBinding?.ivImage?.setImageURI(it)
                    uploadFile(CommonMethod.uriToFile(requireActivity(), it))
                }
            }

            setData()
        }
    }

    private fun cancelOrder() {
        CommonMethod.showSuccessDialog(requireActivity(),"Order Cancelled successfully!"){
            findNavController().popBackStack()
        }
    }

    private fun exchangeOrder() {
        CommonMethod.showSuccessDialog(requireActivity(),"Order Exchange Request Sent successfully!"){
            findNavController().popBackStack()
        }
    }

    private fun returnOrder() {
        CommonMethod.showSuccessDialog(requireActivity(),"Order Return Request Sent successfully!"){
            findNavController().popBackStack()
        }
    }

    private fun rateOrder(map:HashMap<String,Any>) {

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
            orderProductAdapter?.notifyDataSetChanged()

            toolbar.tvToolbar.text = "#${orderData?.orderId}"

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

            when(orderData?.orderDetails?.get(0)?.status){//1 all 2 ongoing 3 delivered 4 cancelled 5 return
                "1" -> {
                    btnInvoice.isVisible = false
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#17854C"))
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.text = "New"
                }
                "2" -> {
                    btnInvoice.isVisible = false
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#17854C"))
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.text = "Ongoing"
                }
                "3" -> {
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#16B364"))
                    tvStatus.setTextColor(Color.parseColor("#16B364"))
                    tvStatus.text = "Delivered"

                    btnInvoice.isVisible = true
                }

                "4" -> {
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E70000"))
                    tvStatus.setTextColor(Color.parseColor("#E70000"))
                    tvStatus.text = "Cancelled"

                    btnInvoice.isVisible = false
                    tvOnTheWay.isVisible = false
                    ivOnTheWay.isVisible = false
                    ivReturned.setImageResource(R.drawable.ic_cancelled)
                    tvOnTheWay.text = "Cancelled"
                    tvOnTheWay.setTextColor(Color.parseColor("#D72E04"))
                }
                "5" -> {
                    btnInvoice.isVisible = false
                    relStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF5F1B"))
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    tvStatus.text = "Return Req.."
                }
            }

        }
    }

    override fun initControl() {

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
                    imageUrl = response.data.url
                }
                isImageObserver = false
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

    private fun getOrder(){
        val map = HashMap<String,Any>()
        map["order_id"] = orderId
        viewModel.getOrderDetailInfo(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun rateProductDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RateProductDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            tvSubmit.setOnClickListener {
                val map = HashMap<String,Any>()
                map["product_rating"] = rbProduct.rating
                map["product_review"] = etProductReview.text?.trim().toString()
                map["rating"] = rbStore.rating
                map["review"] = etStoreReview.text?.trim().toString()
                rateOrder(map)
                dialog.dismiss()
            }


        }

        dialog.show()
    }

    private fun cancelOrderDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = CancelOrderDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            reasonAdapter = ReasonAdapter(requireActivity(), reasonList,object :
                ReasonAdapter.OnClick{
                override fun onClickItem(position: Int) {
                }

            })
            rvReason.adapter = reasonAdapter

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }


            btnSubmit.setOnClickListener {
                cancelOrder()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun returnOrderDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        returnDialogBinding = ReturnOrderDialogBinding.inflate(LayoutInflater.from(context))
        returnDialogBinding?.root.let {
            if (it != null) {
                dialog.setContentView(it)
            }
        }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        returnDialogBinding?.apply {

            reasonAdapter = ReasonAdapter(requireActivity(), reasonList,object :
                ReasonAdapter.OnClick{
                override fun onClickItem(position: Int) {
                }

            })
            rvReason.adapter = reasonAdapter

            ivImage.setOnClickListener {

            }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                returnOrder()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun exchangeOrderDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ExchangeOrderDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

             sizeAdapter = ReasonSizeAdapter(requireActivity(), sizeList,object :
                ReasonSizeAdapter.OnClick{
                override fun onClickItem(position: Int) {
                }

            })
            rvSize.adapter = sizeAdapter

            reasonAdapter = ReasonAdapter(requireActivity(), reasonList,object :
                ReasonAdapter.OnClick{
                override fun onClickItem(position: Int) {
                }

            })
            rvReason.adapter = reasonAdapter

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                exchangeOrder()
                dialog.dismiss()
            }
            if (sizeList.isEmpty()) {
                viewModel.getSizeList()
                ProgressDialogUtils.getInstance().showProgress(requireActivity())
                isSizeObserver = true
            }
        }

        dialog.show()
    }

}