package com.desynatuser.ui.affiliateUser.home.payment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
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
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.AffiliatePaymentAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliatePaymentBinding
import com.desynatuser.databinding.PaymentFilterDialogBinding
import com.desynatuser.ui.affiliateUser.home.payment.response.GetAllAffiliateOrderResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.openCustomPreSelectedDatePicker
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.HashMap
import kotlin.getValue
import kotlin.math.abs
import kotlin.toString

@AndroidEntryPoint
class AffiliatePaymentFragment : BaseFragment() {
    private var binding: FragmentAffiliatePaymentBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var paymentAdapter: AffiliatePaymentAdapter? = null
    private var paymentData: GetAllAffiliateOrderResponse.Data?=null
    private var paymentList = ArrayList<GetAllAffiliateOrderResponse.Data.Orders.Data>()
    private var animator: ObjectAnimator?=null

    private var search: Job? = null
    private var lastQuery: String? = null
    var filter:String? = null

    private var fromTimestamp: Long? = null
    private var toTimestamp: Long? = null
    private var fromDateText: String? = null
    private var toDateText: String? = null

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var pagination=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliatePaymentBinding.inflate(layoutInflater, container, false)
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

            animator = ObjectAnimator.ofFloat(ivRefresh, View.ROTATION, 0f, 360f)
            animator?.duration = 800
            animator?.repeatCount = ValueAnimator.INFINITE
            animator?.interpolator = LinearInterpolator()


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
                            getAffiliatePayment(isLoading = false)
                    }

                }
            })

            paymentAdapter = AffiliatePaymentAdapter(requireActivity(), paymentList, object :
                AffiliatePaymentAdapter.OnClick {
                override fun click(position: Int, type: Int) {

                }

            })
            rvPayment.adapter = paymentAdapter


        /*    nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
                val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

                if (lastChild != null) {
                    val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                    if (diff == 0 && scrollY > oldScrollY) {
                        if(totalItem==0)
                            return@setOnScrollChangeListener
                        if (!isObserver && !pagination && currentPage < lastPage) {
                            pagination = true
                            currentPage += 1
                            getAffiliatePayment(false)
                            binding?.progressBar?.isVisible = true
                        }
                    }
                }
            }*/

        }

        getAffiliatePayment()
    }

    override fun initControl() {
        binding?.apply {
            tvWithdraw.setOnClickListener {
                findNavController().navigate(R.id.action_affiliatePaymentFragment_to_affiliateWithdrawMoneyFragment)
            }

            ivRefresh.setOnClickListener {
                animator?.start()
                getAffiliatePayment(false)
            }

            ivFilter.setOnClickListener {
                paymentFilterDialog()
            }

            tvExport.setOnClickListener {

            }
        }

    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.getAffiliatePaymentResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                animator?.cancel()
                binding?.ivRefresh?.setImageResource(R.drawable.ic_refresh)
                response?.let {
                    paymentData = response.data
                    paymentList.clear()
                    response.data?.orders?.data?.let { c -> paymentList.addAll(c) }
                    paymentAdapter?.notifyDataSetChanged()
                    checkData()
                    setData()
                }
                isObserver = false
            }
        }

    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = paymentList.isEmpty()
    }

    private fun getAffiliatePayment(isLoading: Boolean=true){
        val map = HashMap<String,Any>()
        if (lastQuery!=null){
         map["search"] = lastQuery.toString()
        }
            if (filter!=null) {
                map["filter"] = filter.toString()
            }
            if (fromDateText!=null) {
                map["start_date"] = fromDateText.toString()
            }
            if (toDateText!=null) {
                map["end_date"] = toDateText.toString()
            }
        viewModel.getAffiliatePayment(map)
        if (isLoading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun setData(){
        binding?.apply {
            val amount = paymentData?.totalEarning?.toDoubleOrNull() ?: 0.0

            tvAvailable.text = CommonMethod.formatCurrencyAmount(
                requireActivity(),
                abs(amount)
            )

        }
    }

    private fun paymentFilterDialog() {
        var fromTemp: Long? = null
        var toTemp: Long? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = PaymentFilterDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.white)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            if (fromDateText!=null){
                fromTemp = fromTimestamp
                etFrom.text= CommonMethod.formatDate(fromDateText.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            if (toDateText!=null){
                toTemp = toTimestamp
                etTo.text= CommonMethod.formatDate(toDateText.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            when (filter) {
                "2" -> radioGroup.check(R.id.rbPending)
                "3" -> radioGroup.check(R.id.rbDelivered)
                "4" -> radioGroup.check(R.id.rbCancelled)

            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                filter = when (checkedId) {
                    R.id.rbPending -> "2"
                    R.id.rbDelivered -> "3"
                    R.id.rbCancelled -> "4"
                    else -> null
                }
            }

            etFrom.setOnClickListener {
                val initialDate = fromTemp ?: Calendar.getInstance().timeInMillis
                openCustomPreSelectedDatePicker(requireActivity(), initialDate = initialDate) { date, timestamp ->
                    fromTemp = timestamp
                    etFrom.text = date
                    toTimestamp = null
                    toDateText = null
                    etTo.text = ""
                }
            }

            etTo.setOnClickListener {
                if (fromTemp == null) {
                    showToast(getString(R.string.please_select_from_date_first))
                    return@setOnClickListener
                }

                val initialDate = toTemp ?: fromTemp
                openCustomPreSelectedDatePicker(
                    requireActivity(),
                    minDate = fromTemp,initialDate
                ) { date, timestamp ->
                    toTemp = timestamp
                    etTo.setText(date)
                }
            }

            tvToolbar.setOnClickListener {
                toDateText = null
                fromDateText = null
                filter = null
                toTimestamp = 0L
                fromTimestamp = 0L
                dialog.dismiss()
            }

            tvClear.setOnClickListener {
                filter = null
                fromDateText = null
                fromTimestamp = null
                toDateText = null
                toTimestamp = null
                getAffiliatePayment()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                if (etFrom.text.isNotEmpty()){
                    toTimestamp = toTemp

                    fromDateText = CommonMethod.formatDate(etFrom.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }

                if (etTo.text.isNotEmpty()){
                    fromTimestamp = fromTemp
                    toDateText = CommonMethod.formatDate(etTo.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }
                getAffiliatePayment()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

}
