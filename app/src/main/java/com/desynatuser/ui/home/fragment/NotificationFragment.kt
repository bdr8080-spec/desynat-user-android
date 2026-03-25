package com.desynatuser.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.NotificationAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentNotificationBinding
import com.desynatuser.ui.home.fragment.response.GetNotificationResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import kotlin.getValue
@AndroidEntryPoint
class NotificationFragment : BaseFragment() {
    private var binding: FragmentNotificationBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isReadObserver = true
    private var isFromSwipe=false
    private var selectedPos = -1

    private var notificationList = ArrayList<GetNotificationResponse.Data.Data>()
    private var notificationAdapter: NotificationAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {

            notificationAdapter = NotificationAdapter(requireActivity(),notificationList,object :
                NotificationAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    selectedPos = position
                    readUserNotification()
                }
            })
            rvNotification.adapter = notificationAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getNotification(false)
            }
        }

        getNotification()
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvReadAll.setOnClickListener {
             //   readAllNotification()
            }
        }
    }

    private fun getNotification(isLoading: Boolean=true){
        var map = HashMap<String, Any>()
        if (SharedPreference.get(requireActivity()).affiliateAccessToken.isNotEmpty()) {
            viewModel.getAffiliateUserNotification(map)
        }
        else{
            viewModel.getUserNotification(map)
        }

        isObserver = true
        if (isLoading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
    }

    private fun readUserNotification(){
        val map = HashMap<String, Any>()
        map["notification_id"] = notificationList[selectedPos].id
        if (SharedPreference.get(requireActivity()).affiliateAccessToken.isNotEmpty()) {
            viewModel.readAffiliateUserNotification(map)
        }
        else{
            viewModel.readUserNotification(map)
        }

        isReadObserver = true
    }

    private fun readAllNotification(){
        val map = HashMap<String, Any>()
        if (SharedPreference.get(requireActivity()).affiliateAccessToken.isNotEmpty()) {
            viewModel.readAffiliateUserNotification(map)
        }
        else{
            viewModel.readUserNotification(map)
        }

        isReadObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getUserNotificationResult.observe(viewLifecycleOwner){response ->
            if (isObserver){
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                notificationList.clear()
                notificationList.addAll(response.data.data)
                notificationAdapter?.notifyDataSetChanged()
                checkData()
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.readUserNotificationResult.observe(viewLifecycleOwner){response ->
            if (isReadObserver){
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                notificationList[selectedPos].isRead = 1
                notificationAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
                isReadObserver = false
            }
        }

        viewModel.getAffiliateUserNotificationResult.observe(viewLifecycleOwner){response ->
            if (isObserver){
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                notificationList.clear()
                notificationList.addAll(response.data.data)
                notificationAdapter?.notifyDataSetChanged()
                checkData()
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.readAffiliateUserNotificationResult.observe(viewLifecycleOwner){response ->
            if (isReadObserver){
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                notificationList[selectedPos].isRead = 1
                notificationAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
                isReadObserver = false
            }
        }
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = notificationList.isEmpty()
    }
}