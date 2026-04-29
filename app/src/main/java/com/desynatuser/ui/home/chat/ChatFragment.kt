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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatuser.R
import com.desynatuser.adapter.ChatAdapter
import com.desynatuser.adapter.ImagePreviewAdapter
import com.desynatuser.adapter.UploadMsgImageAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.AddCustomRequestDialogBinding
import com.desynatuser.databinding.AddDeliveryAddressDialogBinding
import com.desynatuser.databinding.FragmentChatBinding
import com.desynatuser.databinding.ImagePreviewDialogBinding
import com.desynatuser.databinding.ViewCustomOrderDetailDialogBinding
import com.desynatuser.ui.home.chat.response.ChatDetailResponse
import com.desynatuser.ui.home.chat.response.ChatMessage
import com.desynatuser.ui.home.chat.response.CreateCustomOrderResponse
import com.desynatuser.ui.home.chat.response.GetChatListResponse
import com.desynatuser.ui.home.chat.response.NormalChatResponse
import com.desynatuser.ui.home.chat.response.SendMessageRequest
import com.desynatuser.ui.home.profile.response.GetAddressResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.desynatuser.utils.translation.ChatLanguageDetector
import com.desynatuser.utils.translation.TranslateRequest
import com.desynatuser.utils.translation.TranslatorRetrofit
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant

@AndroidEntryPoint
class ChatFragment : BaseFragment() {
    private var binding: FragmentChatBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = false
    private var isCartObserver = false
    private var isAccept = false
    private var isImageObserver = false
    private var isCustomObserver = false
    private var isAddressObserver = false
    private var chatAdapter: ChatAdapter? = null
    private var chatList = ArrayList<ChatMessage>()

    private var uploadMsgImageAdapter: UploadMsgImageAdapter? = null
    private var uploadImageList = ArrayList<UploadImageType>()

    var from = ""
    var sellerId = ""
    var customOrderInfo: CreateCustomOrderResponse.Data? = null
    var normalChatData: NormalChatResponse.Data? = null
    var chatData: GetChatListResponse.Data? = null

    private var uniqueId = ""
    var senderId = ""
    var receiverId = ""
    var isFirstMessageLoaded = false
    private var selectedAddress: GetAddressResponse.Data? = null
    private var selectedQuantity: String? = null
    private var customDetail: String? = null
    private var addCustomRequestDialog: Dialog? = null
    private var addCustomRequestDialogBinding: AddCustomRequestDialogBinding? = null

    private var chatDetailData: ChatDetailResponse.Data.Record? = null

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
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
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

            parentFragmentManager.setFragmentResultListener(
                "address_request",
                viewLifecycleOwner
            ) { _, bundle ->

                val addressJson = bundle.getString("address")
                selectedAddress = Gson().fromJson(addressJson, GetAddressResponse.Data::class.java)
                addCustomRequestDialog()
            }


            from = arguments?.getString("from").toString()
            if (from == "productDetail") {

                val itemJson = arguments?.getString("customOrderData")
                if (!itemJson.isNullOrEmpty()) {
                    customOrderInfo =
                        Gson().fromJson(itemJson, CreateCustomOrderResponse.Data::class.java)
                    uniqueId = customOrderInfo?.uniqueId.toString()
                    receiverId = customOrderInfo?.sellerId.toString()
                }

            } else if (from == "store") {
                val itemJson = arguments?.getString("normalChatData")
                if (!itemJson.isNullOrEmpty()) {
                    normalChatData = Gson().fromJson(itemJson, NormalChatResponse.Data::class.java)
                    uniqueId = normalChatData?.uniqueId.toString()
                    receiverId = normalChatData?.sellerId.toString()
                }
            } else {
                val itemJson = arguments?.getString("chatData")
                if (!itemJson.isNullOrEmpty()) {
                    chatData = Gson().fromJson(itemJson, GetChatListResponse.Data::class.java)
                    uniqueId = chatData?.uniqueId.toString()
                    receiverId = chatData?.sellerId.toString()
                }
            }

