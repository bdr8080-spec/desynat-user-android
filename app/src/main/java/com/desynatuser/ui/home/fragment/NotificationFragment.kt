package com.desynatuser.ui.home.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.NotificationAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentNotificationBinding
import com.desynatuser.ui.home.fragment.response.GetNotificationResponse
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var pagination=false

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
                pagination = false
                currentPage = 1
                lastPage = 1

                getNotification(false)
            }
             nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
            val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

            if (lastChild != null) {
                val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                if (diff == 0 && scrollY > oldScrollY) {
                    if(totalItem==0)
                        return@setOnScrollChangeListener
                    if (!isObserver && !pagination && currentPage < lastPage) {
                        pagination = true
                        isFromSwipe = false
                        currentPage += 1
                        getNotification(false)
                        binding?.progressBar?.isVisible = true
                    }
                }
            }
        }

        }

        getNotification()
        onMessageEvent(null)
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
        map["page"] = currentPage
        map["limit"] = pageLimit
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

                currentPage = response.data.currentPage
                totalItem = response.data.total

                lastPage = response.data.lastPage

                val oldSize = notificationList.size
                if (!pagination){
                    notificationList.clear()
                }
                val newItems = response.data.data ?: emptyList()

                notificationList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        notificationAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    notificationAdapter?.notifyDataSetChanged()
                }

                pagination = false
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

    override fun onStart() {
        super.onStart()
        Constants.currentScreen = "notification"
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        Constants.currentScreen = ""
        EventBus.getDefault().unregister(this)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String?=null) {
        if (event!=null) {
            currentPage = 0
            getNotification()
        }
    }
}