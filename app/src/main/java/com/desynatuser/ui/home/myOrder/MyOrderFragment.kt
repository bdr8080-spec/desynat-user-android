package com.desynatuser.ui.home.myOrder

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
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.MyOrderAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentMyOrderBinding
import com.desynatuser.databinding.OrderFilterDialogBinding
import com.desynatuser.ui.home.myOrder.response.GetAllOrderResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.HashMap

@AndroidEntryPoint
class MyOrderFragment : BaseFragment() {
    private var binding : FragmentMyOrderBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    private var isFirstLoad = true
    private var myOrderAdapter: MyOrderAdapter?=null
    private var myOrderList = ArrayList<GetAllOrderResponse.Data>()
    private var filter:String?=null // // 1 all 2 ongoing 3 delivered 4 cancelled 5 return
    private var search: Job? = null
    private var lastQuery: String? = null
    private var orderType = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyOrderBinding.inflate(layoutInflater, container,false)
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
            toolbar.tvToolbar.text = "My Order"

            myOrderAdapter = MyOrderAdapter(requireActivity(),myOrderList,object :MyOrderAdapter.OrderClick{
                override fun onOrderClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("orderData", Gson().toJson(myOrderList[position]))
                    }
                    findNavController().navigate(R.id.action_myOrderFragment_to_orderDetailFragment,bundle)
                }

            })
            rvOrder.adapter = myOrderAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        if (!isObserver)
                            getOrder(false)
                    }

                }
            })

            if (orderType==2) {
                orderType = 2
                getOrder()
                tvOrder.background = null
                tvOrder.setTextColor(requireActivity().getColor(R.color.black))
                tvCustomOrder.setTextColor(requireActivity().getColor(R.color.white))
                tvCustomOrder.setBackgroundResource(R.drawable.selected_right_tab)
                rvOrder.isVisible = true
                checkData()
            }

            else {
                orderType = 1
                getOrder()
                tvOrder.setBackgroundResource(R.drawable.selected_left_tab)
                tvOrder.setTextColor(requireActivity().getColor(R.color.white))
                tvCustomOrder.setTextColor(requireActivity().getColor(R.color.black))
                tvCustomOrder.background = null
                rvOrder.isVisible = true
                checkData()
            }

        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvOrder.setOnClickListener {
                orderType = 1
                getOrder()
                tvOrder.setBackgroundResource(R.drawable.selected_left_tab)
                tvOrder.setTextColor(requireActivity().getColor(R.color.white))
                tvCustomOrder.setTextColor(requireActivity().getColor(R.color.black))
                tvCustomOrder.background = null
                rvOrder.isVisible = true
                checkData()
            }

            tvCustomOrder.setOnClickListener {
                orderType = 2
                getOrder()
                tvOrder.background = null
                tvOrder.setTextColor(requireActivity().getColor(R.color.black))
                tvCustomOrder.setTextColor(requireActivity().getColor(R.color.white))
                tvCustomOrder.setBackgroundResource(R.drawable.selected_right_tab)
                rvOrder.isVisible = true
                checkData()
            }

            ivFilter.setOnClickListener {
                orderFilterDialog()
            }
        }
    }

    private fun getOrder(loading:Boolean = true){
        val map = HashMap<String,Any>()
        map["order_type"] = orderType.toString()
        lastQuery?.let { searchId ->
            map["search"] =
                myOrderList.firstOrNull { order ->
                    order.id.toString() == searchId
                }?.order?.orderId ?: searchId
        }
        if (filter!=null) {
            map["filter"] = filter.toString()
        }
        viewModel.getAllOrder(map)
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.orderListResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                myOrderList.clear()
                myOrderList.addAll(response.data)
                myOrderAdapter?.notifyDataSetChanged()
                checkData()
                isObserver = false
            }
        }
    }

    private fun orderFilterDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = OrderFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            when (filter) {
                "1" -> radioGroup.check(R.id.rbAll)
                "2" -> radioGroup.check(R.id.rbOngoing)
                "3" -> radioGroup.check(R.id.rbDelivered)
                "4" -> radioGroup.check(R.id.rbCancelled)
                "5" -> radioGroup.check(R.id.rbReturn)

            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                filter = when (checkedId) {
                    R.id.rbAll -> "1"
                    R.id.rbOngoing -> "2"
                    R.id.rbDelivered -> "3"
                    R.id.rbCancelled -> "4"
                    R.id.rbReturn -> "5"
                    else -> null
                }
            }


            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            tvClear.setOnClickListener {
                filter = null
                getOrder()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                getOrder()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = myOrderList.isEmpty()
    }

}