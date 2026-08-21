package com.desynatuser.ui.home.fragment

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.desynatuser.R
import com.desynatuser.adapter.PremiumStoreViewPagerAdapter
import com.desynatuser.adapter.StoreAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentStoreBinding
import com.desynatuser.databinding.StoreFilterBinding
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.utils.VoiceSearchBottomSheet
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse
import com.desynatuser.ui.response.GetHomeScreenAllBannerResponse
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreFragment : BaseFragment() {
    private var binding: FragmentStoreBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var isAllBannerObserver = false
    var isAdminObserver = false
    private var subCategoryData: GetSubCategoryResponse.Data? = null
    private var storeAdapter: StoreAdapter? = null
    private var storeList = ArrayList<GetStoreResponse.Data>()
    var premiumStoreViewPagerAdapter: PremiumStoreViewPagerAdapter? = null
    var premiumStoreList = ArrayList<GetHomeScreenAllBannerResponse.Data>()
    private var search: Job? = null
    private var lastQuery: String? = null
    var filter: String? = null

    private val audioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                showVoiceSearchBottomSheet()
            } else {
                showToast("Microphone permission is required for voice search.")
            }
        }

    private val myStories = ArrayList<GetSellerStoryResponse.Data>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreBinding.inflate(layoutInflater, container, false)
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
        getAdminStory()
        val itemJson = arguments?.getString("subCategoryData")
        if (!itemJson.isNullOrEmpty()) {
            subCategoryData = Gson().fromJson(itemJson, GetSubCategoryResponse.Data::class.java)
        }
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.stores)

            storeAdapter =
                StoreAdapter(requireActivity(), storeList, object : StoreAdapter.StoreClick {
                    override fun onStoreClick(position: Int, type: Int) {
                        when (type) {
                            1 -> {
                                val bundle = Bundle().apply {
                                    putString("storeId", storeList[position].id.toString())
                                    putString("catId", subCategoryData?.categoryId.toString())
                                    putString("subId", subCategoryData?.id.toString())
                                }
                                findNavController().navigate(
                                    R.id.action_storeFragment_to_storeDetailFragment,
                                    bundle
                                )
                            }

                            2 -> {
                                if (storeList[position].sellerStories.isNotEmpty()) {
                                    val bundle = Bundle().apply {
                                        putString("from", "story")
                                        putString("storeName", storeList[position].storeName)
                                        putString("storeImage", storeList[position].storeImage)
                                        putString(
                                            "storyData",
                                            Gson().toJson(storeList[position].sellerStories)
                                        )
                                    }
                                    findNavController().navigate(
                                        R.id.action_storeFragment_to_viewStoryFragment,
                                        bundle
                                    )
                                }

                            }
                        }

                    }

                })
            rvStore.adapter = storeAdapter


            premiumStoreViewPagerAdapter = PremiumStoreViewPagerAdapter(
                requireActivity(),
                premiumStoreList,
                object : PremiumStoreViewPagerAdapter.OnClick {
                    override fun onItemClick(position: Int) {
                        val bundle = Bundle().apply {
                            putString(
                                "storeId",
                                premiumStoreList[position]?.sellerDetails?.storeInfo?.id.toString()
                            )
                            putString("subCategoryData", Gson().toJson(subCategoryData))
                        }
                        findNavController().navigate(
                            R.id.action_storeFragment_to_storeDetailFragment,
                            bundle
                        )
                    }

                })
            binding?.viewPager?.adapter = premiumStoreViewPagerAdapter

            TabLayoutMediator(
                binding!!.dotsIndicatorStore,
                binding!!.viewPager
            ) { _, _ -> }.attach()

            lastQuery = etSearch.text.toString().trim()
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        if (!isObserver)
                            getStoreList(search = query.ifEmpty { null })
                    }

                }
            })
        }

        getStoreList()

        getHomeScreenAllBanner()
    }

    override fun initControl() {

        binding?.apply {

            ivAdminStory.setOnClickListener {
                if (myStories.isNotEmpty()) {
                    val bundle = Bundle().apply {
                        putString("from", "admin")
                        putString("storyData", Gson().toJson(myStories))
                    }
                    findNavController().navigate(
                        R.id.actions_storeFragment_to_viewStoryFragment,
                        bundle
                    )
                }
            }

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivMic.setOnClickListener {
                openSpeechDialog()
            }

            ivFilter.setOnClickListener {
                storeFilterDialog()
            }
        }
    }

    private fun getHomeScreenAllBanner() {
        val map = HashMap<String, Any>()
        map["type"] = "3"
        map["category"] = subCategoryData?.categoryId.toString()
        map["subcategory"] = subCategoryData?.id.toString()
        map["subcategory_id"] = subCategoryData?.id.toString()
        viewModel.getHomeScreenAllBanner(map)
        isAllBannerObserver = true
    }


    private fun getStoreList(search: String? = null) {
        val map = HashMap<String, Any>()
        map["category_id"] = subCategoryData?.categoryId.toString()
        map["subcategory"] = subCategoryData?.id.toString()
        map["subcategory_id"] = subCategoryData?.id.toString()
        if (search != null) {
            map["search"] = search
        }
        if (filter != null) {
            map["availbility"] = filter.toString()
        }
        if (!Constants.isGuest) {
            map["user_id"] = SharedPreference.get(requireActivity()).getUser()?.id.toString()
        }

        viewModel.getStoreList(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }


    private fun getAdminStory() {
        val map = HashMap<String, Any>()
        if (!Constants.isGuest) {
            map["user_id"] = SharedPreference.get(requireActivity()).getUser()?.id.toString()
        }
        viewModel.getAdminStory(map)
        isAdminObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.getAdminStoryResult.observe(viewLifecycleOwner) { response ->
            if (isAdminObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    myStories.clear()
                    myStories.addAll(response.data)
                    setAdminStory()

                }
                isAdminObserver = false
            }
        }


        viewModel.getHomeScreenAllBannerResult.observe(viewLifecycleOwner) { response ->
            if (isAllBannerObserver) {
                response?.let {

                    premiumStoreList.clear()
                    premiumStoreList.addAll(response.data)
                    premiumStoreViewPagerAdapter?.notifyDataSetChanged()
                    checkBannerData()

                }
                isAllBannerObserver = false
            }
        }


        viewModel.storeListResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                storeList.clear()
                storeList.addAll(response.data)
                checkData()
                storeAdapter?.notifyDataSetChanged()
                isObserver = false
            }
        }
    }

    private fun checkBannerData() {
        binding?.apply {
            if (premiumStoreList.size != 0) {
                tvPremium.isVisible = true
                dummy.isVisible = false
                viewPager.isVisible = true
                dotsIndicatorStore.isVisible = true
                autoScrollViewPager(viewPager, bannerHandler)
            } else {
                tvPremium.isVisible = true
                dummy.isVisible = true
                viewPager.isVisible = false
                dotsIndicatorStore.isVisible = false
            }
        }
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = storeList.isEmpty()
    }

    private fun storeFilterDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = StoreFilterBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.white)
        dialog.setCancelable(false)

        dialogBinding.apply {

            when (filter) {
                "0" -> radioGroup.check(R.id.rbOpen)
                "1" -> radioGroup.check(R.id.rbBusy)
                "2" -> radioGroup.check(R.id.rbShortestDeliveryTime)
                "3" -> radioGroup.check(R.id.rbHighestSale)

            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                filter = when (checkedId) {
                    R.id.rbOpen -> "0"
                    R.id.rbBusy -> "1"
                    R.id.rbShortestDeliveryTime -> "2"
                    R.id.rbHighestSale -> "3"
                    else -> null
                }
            }


            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            tvClear.setOnClickListener {
                filter = null
                getStoreList()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {
                getStoreList()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun openSpeechDialog() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            showVoiceSearchBottomSheet()
        }
    }

    private fun showVoiceSearchBottomSheet() {
        val bottomSheet = VoiceSearchBottomSheet.newInstance { text ->
            if (text.isNotEmpty()) {
                binding?.etSearch?.setText(text)
                binding?.etSearch?.setSelection(text.length)
            }
        }
        bottomSheet.show(childFragmentManager, "VoiceSearchBottomSheet")
    }

    private val speechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                val list = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val text = list?.firstOrNull()

                if (!text.isNullOrEmpty()) {
                    binding?.etSearch?.setText(text)
                    binding?.etSearch?.setSelection(text.length)
                }
            }
        }

    private val bannerHandler = Handler(Looper.getMainLooper())

    private fun autoScrollViewPager(
        viewPager: ViewPager2,
        handler: Handler,
        delay: Long = 4000L
    ) {
        val runnable = object : Runnable {
            override fun run() {
                val adapter = viewPager.adapter ?: return
                if (adapter.itemCount <= 1) return

                val nextItem =
                    if (viewPager.currentItem == adapter.itemCount - 1) 0
                    else viewPager.currentItem + 1

                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, delay)
            }
        }

        handler.postDelayed(runnable, delay)

        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, delay)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        bannerHandler.removeCallbacksAndMessages(null)
    }

    private fun setAdminStory() {
        binding?.apply {
            if (myStories.size != 0) {
                binding?.ivHighlightStory?.isVisible = true
            } else {
                binding?.ivHighlightStory?.isInvisible = true
            }
        }

    }
}