            chatAdapter = ChatAdapter(requireActivity(), chatList, object : ChatAdapter.ChatClick {
                override fun onChatClick(position: Int) {
                    chatList[position].message.toSafeString()?.let { imagePreviewDialog(it) }
                }

            })
            rvChat.adapter = chatAdapter

            if (uniqueId!=""){
            markAllRead()
            }

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

                    if (message.by.toSafeString() != "user" && message.read.toSafeInt() == 0 && message.id.toSafeString() != null && message.id.toSafeString() != "") {
                        markMessageRead(uniqueId, message.id.toSafeString())
                    }
                },
                onMessageUpdated = { updatedMessage ->
                    Log.e("update", "initView: ${updatedMessage}")

                    val index =
                        chatList.indexOfFirst { it.id.toSafeString() == updatedMessage.id.toSafeString() }
                    Log.e("update", "id: ${index}")
                    Log.e("update", "id: ${chatList[index]}")
                    if (index != -1) {
                        chatList[index] = updatedMessage
                        chatAdapter?.notifyItemChanged(index)
                    }
                }
            )

            uploadImageList.clear()
            uploadMsgImageAdapter = UploadMsgImageAdapter(
                requireActivity(),
                uploadImageList,
                object : UploadMsgImageAdapter.Click {
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
        if (uniqueId != "") {
            getChat()
        }

    }

    private fun setQuotationData() {
        binding?.apply {
            when (chatDetailData?.action) {
                "0" -> {

                    if (chatDetailData?.customDetails != null) {
                        ivViewRequest.isVisible = false
                        ivWaiting.isVisible = true
                        ivSendReq.isVisible = false
                        clCustomerRequest.isVisible = true
                        clQuotationDetail.isVisible = false
                        tvAccept.isVisible = false
                        tvReject.isVisible = false
                        btnPayNow.isVisible = false
                    } else {
                        ivViewRequest.isVisible = false
                        ivWaiting.isVisible = false
                        ivSendReq.isVisible = true
                        clCustomerRequest.isVisible = false
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

                "4" -> {
                    ivViewRequest.isVisible = true
                    ivWaiting.isVisible = false
                    ivSendReq.isVisible = false
                    tvAccept.isVisible = false
                    tvReject.isVisible = false
                    btnPayNow.isVisible = false
                    clCustomerRequest.isVisible = false
                    clQuotationDetail.isVisible = false
                }

                "5" -> {
                    ivViewRequest.isVisible = true
                    ivWaiting.isVisible = false
                    ivSendReq.isVisible = false
                    tvAccept.isVisible = false
                    tvReject.isVisible = false
                    if (chatDetailData?.productBuyStatus == "1") {
                        btnPayNow.isVisible = false
                        clCustomerRequest.isVisible = false
                        clQuotationDetail.isVisible = true
                        tvOrderPlaced.isVisible = true
                    }
                    else if(chatDetailData?.addToCartStatus != "1") {
                        btnPayNow.text = "Add to Bag"
                        btnPayNow.isVisible = true
                        clCustomerRequest.isVisible = false
                        clQuotationDetail.isVisible = true
                        tvOrderPlaced.isVisible = false
                    }
                    else {
                        btnPayNow.text = "Go to Bag"
                        clCustomerRequest.isVisible = false
                        clQuotationDetail.isVisible = true
                        btnPayNow.isVisible = true
                        tvOrderPlaced.isVisible = false
                    }

                }

            }
            tvTotalQuantityValue.text = chatDetailData?.quantity.toString()
            tvCustomDetail.text = chatDetailData?.customDetails.toString()
            tvRequestId.text = "Request Id #${chatDetailData?.id}"


            tvPerItemPrice.text = "Item Price (${CommonMethod.formatCurrencyAmount(requireActivity(),chatDetailData?.price ?: 0.0)} X ${chatDetailData?.quantity})"
            val itemPrice = chatDetailData?.price ?: 0.0
            val quantity = chatDetailData?.quantity?.toIntOrNull() ?: 1
            val subTotal = itemPrice * quantity
            tvPerItemPriceValue.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),subTotal)}"
            tvShippingFeesValue.text =
                "${CommonMethod.formatCurrencyAmount(requireActivity(),chatDetailData?.shipping ?: 0.0)}"
            tvTaxValue.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),chatDetailData?.tax ?: 0.0)}"
            tvTotalAmountValue.text =
                "${CommonMethod.formatCurrencyAmount(requireActivity(),chatDetailData?.totalAmount ?: 0.0)}"

            tvProductName.text = chatDetailData?.productVariation?.productDetails?.productNameEn
            tvSizeValue.text = chatDetailData?.productVariation?.size?.name.toString()
            ivColor.setColorFilter(Color.parseColor(chatDetailData?.productVariation?.color?.hexCode))

            tvPrice.text =
                "${CommonMethod.formatCurrencyAmount(requireActivity(),chatDetailData?.productVariation?.actualPrice ?: 0.0)}"
            tvDiscountPrice.text =
                "${CommonMethod.formatCurrencyAmount(requireActivity(),chatDetailData?.productVariation?.finalPrice ?: 0.0)}"

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
                    val isArabic = ChatLanguageDetector.isArabic(textMessage)

                    if (isArabic) {
                        // Arabic typed → translate to English
                        callGoogleTranslatorApi(
                            input = textMessage,
                            source = "ar",
                            target = "en"
                        )
                    } else {
                        // English typed → translate to Arabic
                        callGoogleTranslatorApi(
                            input = textMessage,
                            source = "en",
                            target = "ar"
                        )
                    }
                }

                if (hasImages) {
                    uploadImageList.forEach { image ->
                        sendMessage(
                            uniqueId = uniqueId,
                            message = image.imageUrl,
                            messageAr = image.imageUrl,
                            type = "1"
                        )
                    }
                    uploadImageList.clear()
                    uploadMsgImageAdapter?.notifyDataSetChanged()
                    val map = HashMap<String, Any>()
                    map["sender_id"] = SharedPreference.get(requireActivity()).getUser()?.id.toString()
                    map["sender_type"] = 2
                    map["receiver_id"] = receiverId
                    map["receiver_type"] = 3
                    map["title_english"] = "New Attachement"
                    map["description_english"] = "You have a new message"
                    map["notification_type"] = "chat_message"

                    sendNotification(map)
                }
            }

            ivAttachment.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            btnPayNow.setOnClickListener {
                if(chatDetailData?.addToCartStatus != "1") {
                   addToCartCustomOrder()
                }
                else{
                    findNavController().navigate(
                        R.id.action_chatFragment_to_myBagFragment)
                }

            }

            tvAccept.setOnClickListener {
                acceptRejectQuotation("5")
            }

            tvReject.setOnClickListener {
                acceptRejectQuotation("4")
            }

            ivViewRequest.setOnClickListener {
                viewCustomOrderDialog()
            }

            ivSendReq.setOnClickListener {
                addCustomRequestDialog()
            }
        }
    }

    private fun setData() {
        binding?.apply {
            tvToolbar.text =
                chatDetailData?.sellerDetails?.firstName + " " + chatDetailData?.sellerDetails?.lastName
            toolbar.text =
                chatDetailData?.sellerDetails?.firstName + " " + chatDetailData?.sellerDetails?.lastName

            if (chatDetailData?.type == "2") {
                toolbar.isVisible = false
                clQuotation.isVisible = true
                setQuotationData()
            } else {
                toolbar.isVisible = true
                clQuotation.isVisible = false
            }

        }

    }

    private fun viewCustomOrderDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding =
            ViewCustomOrderDetailDialogBinding.inflate(LayoutInflater.from(requireActivity()))
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

            tvTotalQuantityValue.text = chatDetailData?.quantity.toString()
            if (chatDetailData?.orderCreateStatus?.order?.deliveryDetails!=null){
                tvAddress.isVisible = true
                tvAddressValue.isVisible = true
                tvAddressValue.text = chatDetailData?.orderCreateStatus?.order?.deliveryDetails?.address.toString()
            }
            else{
                tvAddress.isVisible = false
                tvAddressValue.isVisible = false
            }
            tvCustomDetail.text = chatDetailData?.customDetails.toString()

            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }
        }
        dialog?.show()
    }

    private fun addCustomRequestDialog() {
        addCustomRequestDialog = Dialog(requireActivity(), R.style.CustomDialog)
        addCustomRequestDialogBinding =
            AddCustomRequestDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        addCustomRequestDialogBinding?.root.let {
            it?.let { it1 ->
                addCustomRequestDialog?.setContentView(
                    it1
                )
            }
        }

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

            if (selectedQuantity != null) {
                etAddQuantity.setText(selectedQuantity)
                selectedQuantity = null
            }
            if (customDetail != null) {
                etCustomDetails.setText(customDetail)
                customDetail = null
            }

            tvToolbar.setOnClickListener {
                addCustomRequestDialog?.dismiss()
            }

            btnSendRequest.setOnClickListener {
                if (etAddQuantity.text?.trim()?.isEmpty() == true) {
                    showToast("Please enter quantity")
                }
                else if (etCustomDetails.text?.trim()?.isEmpty() == true) {
                    showToast("Please enter custom detail")
                } else {
                    sendCustomOrderRequest()
                }
            }
        }
        addCustomRequestDialog?.show()
    }

    private fun addNewAddressDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding =
            AddDeliveryAddressDialogBinding.inflate(LayoutInflater.from(requireActivity()))
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

    fun sendMessage(uniqueId: String, message: String, messageAr: String?=null, type: String) {
        val ref = database.child("chats").child(uniqueId).push()
        val messageId = ref.key ?: return

        val msg = SendMessageRequest(
            id = messageId,
            by = "user",
            message = message,
            messageAr = messageAr,
            type = type,
            unique_id = uniqueId,
            times = Instant.now().toString(),
            user_id = SharedPreference.get(requireActivity()).getUser()?.id?.toLong() ?: 0L,
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
            if (isObserver || isAddressObserver) {
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
                    imageUri?.let { it1 -> UploadImageType(it1, response.data.url) }
                        ?.let { it2 -> uploadImageList.add(it2) }
                    uploadMsgImageAdapter?.notifyDataSetChanged()

                    imageUri = null
                }
                isImageObserver = false
            }
        }

        viewModel.markAllReadResult.observe(viewLifecycleOwner) { response ->
                ProgressDialogUtils.getInstance().hideProgress()
        }

        viewModel.acceptRejectQuotationResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {

                if (response.data.action == "4") {
                    chatDetailData?.action = response.data.action
                    setQuotationData()
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        "Quotation rejected Successfully"
                    ) {

                    }
                } else {
                    isAccept = true
                    addToCartCustomOrder()

                }

            }
            isObserver = false
        }

        viewModel.addToCartCustomOrderResult.observe(viewLifecycleOwner) { response ->
            if (isCartObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (isAccept){
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        "Quotation accepted Successfully"
                    ) {
                        findNavController().navigate(R.id.action_chatFragment_to_myBagFragment)
                    }
                }
                else{
                    findNavController().navigate(R.id.action_chatFragment_to_myBagFragment)
                }

                isAccept = false
                isCartObserver = false
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
                chatDetailData?.action = response.data.action
                chatDetailData?.customDetails = response.data.customDetails
                chatDetailData?.quantity = response.data.quantity
                setQuotationData()
                CommonMethod.showSuccessDialog(requireActivity(), "Request Sent successfully!") {

                    addCustomRequestDialog?.dismiss()
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

    }

    private fun sendCustomOrderRequest() {
        val map = HashMap<String, Any>()
        map["unique_id"] = uniqueId
        map["quantity"] = addCustomRequestDialogBinding?.etAddQuantity?.text?.trim().toString()
        map["custom_details"] =
            addCustomRequestDialogBinding?.etCustomDetails?.text?.trim().toString()
        viewModel.sendCustomOrderRequest(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isCustomObserver = true
    }

    private fun acceptRejectQuotation(status: String) {
        val map = HashMap<String, Any>()
        map["unique_id"] = uniqueId
        map["status"] = status
        viewModel.acceptRejectQuotation(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getAddress() {
        viewModel.getAddress()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isAddressObserver = true
    }

    private fun addToCartCustomOrder() {
        val map = HashMap<String, Any>()
        map["unique_id"] = uniqueId
        viewModel.addToCartCustomOrder(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isCartObserver = true
    }

    private fun getChat(isLoading: Boolean = true) {
        val map = HashMap<String, Any>()
        map["unique_id"] = uniqueId
        viewModel.chatDetail(map)
        if (isLoading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    fun uploadFile(image: File) {
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    fun markAllRead() {
        val map = HashMap<String, Any>()
        map["unique_id"] = uniqueId
        viewModel.markAllRead(map)

    }

    private fun imagePreviewDialog(image: String) {
        var imageList = arrayListOf(image)
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding =
            ImagePreviewDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            ivCross.setOnClickListener {
                dialog?.dismiss()
            }

            dotsIndicator.isVisible = false

            val imagePreviewAdapter = ImagePreviewAdapter(requireActivity(), imageList)
            viewPager?.adapter = imagePreviewAdapter

        }

        dialog?.show()
    }

    fun callGoogleTranslatorApi(input: String, source: String, target: String) {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())

        lifecycleScope.launch {
            try {
                val response = TranslatorRetrofit.api.googleTranslator(
                    apiKey = requireActivity().getString(R.string.google_translator_api_key),
                    request = TranslateRequest(
                        q = input,
                        source = source,
                        target = target
                    )
                )
                ProgressDialogUtils.getInstance().hideProgress()
                if (response.isSuccessful) {
                    val translatedText =
                        response.body()
                            ?.data
                            ?.translations
                            ?.firstOrNull()
                            ?.translatedText

                    if (source == "ar") {
                        sendMessage(
                            uniqueId = uniqueId,
                            message = translatedText.toString(),   // English
                            messageAr = input,          // Arabic
                            type = "0"
                        )
                        val map = HashMap<String, Any>()
                        map["sender_id"] = SharedPreference.get(requireActivity()).getUser()?.id.toString()
                        map["sender_type"] = 2
                        map["receiver_id"] = receiverId
                        map["receiver_type"] = 3
                        map["title_english"] = translatedText.toString()
                        map["description_english"] = "You have a new message"
                        map["notification_type"] = "chat_message"

                        sendNotification(map)
                    } else {
                        sendMessage(
                            uniqueId = uniqueId,
                            message = input,            // English
                            messageAr = translatedText.toString(), // Arabic
                            type = "0"
                        )
                        val map = HashMap<String, Any>()
                        map["sender_id"] = SharedPreference.get(requireActivity()).getUser()?.id.toString()
                        map["sender_type"] = 2
                        map["receiver_id"] = receiverId
                        map["receiver_type"] = 3
                        map["title_english"] = input
                        map["description_english"] = "You have a new message"
                        map["notification_type"] = "chat_message"

                        sendNotification(map)
                    }



                    binding?.etMessage?.setText("")
                    Log.e("chat_response", translatedText ?: "No translation")
                } else {
                    Log.e("chat_response", "API Error: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("VISION_ERROR", e.message ?: "Error")
            }
        }
    }
    private fun sendNotification(map: HashMap<String,Any>){


        viewModel.sendNotification(map)

        isObserver = true
    }

}
fun Any?.toSafeString(): String {
    return when (this) {
        is String -> this
        is Long -> this.toString()
        is Int -> this.toString()
        is Double -> this.toString()
        else -> ""
    }
}

fun Any?.toSafeInt(): Int {
    return when (this) {
        is Int -> this
        is Long -> this.toInt()
        is String -> this.toIntOrNull() ?: 0
        else -> 0
    }
}

fun Any?.toSafeLong(): Long {
    return when (this) {
        is Long -> this
        is Int -> this.toLong()
        is String -> this.toLongOrNull() ?: 0L
        else -> 0L
    }
}


data class UploadImageType(
    var image:Uri,
            var imageUrl:String
)