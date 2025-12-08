package com.desynatuser.ui.home.giftCard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentPaymentMethodBinding
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodFragment : BaseFragment() {
    private var binding: FragmentPaymentMethodBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    private var from = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentMethodBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Payment Method"

            if(from=="gift"){
                relGiftCard.isVisible = false
                tvGiftCard.isVisible = false
            }
            else{
                relGiftCard.isVisible = true
                tvGiftCard.isVisible = true
            }
        }

    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnProceed.setOnClickListener {
                CommonMethod.showSuccessDialog(requireActivity(),"Gift card purchased successfully!"){

                }
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
    }

    fun orderPayment(){
        val map = HashMap<String,Any>()
        map["order_id"] = ""
        map["Payment_method"] = ""//1 myfatoorah 2 wallet 3 Cod
        map["payment_status"] = "" // 0 pending 1 paid 2 cancel or reject 3 refund
    }

}
