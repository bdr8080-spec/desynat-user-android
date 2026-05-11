package com.desynatuser.ui.affiliateUser.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliateHomeBinding
import com.desynatuser.ui.affiliateUser.home.profile.response.GetAffiliateUserProfileResponse
import com.desynatuser.ui.affiliateUser.home.response.GetHomeDashboardResponse
import com.desynatuser.ui.auth.response.GetCountryResponse
import com.desynatuser.ui.auth.response.LoginResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.openCustomPreSelectedDatePickerWithReset
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import me.leolin.shortcutbadger.ShortcutBadger
import java.util.Calendar

@AndroidEntryPoint
class AffiliateHomeFragment : BaseFragment() {
    private var binding: FragmentAffiliateHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var isCountryObserver = false
    var isDashboardObserver = false
    var isNotificationObserver = false
    var profileData : GetAffiliateUserProfileResponse.Data?=null
    private var filter = "1" //1-today,2-week,3-month
    private var dashboardData: GetHomeDashboardResponse.Data?=null

    private var fromTimestamp: Long? = null
    private var toTimestamp: Long? = null
    private var fromDateText: String? = null
    private var toDateText: String? = null
    private var countryList= ArrayList<GetCountryResponse.Data>()
    private var user: LoginResponse.Data? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        user = SharedPreference.get(requireActivity()).getUser()
        getCurrency()
        if (SharedPreference.get(requireActivity()).getAffiliateUser()==null){
            getAffiliateUserProfile()
        }
        else{
            profileData = SharedPreference.get(requireActivity()).getAffiliateUser()
            setData()
        }
        binding?.apply {
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbToday -> {
                        filter = "1"
                        getAffiliateDashboard()
                    }
                    R.id.rbWeek -> {
                        filter = "2"
                        getAffiliateDashboard()
                    }
                    R.id.rbMonth -> {
                        filter = "3"
                        getAffiliateDashboard()
                    }
                }
            }
        }
        getAffiliateDashboard()
        notificationCount()

    }

    private fun setData(){
        binding?.apply {
            tvName.text = "${getString(R.string.hi)} ${profileData?.fullName}"
            Glide.with(requireActivity()).load(profileData?.profileImage).placeholder(R.drawable.ic_user).into(ivProfile)
        }
    }

    override fun initControl() {
        binding?.apply {
            ivNotification.setOnClickListener {
                findNavController().navigate(R.id.action_affiliateHomeFragment_to_notificationFragment)
            }

            ivSupport.setOnClickListener {
                findNavController().navigate(R.id.action_affiliateHomeFragment_to_tidioChatFragment)
            }

            etFrom.setOnClickListener {
                val initialDate = fromTimestamp ?: Calendar.getInstance().timeInMillis

                openCustomPreSelectedDatePickerWithReset(
                    requireActivity(),
                    initialDate = initialDate,
                    onClick = { date, timestamp ->

                        fromTimestamp = timestamp
                        fromDateText = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                        etFrom.setText(date)

                        toTimestamp = null
                        toDateText = null
                        etTo.setText("")
                        radioGroup?.clearCheck()
                    },
                    onReset = {

                        etFrom.setText("")
                        etTo.setText("")
                        fromTimestamp = null
                        fromDateText = null
                        toTimestamp = null
                        toDateText = null
                        rbToday?.isChecked = true
                        filter = "1"
                    }
                )
            }

            etTo.setOnClickListener {
                if (fromTimestamp == null) {
                    showToast(getString(R.string.please_select_from_date_first))
                    return@setOnClickListener
                }

                val initialDate = toTimestamp ?: fromTimestamp

                openCustomPreSelectedDatePickerWithReset(
                    requireActivity(),
                    minDate = fromTimestamp,initialDate,
                    onClick = { date, timestamp ->

                        toTimestamp = timestamp
                        toDateText = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                        etTo.setText(date)
                        radioGroup?.clearCheck()
                        getAffiliateDashboard()
                    },
                    onReset = {

                        etFrom.setText("")
                        etTo.setText("")
                        fromTimestamp = null
                        fromDateText = null
                        toTimestamp = null
                        toDateText = null
                        rbToday?.isChecked = true
                        filter = "1"
                    }
                )
            }

        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isNotificationObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                errorMessage?.let { showToast(it) }
                isObserver = false
                isNotificationObserver = false
            }
        }


        viewModel.affiliateUserNotificationCountResult.observe(viewLifecycleOwner) { response ->
            if (isNotificationObserver) {

                response?.let {
                    if (response?.data!=null&&response?.data!=0){
                        setNotificationCount(response.data)
                    }
                    else{
                        SharedPreference.get(requireActivity()).badgeCount = 0
                        ShortcutBadger.removeCount(requireActivity())
                    }
                }
                isNotificationObserver = false
            }
        }

        viewModel.getSelectedCountryResult.observe(viewLifecycleOwner) { response ->
            if (isCountryObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                response?.let {
                    SharedPreference.get(requireActivity()).saveSelectedCountryList(response.data)
                    countryList.clear()
                    response.data?.let { countryList.addAll(it) }
                    for (i in 0 until countryList.size) {
                        if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){

                            if (countryList[i].id.toString() == user?.selectedCurrency) {
                                getCurrencyData(countryList[i].currencyCode)
                                break
                            }
                        }
                        else{
                            if (countryList[i].id.toString() == user?.selectedCurrency) {
                                getCurrencyData(countryList[i].currencyCode)

                                break
                            }
                        }
                    }

                }
                isCountryObserver = false
            }
        }


        viewModel.getAffiliateDashboardResult.observe(viewLifecycleOwner) { response ->
            if (isDashboardObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                response?.let {
                    dashboardData = response.data
                    setDashBoardDate()
                }
                isDashboardObserver = false
            }
        }

        viewModel.getAffiliateProfileResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                profileData = response.data
                SharedPreference.get(requireActivity()).saveAffiliateUser(response.data)
                setData()
                isObserver = false
            }
        }

    }

    private fun getAffiliateUserProfile(){
        viewModel.getAffiliateProfile()
        binding?.progressBar?.isVisible = true
        isObserver = true
    }

 private fun getAffiliateDashboard(){
     val map = HashMap<String, Any>()
     if (fromDateText!=null&&toDateText!=null){
         map["from_date"] = fromDateText.toString()
         map["to_date"] = toDateText.toString()
         viewModel.getAffiliateDashboard(map)
         binding?.progressBar?.isVisible = true
         isDashboardObserver = true
     }
     else if (fromDateText==null&&toDateText==null){
         map["filter"] =filter//1-today,2-week,3-month
         viewModel.getAffiliateDashboard(map)
         binding?.progressBar?.isVisible = true
         isDashboardObserver = true
     }
     else{

     }


    }

    private fun notificationCount(){
        viewModel.affiliateUserNotificationCount()
        isNotificationObserver = true
    }

    private fun setNotificationCount(data: Int) {
        binding?.relCount?.isVisible = true
        binding?.tvCount?.text = data.toString()
        ShortcutBadger.applyCount(requireActivity(), data)
    }

    private fun setDashBoardDate(){
        binding?.apply {
          //  ivAmount.text = "${ CommonMethod.formatPrice(dashboardData?.totalRevenue?.toDoubleOrNull()?:0.0)} KD"
            ivAmount.text = "${ CommonMethod.formatCurrencyAmount(requireActivity(),dashboardData?.totalRevenue?.toDoubleOrNull()?:0.0)}"
            tvActiveProduct.text = dashboardData?.totalProducts.toString()
            tvInActiveProduct.text = dashboardData?.totalProducts.toString()
            tvNewOrder.text = dashboardData?.totalOrders.toString()
            tvOnGoingOrder.text = dashboardData?.totalOngoing.toString()
            tvCompleteOrder.text = dashboardData?.totalCompleted.toString()
            tvCancelledOrder.text = dashboardData?.totalCancelled.toString()
        }
    }

    private fun getCurrency() {
        countryList.clear()
        if (SharedPreference.get(requireActivity()).getSelectedCountryList()?.isNotEmpty() == true) {
            val data = SharedPreference.get(requireActivity()).getSelectedCountryList()
            countryList.clear()
            data?.let { countryList.addAll(it) }
            for (i in 0 until countryList.size) {
                if (SharedPreference.get(requireActivity()).affiliateAccessToken != "") {

                    if (countryList[i].id.toString() == user?.selectedCurrency) {
                        getCurrencyData(countryList[i].currencyCode)
                        break
                    }
                } else {
                    if (countryList[i].id.toString() == user?.selectedCurrency) {
                        getCurrencyData(countryList[i].currencyCode)
                        break
                    }
                }

            }
        }
        else{
            getSelectedCountry()
        }
    }

    private fun getSelectedCountry() {
        viewModel.getSelectedCountry()
        isCountryObserver = true
    }


    fun getCurrencyData(code: String) {
        Constants.CURRENCY = if (code == "KWD") "KD" else code
    }
}