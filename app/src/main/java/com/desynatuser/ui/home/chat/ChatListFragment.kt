package com.desynatuser.ui.home.chat

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.ChatListAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.ChatFilterDialogBinding
import com.desynatuser.databinding.FragmentChatListBinding
import com.desynatuser.ui.home.chat.response.GetChatListResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : BaseFragment() {
    private var binding: FragmentChatListBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    private var chatAdapter: ChatListAdapter?=null
    private var chatList = ArrayList<GetChatListResponse.Data>()
    private var search: Job? = null
    private var lastQuery: String? = null
    var chatFilter:String? = null
    private var isFromSwipe = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatListBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = getString(R.string.chat)

            chatAdapter = ChatListAdapter(requireActivity(),chatList,object :ChatListAdapter.ChatClick{
                override fun onChatClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("from", "chat")
                        putString("chatData", Gson().toJson(chatList[position]))
                    }
                    findNavController().navigate(R.id.action_chatListFragment_to_chatFragment,bundle)
                }

            })
            rvChat.adapter = chatAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getChat(false)
            }

            lastQuery = etSearch.text.toString().trim()
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        getChat(isLoading = false)

                    }
                }
            })
        }
        getChat(chatList.isEmpty())

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivFilter.setOnClickListener {
                chatFilterDialog()
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.swipeRefreshLayout?.isRefreshing = false
                binding?.progressBar?.isVisible = false
                isFromSwipe = false
                errorMessage?.let { showToast(it) }
                isObserver = false
                isFromSwipe = false
                binding?.swipeRefreshLayout?.isRefreshing = false
            }
        }

        viewModel.getAllChatListResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                    isFromSwipe = false
                }
                chatList.clear()
                chatList.addAll(response.data)
                chatAdapter?.notifyDataSetChanged()
                checkData()
                isObserver = false
            }
        }
    }

    private fun chatFilterDialog() {
        var filter:String? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ChatFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            if (chatFilter!=null) {
                filter = chatFilter.toString()
                when (chatFilter) {
                    "1" -> rbAll.isChecked = true
                    "2" -> rbWaiting.isChecked = true
                    "3" -> rbQuotationReceived.isChecked = true
                    "4" -> rbQuotationAccepted.isChecked = true
                }
            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                filter = when (checkedId) {
                    R.id.rbAll -> "1"
                    R.id.rbWaiting -> "2"
                    R.id.rbQuotationReceived -> "3"
                    R.id.rbQuotationAccepted -> "4"
                    else -> null
                }
            }

            closeIv.setOnClickListener {
                dialog.dismiss()
            }


            tvClear.setOnClickListener {
                filter = ""
                chatFilter = null
                getChat()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {

                chatFilter = filter
                if (chatFilter==null) {
                    showToast(getString(R.string.please_select_chat_status))
                    return@setOnClickListener
                }
                else{
                    getChat()
                    dialog.dismiss()
                }

            }

        }

        dialog.show()
    }

    private fun getChat(isLoading: Boolean=true){
        val map = HashMap<String, Any>()
        if (lastQuery!=null){
            map["search"] = lastQuery.toString()
        }
        if (chatFilter!=null){
            map["filters"] = chatFilter.toString()
        }
        viewModel.getAllChatList(map)
        if (isLoading){
            binding?.progressBar?.isVisible = true

        }
        isObserver = true
    }


    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = chatList.isEmpty()
    }

}