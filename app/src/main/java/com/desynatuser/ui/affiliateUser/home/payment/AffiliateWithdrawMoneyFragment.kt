package com.desynatuser.ui.affiliateUser.home.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.adapter.BankAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliateWithdrawMoneyBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AffiliateWithdrawMoneyFragment : BaseFragment() {
    private var binding: FragmentAffiliateWithdrawMoneyBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var profileData : GetAffiliateUserProfileResponse.Data?=null
    private var bankList = ArrayList<GetAffiliateUserProfileResponse.Data.BankDetails>()
    private var bankAdapter: BankAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateWithdrawMoneyBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Withdraw Money"
            profileData = SharedPreference.get(requireActivity()).getAffiliateUser()
            bankList.clear()
            profileData?.bankDetails?.let { bankList.addAll(it) }
            bankAdapter?.notifyDataSetChanged()

            bankAdapter = BankAdapter(requireActivity(),bankList,isShow=false,object : BankAdapter.OnClick{
                override fun onClick(position: Int,type: Int) {
                    when(type){
                      3->{

                      }
                    }
                }

            })

            rvBank.adapter = bankAdapter
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSubmit.setOnClickListener {
                CommonMethod.showSuccessDialog(requireActivity(),"Request sent successfully!"){

                }
            }
        }
    }

    override fun observe() {
    }

}
