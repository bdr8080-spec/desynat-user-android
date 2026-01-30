package com.desynatuser.ui.home.chat

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.isVisible
import coil.load
import com.desynatuser.R
import com.desynatuser.adapter.ChatAdapter
import com.desynatuser.base.BaseActivity
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.ActivityChatBinding
import com.desynatuser.databinding.AddCustomRequestDialogBinding
import com.desynatuser.databinding.AddDeliveryAddressDialogBinding
import com.desynatuser.databinding.ViewCustomOrderDetailDialogBinding
import com.desynatuser.ui.home.chat.response.ChatMessage
import com.desynatuser.ui.home.chat.response.CreateCustomOrderResponse
import com.desynatuser.ui.home.chat.response.GetChatListResponse
import com.desynatuser.ui.home.chat.response.SendMessageRequest
import com.desynatuser.ui.home.store.response.Product
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant

@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    private var binding: ActivityChatBinding? =null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var chatAdapter: ChatAdapter?=null
    private var chatList = ArrayList<ChatMessage>()
    var from = ""
    var customOrderInfo: CreateCustomOrderResponse.Data?=null
    var productInfo: Product?=null
    var productVariant: Product.ProductVariation?=null
    var chatData: GetChatListResponse.Data?=null

    private var uniqueId = ""
    var isFirstMessageLoaded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
        initControl()
        observer()
    }
    override fun initView() {

        binding?.apply {
            CommonMethod.detectGestureMode(root, viewBottom)

            val from = intent?.getStringExtra("from")
            if (from=="productDetail"){
                clQuotation.isVisible = true
                toolbar.isVisible = false
                val itemJson = intent?.getStringExtra("productInfo")
                if (!itemJson.isNullOrEmpty()) {
                    productInfo = Gson().fromJson(itemJson, Product::class.java)
                }
                val itemJsonProductVariant = intent?.getStringExtra("productVariant")
                if (!itemJson.isNullOrEmpty()) {
                    productVariant = Gson().fromJson(itemJsonProductVariant, Product.ProductVariation::class.java)
                }
                val itemJsonCustomOrderData = intent?.getStringExtra("customOrderData")
                if (!itemJsonCustomOrderData.isNullOrEmpty()) {
                    customOrderInfo = Gson().fromJson(itemJsonCustomOrderData, CreateCustomOrderResponse.Data::class.java)
                }
                uniqueId = customOrderInfo?.uniqueId.toString()
                setQuotationData()
            }
            else{
                val itemJson = intent?.getStringExtra("chatData")
                if (!itemJson.isNullOrEmpty()) {
                    chatData = Gson().fromJson(itemJson, GetChatListResponse.Data::class.java)
                    uniqueId = chatData?.uniqueId.toString()
                }
                clQuotation.isVisible = false
                toolbar.isVisible = true
            }


            chatAdapter = ChatAdapter(this@ChatActivity,chatList,object : ChatAdapter.ChatClick{
                override fun onChatClick(position: Int) {

                }

            })
            rvChat.adapter = chatAdapter

            listenMessages(uniqueId = uniqueId) { message ->
                if (!isFirstMessageLoaded) {
                   pbLoader.isVisible = false
                    isFirstMessageLoaded = true
                }

                chatList.add(message)
                chatAdapter?.notifyItemInserted(chatList.size - 1)
                rvChat?.scrollToPosition(chatList.size - 1)

                if (message.by.toSafeString() != "user" && message.read.toSafeInt() == 0 && message.id.toSafeString() != null) {
                    markMessageRead(uniqueId, message.id.toSafeString())
                }
            }
        }
    }

    private fun setQuotationData() {
        binding?.apply {
            clQuotation.isVisible = true
            toolbar.isVisible = false
            tvToolbar.text = customOrderInfo?.productVariation?.productDetails?.productNameEn

            if (customOrderInfo?.customDetails!=null){
                ivViewRequest.isVisible = true
                ivWaiting.isVisible = false
                ivSendReq.isVisible = false

                clCustomerRequest.isVisible = true
                tvTotalQuantityValue.text = customOrderInfo?.quantity.toString()
                tvAddressValue.text = customOrderInfo?.deliveryAddress.toString()
                tvCustomDetail.text = customOrderInfo?.customDetails.toString()
            }
            else{
                ivViewRequest.isVisible = false
                ivWaiting.isVisible = false
                ivSendReq.isVisible = true
                clCustomerRequest.isVisible = false
            }

            tvProductName.text = customOrderInfo?.productVariation?.productDetails?.productNameEn
            tvSizeValue.text = productVariant?.size?.name.toString()
            ivColor.setColorFilter(Color.parseColor(productInfo?.color?.hex_code))

            tvPrice.text = "${productVariant?.actualPrice} KD"
            tvDiscountPrice.text = "${productVariant?.finalPrice} KD"

            ivProduct.load(productInfo?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.setOnClickListener {
                finish()
            }

            ivSend.setOnClickListener {
                if (etMessage.text?.trim()?.isEmpty() == true){
                    showToast("Please enter message")
                }
                else{
                    sendMessage(uniqueId = uniqueId, message = etMessage.text?.trim().toString(), type = "0")
                    etMessage.setText("")
                }
            }

            ivAttachment.setOnClickListener {

            }

            btnPayNow.setOnClickListener {

            }

            tvAccept.setOnClickListener {

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
            var request = 1 //1-new,2-send,3-approved
            when (request) {
                1 -> {
                    ivViewRequest.isVisible = false
                    ivWaiting.isVisible = true
                    clCustomerRequest.isVisible = true
                    clQuotationDetail.isVisible = false
                    tvAccept.isVisible = false
                    tvReject.isVisible = false
                    btnPayNow.isVisible = false
                }
                2 -> {
                    ivViewRequest.isVisible = true
                    ivWaiting.isVisible = false
                    clCustomerRequest.isVisible = false
                    clQuotationDetail.isVisible = true
                    tvAccept.isVisible = true
                    tvReject.isVisible = true
                    btnPayNow.isVisible = false
                }
                3 -> {
                    ivViewRequest.isVisible = true
                    ivWaiting.isVisible = false
                    clCustomerRequest.isVisible = false
                    clQuotationDetail.isVisible = true
                    tvAccept.isVisible = false
                    tvReject.isVisible = false
                    btnPayNow.isVisible = true
                }
            }
        }

    }

    private fun viewCustomOrderDialog() {
        val dialog = Dialog(this, R.style.CustomDialog)
        val dialogBinding = ViewCustomOrderDetailDialogBinding.inflate(LayoutInflater.from(this))
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
        val dialog = Dialog(this, R.style.CustomDialog)
        val dialogBinding = AddCustomRequestDialogBinding.inflate(LayoutInflater.from(this))
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

            relDeliveryAddress.setOnClickListener {
                addNewAddressDialog()
            }

            btnSendRequest.setOnClickListener {
                val map = HashMap<String, Any>()
                map["unique_id"] = uniqueId
                map["quantity"] = etAddQuantity.text?.trim().toString()
                map["custom_details"] = etCustomDetails.text?.trim().toString()
                map["delivery_address"] = tvAddress.text?.trim().toString()
                sendCustomOrderRequest(map)
            }
        }
        dialog?.show()
    }

    private fun addNewAddressDialog() {
        val dialog = Dialog(this, R.style.CustomDialog)
        val dialogBinding = AddDeliveryAddressDialogBinding.inflate(LayoutInflater.from(this))
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
            messageAr = message,
            type = type,
            unique_id = uniqueId,
            times = Instant.now().toString(),
            user_id = SharedPreference.get(this).getUser()?.id?.toLong()?:0,
            read = 0
        )

        ref.setValue(msg)
    }

    fun listenMessages(
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
    }

    fun markMessageRead(uniqueId: String, messageId: String) {
        val map = HashMap<String, Any>()
        map["read"] = 1

        database.child("chats")
            .child(uniqueId)
            .child(messageId)
            .updateChildren(map)
    }

    override fun observer() {
        viewModel.error.observe(this) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.sendCustomOrderRequestResult.observe(this) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(this,"Request Sent successfully!")
                customOrderInfo = response.data
                setQuotationData()
                isObserver = false
            }
        }
    }

    private fun sendCustomOrderRequest(map:HashMap<String,Any>) {

        viewModel.sendCustomOrderRequest(map)
        ProgressDialogUtils.getInstance().showProgress(this)
        isObserver = true
    }


    }