package com.desynatuser.ui.affiliateUser.home.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentAffiliateProfileBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.ui.auth.AuthActivity
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffiliateProfileFragment : BaseFragment() {
    private var binding: FragmentAffiliateProfileBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var profileData : GetAffiliateUserProfileResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateProfileBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        profileData = SharedPreference.get(requireActivity()).getAffiliateUser()
        binding?.apply {
            setData()
        }
    }

    private fun setData() {
        binding?.apply {
            tvName.text = profileData?.fullName
            tvEmail.text = profileData?.email
            Glide.with(requireActivity()).load(profileData?.profileImage).placeholder(R.drawable.ic_user).into(ivProfileImage)
        }
    }

    override fun initControl() {
        binding?.apply {

            ivEdit.setOnClickListener{
                findNavController().navigate(R.id.action_affiliateProfileFragment_to_affiliateProfileDetailFragment)
            }

            rlMyWithdrawals.setOnClickListener{
                findNavController().navigate(R.id.action_affiliateProfileFragment_to_myWithdrawalsFragment)
            }

            relLogout.setOnClickListener {
                Constants.isLogout = true
                CommonMethod.clearAffiliateData(requireActivity())
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        AuthActivity::class.java
                    )
                )
                requireActivity().finish()
            }

            rlSupport.setOnClickListener {
                findNavController().navigate(R.id.action_affiliateProfileFragment_to_supportFragment)
            }


           /* rlLanguage.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "profile")
                }
                findNavController().navigate(R.id.action_affiliateProfileFragment_to_languageFragment,bundle)
            }

            rlSetting.setOnClickListener {
                findNavController().navigate(R.id.action_affiliateProfileFragment_to_settingFragment)
            }

            rlFaq.setOnClickListener {
                findNavController().navigate(R.id.action_affiliateProfileFragment_to_faqFragment)
            }

            rlChangeCurrency.setOnClickListener {
                findNavController().navigate(R.id.action_affiliateProfileFragment_to_changeCurrencyFragment)
            }

            rlChangeCountry.setOnClickListener {
                findNavController().navigate(R.id.action_affiliateProfileFragment_to_changeCountryFragment)
            }*/
        }

    }

    override fun observe() {
    }

}