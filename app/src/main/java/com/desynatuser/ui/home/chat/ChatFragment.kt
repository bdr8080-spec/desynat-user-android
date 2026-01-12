package com.desynatuser.ui.home.chat

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.desynatuser.adapter.ChatAdapter
import com.desynatuser.adapter.UploadMsgImageAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.AddCustomRequestDialogBinding
import com.desynatuser.databinding.AddDeliveryAddressDialogBinding
import com.desynatuser.databinding.FragmentChatBinding
import com.desynatuser.databinding.ViewCustomOrderDetailDialogBinding
import com.desynatuser.ui.home.chat.response.ChatDetailResponse
import com.desynatuser.ui.home.chat.response.ChatMessage
import com.desynatuser.ui.home.chat.response.CreateCustomOrderResponse
import com.desynatuser.ui.home.chat.response.GetChatListResponse
import com.desynatuser.ui.home.chat.response.NormalChatResponse
import com.desynatuser.ui.home.chat.response.SendMessageRequest
import com.desynatuser.ui.home.profile.response.GetAddressResponse
import com.desynatuser.ui.home.store.response.Product
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.time.Instant

@AndroidEntryPoint
class ChatFragment : BaseFragment() {
    private var binding: FragmentChatBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private var isImageObserver = false
    private var isCustomObserver = false
    private var isAddressObserver = false
    private var chatAdapter: ChatAdapter?=null
    private var chatList = ArrayList<ChatMessage>()

    private var uploadMsgImageAdapter: UploadMsgImageAdapter?=null
    private var uploadImageList = ArrayList<UploadImageType>()

    var from = ""
    var sellerId = ""
    var customOrderInfo: CreateCustomOrderResponse.Data?=null
    var normalChatData: NormalChatResponse.Data?=null
    var productInfo: Product?=null
    var productVariant: Product.ProductVariation?=null
    var chatData:GetChatListResponse.Data?=null

    private var uniqueId = ""
    var isFirstMessageLoaded = false
    private var selectedAddress : GetAddressResponse.Data?=null
    private var addCustomRequestDialog:Dialog?=null
    private var addCustomRequestDialogBinding:AddCustomRequestDialogBinding?=null

    private var chatDetailData : ChatDetailResponse.Data.Record?=null

    private var imageUri: Uri? = null

