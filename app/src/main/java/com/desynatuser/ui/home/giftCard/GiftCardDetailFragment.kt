package com.desynatuser.ui.home.giftCard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentGiftCardDetailBinding
import com.desynatuser.ui.home.giftCard.response.GetAllGiftCardResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GiftCardDetailFragment : BaseFragment() {
    private var binding: FragmentGiftCardDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var giftData : GetAllGiftCardResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGiftCardDetailBinding.inflate(layoutInflater,container,false)
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
            val itemJson = arguments?.getString("giftData")
            if (!itemJson.isNullOrEmpty()) {
                giftData = Gson().fromJson(itemJson, GetAllGiftCardResponse.Data::class.java)
                setData()
            }
        }

    }

    override fun initControl() {
        binding?.apply {

            tvToolbar.setOnClickListener {
                findNavController().popBackStack()
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

    private fun setData(){
        binding?.apply {
            tvDate.text = CommonMethod.customizeChangeDateFormat(giftData?.createdAt.toString(), outputFormat = "dd MMM yy hh:mm a")
            ivGift.load(giftData?.giftDetails?.image)
            tvGiftName.text = giftData?.giftDetails?.nameEnglish
            tvFromName.text = giftData?.senderName
            tvWishMessage.text = giftData?.wishMessage
            if (giftData?.notes==null){
                tvNoteMsg.isVisible = false
                tvNote.isVisible = false
            }
            tvNoteMsg.text = giftData?.notes
            tvEmail.text = giftData?.receiverEmail
            tvName.text = giftData?.receiverName
            tvQuantity.text = "Total Quantity- 1"
            tvPrice.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),giftData?.amount?:0.0)}"
            tvAmount.text = "${CommonMethod.formatCurrencyAmount(requireActivity(),giftData?.amount?:0.0)}"
            tvTransaction.text = "Transaction Id: ${giftData?.transactionId}"
            if(giftData?.paymentMethod=="2") {
                tvTransaction.isVisible = false
                ivPay.setImageResource(R.drawable.ic_wallet)
                tvPayBy.text = "Paid by Wallet"
            }
            if(giftData?.transactionId=="wallet") {
                tvTransaction.isVisible = false
                ivPay.setImageResource(R.drawable.ic_wallet)
                tvPayBy.text = "Paid by Wallet"
            }
        }
    }

}
