package com.desynatuser.ui.home.myWallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.WalletTransactionAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentMyWalletBinding
import com.desynatuser.ui.home.myWallet.response.GetWalletHistoryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyWalletFragment : BaseFragment() {
    private var binding: FragmentMyWalletBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var walletTransactionAdapter: WalletTransactionAdapter?=null
    private var walletTransactionList = ArrayList<GetWalletHistoryResponse.Data.History>()
    private var walletData : GetWalletHistoryResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyWalletBinding.inflate(layoutInflater, container, false)
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
            walletTransactionAdapter = WalletTransactionAdapter(requireActivity(),walletTransactionList,object : WalletTransactionAdapter.WalletTransactionClick{
                override fun onWalletTransactionClick(position: Int) {

                }

            })
            rvWalletTransaction.adapter = walletTransactionAdapter
        }

        getWalletHistory()
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivReedeemGiftCard.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "wallet")
                }
                findNavController().navigate(
                    R.id.action_myWalletFragment_to_applyGiftFragment,bundle)
            }

            ivAddMoney.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "wallet")
                }
                findNavController().navigate(
                    R.id.action_myWalletFragment_to_addMoneyInWalletFragment,bundle)
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

        viewModel.getWalletHistoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                walletData = response.data
                setData()
                isObserver = false
            }
        }

    }

    private fun setData() {
        binding?.apply {
            walletTransactionList.clear()
            walletData?.history?.let { walletTransactionList.addAll(it) }
            walletTransactionAdapter?.notifyDataSetChanged()

            tvAvailable.text = "${CommonMethod.formatPrice(walletData?.walletBalance?:0.0)} KD"
        }
    }

    private fun getWalletHistory() {
        viewModel.getWalletHistory()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

}