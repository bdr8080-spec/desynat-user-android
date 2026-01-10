package com.desynatuser.ui.home.chat

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatuser.R
import com.desynatuser.adapter.ChatAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentChatBinding
import com.desynatuser.databinding.ViewCustomOrderDetailDialogBinding
import com.desynatuser.ui.home.chat.response.ChatMessage
import com.desynatuser.ui.home.chat.response.GetChatListResponse
import com.desynatuser.ui.home.chat.response.SendMessageRequest
import com.desynatuser.ui.home.store.response.Product
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
class ChatFragment : BaseFragment() {
    private var binding: FragmentChatBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var chatAdapter: ChatAdapter?=null
    private var chatList = ArrayList<ChatMessage>()
    var isObserver = false
    var from = ""
    var productId = ""
    var storeName = ""
    var productName = ""
    var productInfo:Product?=null
    var productVariant:Product.ProductVariation?=null
    var chatData:GetChatListResponse.Data?=null

    private var uniqueId = ""

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
            val from = arguments?.getString("from")
            if (from=="productDetail"){
                clQuotation.isVisible = true
                toolbar.isVisible = false
                productId = arguments?.getString("productId").toString()
                storeName = arguments?.getString("storeName").toString()
                productName = arguments?.getString("productName").toString()
                val itemJson = arguments?.getString("productInfo")
                if (!itemJson.isNullOrEmpty()) {
                    productInfo = Gson().fromJson(itemJson, Product::class.java)
                    setQuotationData()
                }
                val itemJsonProductVariant = arguments?.getString("productVariant")
                if (!itemJson.isNullOrEmpty()) {
                    productVariant = Gson().fromJson(itemJsonProductVariant, Product.ProductVariation::class.java)
                    setQuotationData()
                }
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

            listenMessages(uniqueId = uniqueId) { message ->
                chatList.add(message)
                chatAdapter?.notifyItemInserted(chatList.size - 1)
                binding?.rvChat?.scrollToPosition(chatList.size - 1)

                if (message.by != "user" && message.read_status == 0 && message.id != null) {
                    markMessageRead(uniqueId, message.id!!)
                }
            }
        }
    }

    private fun setQuotationData() {
        binding?.apply {
            clQuotation.isVisible = true
            toolbar.isVisible = false
            tvToolbar.text = storeName

            tvProductName.text = productName
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
                findNavController().popBackStack()
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

        }
    }

    override fun observe() {

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
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ViewCustomOrderDetailDialogBinding.inflate(LayoutInflater.from(context))
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
        }
        dialog?.show()
    }


    private val database =
        FirebaseDatabase.getInstance().reference

    fun sendMessage(uniqueId: String, message: String,type:String) {
        val msg = SendMessageRequest(by = "user", message = message, type = type, unique_id = uniqueId, times = Instant.now().toString(), user_id = SharedPreference.get(requireActivity()).getUser()?.id?.toLong()?:0)

        database.child("chats").child(uniqueId).push().setValue(msg)
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
        map["read_status"] = 1

        database.child("chats")
            .child(uniqueId)
            .child(messageId)
            .updateChildren(map)
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
      }*/

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