    private lateinit var imagePicker: ImagePicker
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
        binding = FragmentChatBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        binding?.apply {
            CommonMethod.detectGestureMode(root, viewBottom)

            parentFragmentManager.setFragmentResultListener("address_request", viewLifecycleOwner) { _, bundle ->

                val addressJson = bundle.getString("address")
                selectedAddress = Gson().fromJson(addressJson, GetAddressResponse.Data::class.java)

                addCustomRequestDialogBinding?.tvAddress?.text = selectedAddress?.address
            }


            from = arguments?.getString("from").toString()
            if (from=="productDetail"){

                clQuotation.isVisible = true
                toolbar.isVisible = false
                etMessage.isEnabled = false
                val itemJson = arguments?.getString("productInfo")
                val sellerName = arguments?.getString("sellerName")
                if (!itemJson.isNullOrEmpty()) {
                    productInfo = Gson().fromJson(itemJson, Product::class.java)
                }
                val itemJsonProductVariant = arguments?.getString("productVariant")
                if (!itemJson.isNullOrEmpty()) {
                    productVariant = Gson().fromJson(itemJsonProductVariant, Product.ProductVariation::class.java)
                }
                tvToolbar.text = sellerName
                toolbar.isVisible = false
                clQuotation.isVisible = true
                clProduct.isVisible = true
                clQuotationDetail.isVisible = false
                clCustomerRequest.isVisible = false
                tvRequestId.isVisible = false
                ivViewRequest.isVisible = false
                ivSendReq.isVisible = true
                ivWaiting.isVisible = false
                tvProductName.text = chatDetailData?.productVariation?.productDetails?.productNameEn
            tvSizeValue.text = productVariant?.size?.name.toString()
            ivColor.setColorFilter(Color.parseColor(productInfo?.color?.hex_code))

                tvPrice.text = "${CommonMethod.formatPrice(chatDetailData?.productVariation?.actualPrice?:0.0)} KD"
                tvDiscountPrice.text = "${CommonMethod.formatPrice(chatDetailData?.productVariation?.finalPrice?:0.0)} KD"

                ivProduct.load(chatDetailData?.productVariation?.variantImage?.get(0)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
            }
            else if(from=="store"){
                val itemJson = arguments?.getString("normalChatData")
                if (!itemJson.isNullOrEmpty()) {
                    normalChatData = Gson().fromJson(itemJson, NormalChatResponse.Data::class.java)
                    uniqueId = normalChatData?.uniqueId.toString()
                }
                clQuotation.isVisible = false
                toolbar.isVisible = true
            }
            else{
                val itemJson = arguments?.getString("chatData")
                if (!itemJson.isNullOrEmpty()) {
                    chatData = Gson().fromJson(itemJson, GetChatListResponse.Data::class.java)
                    uniqueId = chatData?.uniqueId.toString()
                }
                clQuotation.isVisible = false
                toolbar.isVisible = true
            }


            chatAdapter = ChatAdapter(requireActivity(),chatList,object : ChatAdapter.ChatClick{
                override fun onChatClick(position: Int) {

                }

            })
            rvChat.adapter = chatAdapter

          /*  listenMessages(uniqueId = uniqueId) { message ->
                if (!isFirstMessageLoaded) {
                    pbLoader.isVisible = false
                    isFirstMessageLoaded = true
                }

                chatList.add(message)
                chatAdapter?.notifyItemInserted(chatList.size - 1)
                rvChat?.scrollToPosition(chatList.size - 1)

                if (message.by != "user" && message.read == 0 && message.id != null) {
                    markMessageRead(uniqueId, message.id!!)
                }
            }*/

            listenMessages(
                uniqueId = uniqueId,
                onMessageAdded = { message ->
                    if (!isFirstMessageLoaded) {
                        pbLoader.isVisible = false
                        isFirstMessageLoaded = true
                    }

                    chatList.add(message)
                    chatAdapter?.notifyItemInserted(chatList.size - 1)
                    rvChat.scrollToPosition(chatList.size - 1)

                    if (message.by != "user" && message.read == 0 && message.id != null) {
                        markMessageRead(uniqueId, message.id!!)
                    }
                },
                onMessageUpdated = { updatedMessage ->
                    Log.e("update", "initView: ${updatedMessage}" )

                    val index = chatList.indexOfFirst { it.id == updatedMessage.id }
                    Log.e("update", "id: ${index}" )
                    Log.e("update", "id: ${chatList[index]}" )
                    if (index != -1) {
                        chatList[index] = updatedMessage
                        chatAdapter?.notifyItemChanged(index)
                    }
                }
            )

            uploadImageList.clear()
            uploadMsgImageAdapter = UploadMsgImageAdapter(requireActivity(),uploadImageList,object :UploadMsgImageAdapter.Click{
                override fun onClick(position: Int) {
                    uploadImageList.removeAt(position)
                    uploadMsgImageAdapter?.notifyDataSetChanged()
                }

            })
            rvUploadImg.adapter = uploadMsgImageAdapter

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    imageUri = it
                    uploadFile(CommonMethod.uriToFile(requireActivity(), it))
                }
            }

        }
        if (uniqueId!="") {
            getChat()
        }

    }

    private fun setQuotationData() {
        binding?.apply {
            clQuotation.isVisible = true
            toolbar.isVisible = false
            when (chatDetailData?.action) {
                "0" -> {

                    if (chatDetailData?.customDetails!=null){
                        ivViewRequest.isVisible = false
                        ivWaiting.isVisible = true
                        ivSendReq.isVisible = false
                        clCustomerRequest.isVisible = true
                        clQuotationDetail.isVisible = false
                        tvAccept.isVisible = false
                        tvReject.isVisible = false
                        btnPayNow.isVisible = false
                    }
                    else{
                        ivViewRequest.isVisible = false
                        ivWaiting.isVisible = false
                        ivSendReq.isVisible = true
                        clCustomerRequest.isVisible = true
                        clQuotationDetail.isVisible = false
                        tvAccept.isVisible = false
                        tvReject.isVisible = false
                        btnPayNow.isVisible = false
                    }
                }
                "1" -> {
                    ivViewRequest.isVisible = true
                    ivWaiting.isVisible = false
                    ivSendReq.isVisible = false
                    clCustomerRequest.isVisible = false
                    clQuotationDetail.isVisible = true
                    tvAccept.isVisible = true
                    tvReject.isVisible = true
                    btnPayNow.isVisible = false
                }
                "2" -> {
                    ivViewRequest.isVisible = true
                    ivSendReq.isVisible = false
                    ivWaiting.isVisible = false
                    clCustomerRequest.isVisible = false
                    clQuotationDetail.isVisible = true
                    tvAccept.isVisible = true
                    tvReject.isVisible = true
                    btnPayNow.isVisible = false
                }
                "3" -> {
                    ivViewRequest.isVisible = true
                    ivWaiting.isVisible = false
                    ivSendReq.isVisible = false
                    clCustomerRequest.isVisible = false
                    clQuotationDetail.isVisible = true
                    tvAccept.isVisible = false
                    tvReject.isVisible = false
                    btnPayNow.isVisible = true
                }

            }
                tvTotalQuantityValue.text = chatDetailData?.quantity.toString()
                tvAddressValue.text = chatDetailData?.deliveryAddress.toString()
                tvCustomDetail.text = chatDetailData?.customDetails.toString()
            tvRequestId.text = "Request Id #${chatData?.id}"
            tvPerItemPriceValue.text = "${CommonMethod.formatPrice(chatDetailData?.price?:0.0)} KD"
            tvShippingFeesValue.text = "${CommonMethod.formatPrice(chatDetailData?.shipping?:0.0)} KD"
            tvTaxValue.text = "${CommonMethod.formatPrice(chatDetailData?.tax?:0.0)} KD"
            tvTotalAmountValue.text = "${CommonMethod.formatPrice(chatDetailData?.totalAmount?:0.0)} KD"

            tvProductName.text = chatDetailData?.productVariation?.productDetails?.productNameEn
//            tvSizeValue.text = productVariant?.size?.name.toString()
//            ivColor.setColorFilter(Color.parseColor(productInfo?.color?.hex_code))

            tvPrice.text = "${CommonMethod.formatPrice(chatDetailData?.productVariation?.actualPrice?:0.0)} KD"
            tvDiscountPrice.text = "${CommonMethod.formatPrice(chatDetailData?.productVariation?.finalPrice?:0.0)} KD"

            ivProduct.load(chatDetailData?.productVariation?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSend.setOnClickListener {

                val textMessage = etMessage.text?.trim().toString()
                val hasText = textMessage.isNotEmpty()
                val hasImages = uploadImageList.isNotEmpty()

                if (!hasText && !hasImages) {
                    showToast("Please enter message")
                    return@setOnClickListener
                }

                if (hasText) {
                    sendMessage(
                        uniqueId = uniqueId,
                        message = textMessage,
                        type = "0"
                    )
                    etMessage.setText("")
                }

                if (hasImages) {
                    uploadImageList.forEach { image ->
                        sendMessage(
                            uniqueId = uniqueId,
                            message = image.imageUrl,
                            type = "1"
                        )
                    }
                    uploadImageList.clear()
                    uploadMsgImageAdapter?.notifyDataSetChanged()
                }
            }

            ivAttachment.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            btnPayNow.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "chat")
                    putString("chatData", Gson().toJson(chatDetailData))
                }
                findNavController().navigate(R.id.action_chatFragment_to_orderPaymentFragment,bundle)
            }

            tvAccept.setOnClickListener {
                tvAccept.isVisible = false
                tvReject.isVisible = false
                btnPayNow.isVisible = true
            }

            tvReject.setOnClickListener {

            }

            ivViewRequest.setOnClickListener {
                viewCustomOrderDialog()
            }

            ivSendReq.setOnClickListener {
                addCustomRequestDialog()
            }
        }
    }

    private fun setData(){
        binding?.apply {
            tvToolbar.text = chatDetailData?.sellerDetails?.firstName+" "+chatDetailData?.sellerDetails?.lastName
            toolbar.text = chatDetailData?.sellerDetails?.firstName+" "+chatDetailData?.sellerDetails?.lastName

            if (chatDetailData?.type=="2"){
              toolbar.isVisible = false
              clQuotation.isVisible = true
              setQuotationData()
          }
            else{
              toolbar.isVisible = true
              clQuotation.isVisible = false
          }

        }

    }

    private fun viewCustomOrderDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ViewCustomOrderDetailDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            tvTotalQuantityValue.text = customOrderInfo?.quantity.toString()
            tvAddressValue.text = customOrderInfo?.deliveryAddress.toString()
            tvCustomDetail.text = customOrderInfo?.customDetails.toString()

            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }
        }
        dialog?.show()
    }

    private fun addCustomRequestDialog() {
        addCustomRequestDialog = Dialog(requireActivity(), R.style.CustomDialog)
        addCustomRequestDialogBinding = AddCustomRequestDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        addCustomRequestDialogBinding?.root.let { it?.let { it1 ->
            addCustomRequestDialog?.setContentView(
                it1
            )
        } }

        val window: Window = addCustomRequestDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        addCustomRequestDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        addCustomRequestDialogBinding?.apply {
            selectedAddress = SharedPreference.get(requireActivity()).getDefaultAddress()

            if(selectedAddress!=null) {
                if (SharedPreference.get(requireActivity()).getDefaultAddress()!=null){
                    selectedAddress = SharedPreference.get(requireActivity()).getDefaultAddress()
                    tvAddress.text = selectedAddress?.address}
                else{
                    getAddress()
                }
            }

            tvToolbar.setOnClickListener {
                addCustomRequestDialog?.dismiss()
            }

            tvChange.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "chat")
                }
                findNavController().navigate(R.id.action_chatFragment_to_savedAddressFragment,bundle)
            }

            btnSendRequest.setOnClickListener {
                createCustomOrder()
            }
        }
        addCustomRequestDialog?.show()
    }

    private fun addNewAddressDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddDeliveryAddressDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }

            btnAdd.setOnClickListener {
                dialog?.dismiss()
            }
        }
        dialog?.show()
    }

    private val database =
        FirebaseDatabase.getInstance().reference

    fun sendMessage(uniqueId: String, message: String, type: String) {
        val ref = database.child("chats").child(uniqueId).push()
        val messageId = ref.key ?: return

        val msg = SendMessageRequest(
            id = messageId,
            by = "user",
            message = message,
            type = type,
            unique_id = uniqueId,
            times = Instant.now().toString(),
            user_id = SharedPreference.get(requireActivity()).getUser()?.id.toString(),
            read = 0
        )

        ref.setValue(msg)
    }


    fun listenMessages(
        uniqueId: String,
        onMessageAdded: (ChatMessage) -> Unit,
        onMessageUpdated: (ChatMessage) -> Unit
    ) {
        database.child("chats")
            .child(uniqueId)
            .addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(snapshot: DataSnapshot, prev: String?) {
                    snapshot.getValue(ChatMessage::class.java)
                        ?.let { onMessageAdded(it) }
                }

                override fun onChildChanged(snapshot: DataSnapshot, prev: String?) {
                    snapshot.getValue(ChatMessage::class.java)
                        ?.let { onMessageUpdated(it) }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, prev: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }


    /* fun listenMessages(
         uniqueId: String,
         onMessage: (ChatMessage) -> Unit
     ) {
         database.child("chats")
             .child(uniqueId)
             .addChildEventListener(object : ChildEventListener {

                 override fun onChildAdded(snapshot: DataSnapshot, prev: String?) {
                     snapshot.getValue(ChatMessage::class.java)
                         ?.let { onMessage(it) }
                 }

                 override fun onChildChanged(snapshot: DataSnapshot, prev: String?) {}
                 override fun onChildRemoved(snapshot: DataSnapshot) {}
                 override fun onChildMoved(snapshot: DataSnapshot, prev: String?) {}
                 override fun onCancelled(error: DatabaseError) {}
             })
     }*/

    fun markMessageRead(uniqueId: String, messageId: String) {
        val map = HashMap<String, Any>()
        map["read"] = 1

        database.child("chats")
            .child(uniqueId)
            .child(messageId)
            .updateChildren(map)
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isAddressObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
                isAddressObserver = false
            }
        }

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    imageUri?.let { it1 -> UploadImageType(it1,response.data.url) }
                        ?.let { it2 -> uploadImageList.add(it2) }
                    uploadMsgImageAdapter?.notifyDataSetChanged()

                    imageUri = null
                }
                isImageObserver = false
            }
        }

        viewModel.addressResult.observe(viewLifecycleOwner) { response ->
            if (isAddressObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                val savedAddressList = ArrayList<GetAddressResponse.Data>()
                savedAddressList.clear()
                savedAddressList.addAll(response.data)
                if (savedAddressList.isNotEmpty()) {
                    val defaultIndex = savedAddressList.indexOfFirst { it.setAsDefault == "1" }

                    if (defaultIndex != -1) {
                        selectedAddress = savedAddressList[defaultIndex]
                        SharedPreference.get(requireActivity())
                            .saveDefaultAddress(savedAddressList[defaultIndex])
                        addCustomRequestDialogBinding?.tvAddress?.text = selectedAddress?.address
                    }

                    }
                    isAddressObserver = false
                }
            }

        viewModel.sendCustomOrderRequestResult.observe(viewLifecycleOwner) { response ->
            if (isCustomObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),"Request Sent successfully!"){
                    addCustomRequestDialog?.dismiss()
                }
                if (chatDetailData!=null) {
                    chatDetailData?.action = response.data.action
                    chatDetailData?.deliveryAddress = response.data.deliveryAddress
                    chatDetailData?.customDetails = response.data.customDetails
                    chatDetailData?.quantity = response.data.quantity
                    setQuotationData()
                }
                else{
                    getChat()
                }
                isCustomObserver = false
            }
        }

        viewModel.chatDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                chatDetailData = response.data.record
                setData()
                isObserver = false
            }
        }

        viewModel.createCustomOrderResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                uniqueId = response.data.uniqueId
                sendCustomOrderRequest()
                isObserver = false
            }
        }

    }

    private fun sendCustomOrderRequest() {
        binding?.etMessage?.isEnabled = true
        val map = HashMap<String, Any>()
        map["unique_id"] = uniqueId
        map["quantity"] = addCustomRequestDialogBinding?.etAddQuantity?.text?.trim().toString()
        map["custom_details"] = addCustomRequestDialogBinding?.etCustomDetails?.text?.trim().toString()
        map["delivery_address"] = selectedAddress?.id.toString()
        viewModel.sendCustomOrderRequest(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isCustomObserver = true
    }

    private fun getAddress() {
        viewModel.getAddress()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isAddressObserver = true
    }

    private fun getChat(){
        val map = HashMap<String, Any>()
        map["unique_id"] = uniqueId
        viewModel.chatDetail(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun createCustomOrder() {
        val map = HashMap<String, Any>()
        map["product_id"] = productInfo?.productId.toString()
        map["variation_id"] =productVariant?.id.toString()
        viewModel.createCustomOrder(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun uploadFile(image:File){
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    /*  private fun getChat(){
          val map = HashMap<String, Any>()
          map["unique_id"] = ""
          viewModel.chatDetail(map)
          ProgressDialogUtils.getInstance().showProgress(requireActivity())
          isObserver = true
      }

      private fun sendMessage(){
          val map = HashMap<String, Any>()
          map["unique_id"] = ""
          map["message"] = ""
          map["type"] = "0" // 0 text 1 image ,
          map["by"] = "user"
          viewModel.sendMessage(map)
          ProgressDialogUtils.getInstance().showProgress(requireActivity())
          isObserver = true
      }

      private fun chatWithSeller(){
          val map = HashMap<String, Any>()
          map["seller_id"] = ""
          viewModel.chatWithSeller(map)
          ProgressDialogUtils.getInstance().showProgress(requireActivity())
          isObserver = true
      }

      private fun createCustomOrder() {
          val map = HashMap<String, Any>()
          map["product_id"] = productId
          map["variants_id"] = productVariant?.id.toString()
          viewModel.createCustomOrder(map)
          ProgressDialogUtils.getInstance().showProgress(requireActivity())
          isObserver = true
      }
      */

//    private fun sendCustomOrderRequest() {
//        val map = HashMap<String, Any>()
//        map["unique_id"] = productData?.id.toString()
//        map["quantity"] = productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id.toString()
//        map["custom_details"] = productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id.toString()
//        map["delivery_address"] = productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id.toString()
//        viewModel.sendCustomOrderRequest(map)
//        ProgressDialogUtils.getInstance().showProgress(requireActivity())
//        isObserver = true
//    }
//
//    private fun customOrderApplyGiftCard() {
//        val map = HashMap<String, Any>()
//        map["unique_id"] = productData?.id.toString()
//        map["giftcard_id"] = productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id.toString()
//        map["giftcard_amount"] = productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id.toString()
//        map["Payment_method"] = productInfo?.getOrNull(selectedPos)?.productVariation?.getOrNull(selectedSizePos)?.id.toString()
//        viewModel.applyCustomOrderGiftCard(map)
//        ProgressDialogUtils.getInstance().showProgress(requireActivity())
//        isObserver = true
//    }
//
//    private fun customOrderCompletePayment() {
//        val map = HashMap<String, Any>()
//        map["order_id"] = productData?.id.toString()
//        map["Payment_method"] = 3 //1 myfatoorah 2 wallet 3 Cod
//        map["payment_status"] = "1" // 0 pending 1 paid 2 cancel or reject 3 refund
//        viewModel.customOrderPayment(map)
//        ProgressDialogUtils.getInstance().showProgress(requireActivity())
//        isObserver = true
//    }


}

data class UploadImageType(
    var image:Uri,
            var imageUrl:String
)