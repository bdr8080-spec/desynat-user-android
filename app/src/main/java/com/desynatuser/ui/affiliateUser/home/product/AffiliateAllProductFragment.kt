package com.desynatuser.ui.affiliateUser.home.product

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
import com.desynatuser.adapter.AffiliateProductAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliateAllProductBinding
import com.desynatuser.databinding.GetProductLinkDialogBinding
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateAllProductResponse
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class AffiliateAllProductFragment : BaseFragment() {
    private var binding: FragmentAffiliateAllProductBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var affiliateProductAdapter: AffiliateProductAdapter?=null
    private var productList = ArrayList<GetAffiliateAllProductResponse.Data.Data>()
    private var search: Job? = null
    private var lastQuery: String? = null
    private var categoryData : GetCategoryResponse.Data?=null
    private var subCategoryData : GetSubCategoryResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateAllProductBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "All Products"

            val itemSubCategoryDataJson = arguments?.getString("subCategoryData")
            if (!itemSubCategoryDataJson.isNullOrEmpty()) {
                subCategoryData = Gson().fromJson(itemSubCategoryDataJson, GetSubCategoryResponse.Data::class.java)
            }

            val itemCategoryDataJson = arguments?.getString("categoryData")
            if (!itemCategoryDataJson.isNullOrEmpty()) {
                categoryData = Gson().fromJson(itemCategoryDataJson, GetCategoryResponse.Data::class.java)
            }

            affiliateProductAdapter = AffiliateProductAdapter(requireActivity(),productList,object :
                AffiliateProductAdapter.ProductClick{
                override fun onProductClick(position: Int, type: Int) {
                    when(type){
                        1->{
                        }
                        2->{
                            getLinkDialog()
                        }
                    }
                }
            })
            rvProduct.adapter = affiliateProductAdapter

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
                        if (!isObserver){
                            getAffiliateAllProduct()
                        }
                    }
                }
            })
        }

        getAffiliateAllProduct()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivMic.setOnClickListener {
                openSpeechDialog()
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

        viewModel.getAffiliateAllProductResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    productList.clear()
                    response?.data?.data?.let { list ->
                        productList.addAll(list)
                    }
                    binding?.tvNoDataFound?.isVisible = productList.isEmpty()
                    affiliateProductAdapter?.notifyDataSetChanged()
                }
                isObserver = false
            }
        }
    }

    private fun getLinkDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = GetProductLinkDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(true)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {
            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }

            tvCopyLink.setOnClickListener {
                CommonMethod.copyText(requireActivity(),tvLink.text.toString())
            }

            btnAdd.setOnClickListener {

            }
        }
        dialog?.show()
    }

    private val speechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                val list = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val text = list?.firstOrNull()

                if (!text.isNullOrEmpty()) {
                    val bundle = Bundle().apply {
                        putString("search", text)
                    }
                    findNavController().navigate(
                        R.id.action_searchProductFragment_to_searchProductListFragment,
                        bundle
                    )
                }
            }
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

    private fun getAffiliateAllProduct(){
        val map = HashMap<String, Any>()
        map["category_id"] = categoryData?.id.toString()
        map["subcategory_id"] = subCategoryData?.id.toString()
        viewModel.getAffiliateAllProduct(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

}