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
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentMyWalletBinding
import com.desynatuser.ui.home.giftCard.response.GetAllGiftCardResponse

class MyWalletFragment : BaseFragment() {
    private var binding: FragmentMyWalletBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    private var walletTransactionAdapter: WalletTransactionAdapter?=null
    private var walletTransactionList = ArrayList<GetAllGiftCardResponse.Data>()

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
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAddMoney.setOnClickListener {

            }

            ivReedeemGiftCard.setOnClickListener {

            }
        }
    }

    override fun observe() {
    }

}