package com.desynatuser.ui.home.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.PremiumStoreViewPagerAdapter
import com.desynatuser.adapter.ProductImageAdapter
import com.desynatuser.adapter.StoreAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentStoreBinding
import com.desynatuser.databinding.StoreFilterBinding
import com.desynatuser.ui.home.response.GetCategoryResponse
import com.desynatuser.ui.home.response.GetSubCategoryResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreFragment : BaseFragment() {
    private var binding: FragmentStoreBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var subCategoryData: GetSubCategoryResponse.Data?=null

    private var storeAdapter: StoreAdapter? = null
    private var storeList = ArrayList<GetStoreResponse.Data>()
    var premiumStoreViewPagerAdapter : PremiumStoreViewPagerAdapter?=null
    var premiumStoreList = ArrayList<Int>()
    private var search: Job? = null
    private var lastQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreBinding.inflate(layoutInflater,container,false)
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
        val itemJson = arguments?.getString("subCategoryData")
        if (!itemJson.isNullOrEmpty()) {
            subCategoryData = Gson().fromJson(itemJson, GetSubCategoryResponse.Data::class.java)
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Stores"

            storeAdapter = StoreAdapter(requireActivity(),storeList,object: StoreAdapter.StoreClick{
                override fun onStoreClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("storeData", Gson().toJson(storeList[position]))
                    }
                    findNavController().navigate(R.id.action_storeFragment_to_storeDetailFragment,bundle)
                }

            })
            rvStore.adapter = storeAdapter


            premiumStoreList.clear()
            premiumStoreList.add(R.drawable.premium)
            premiumStoreList.add(R.drawable.newest)
            premiumStoreList.add(R.drawable.banner)
            premiumStoreViewPagerAdapter = PremiumStoreViewPagerAdapter(requireActivity(),premiumStoreList)
            binding?.viewPager?.adapter = premiumStoreViewPagerAdapter

            TabLayoutMediator(binding!!.dotsIndicatorStore, binding!!.viewPager) { _, _ -> }.attach()


            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        if (!isObserver)
                            getStoreList(search =  query.ifEmpty { null })
                    }

                }
            })
        }

        getStoreList()
    }

    override fun initControl() {

        binding?.apply {
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

    private fun getStoreList(search:String?=null){
        val map = HashMap<String,Any>()
        map["category_id"] = subCategoryData?.categoryId.toString()
        if (search!=null){
            map["search"] = search
        }
        viewModel.getStoreList(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
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

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            tvClear.setOnClickListener {

                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun openSpeechDialog() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }

        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            showToast("Speech recognition not supported")
        }
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

}