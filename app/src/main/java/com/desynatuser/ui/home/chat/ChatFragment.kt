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
import com.desynatuser.R
import com.desynatuser.adapter.ChatAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentChatBinding
import com.desynatuser.databinding.ViewCustomOrderDetailDialogBinding
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment() {
    private var binding: FragmentChatBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var chatAdapter: ChatAdapter?=null
    private var chatList = ArrayList<String>()
    var isObserver = false

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
            toolbar.tvToolbar.text = "Chat"

            chatAdapter = ChatAdapter(requireActivity(),chatList,object : ChatAdapter.ChatClick{
                override fun onChatClick(position: Int) {

                }

            })
            rvChat.adapter = chatAdapter
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSend.setOnClickListener {
                if (etMessage.text?.trim()?.isEmpty() == true){
                    showToast("Please enter message")
                }
                else{

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

}