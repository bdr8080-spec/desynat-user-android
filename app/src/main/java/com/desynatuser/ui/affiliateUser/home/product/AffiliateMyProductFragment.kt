package com.desynatuser.ui.affiliateUser.home.product

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.AffiliateCategoryMyProductAdapter
import com.desynatuser.adapter.AffiliateMyProductAdapter
import com.desynatuser.adapter.AffiliateSubCategoryMyProductAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentAffiliateMyProductBinding
import com.desynatuser.ui.affiliateUser.home.product.response.GetAffiliateMyProductResponse
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class AffiliateMyProductFragment : BaseFragment() {
    private var binding: FragmentAffiliateMyProductBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var affiliateMyProductAdapter: AffiliateMyProductAdapter?=null
    private var affiliateCategoryMyProductAdapter: AffiliateCategoryMyProductAdapter?=null
    private var affiliateSubCategoryMyProductAdapter: AffiliateSubCategoryMyProductAdapter?=null
    private var productList = ArrayList<GetAffiliateMyProductResponse.Data.Data>()
    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var allSubCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var search: Job? = null
    private var lastQuery: String? = null
    private var isCategoryObserver = false
    private var isSubCategoryObserver = false
    private var selectedCategory : GetCategoryResponse.Data?=null
    private var selectedSubCategory : GetSubCategoryResponse.Data?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateMyProductBinding.inflate(layoutInflater, container, false)
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
            affiliateMyProductAdapter =
                AffiliateMyProductAdapter(requireActivity(), productList, object :
                    AffiliateMyProductAdapter.ProductClick {
                    override fun onProductClick(position: Int, type: Int) {
                        val bundle = Bundle().apply {
                            putString("productData", Gson().toJson(productList[position]))
                        }
                        findNavController().navigate(R.id.action_affiliateMyProductFragment_to_affiliateItemPerformanceFragment,bundle)
                    }

                })
            rvProduct.adapter = affiliateMyProductAdapter

            affiliateSubCategoryMyProductAdapter =
                AffiliateSubCategoryMyProductAdapter(requireActivity(), subCategoryList, object :
                    AffiliateSubCategoryMyProductAdapter.SubCategoryClick {
                    override fun onSubCategoryClick(position: Int,type: Int) {
                        when(type){
                            1->{
                                selectedSubCategory = null
                            }
                            2->{
                                selectedSubCategory = subCategoryList[position]
                            }
                        }
                        getMyProduct()
                    }

                })
            rvSubCategory.adapter = affiliateSubCategoryMyProductAdapter

            affiliateCategoryMyProductAdapter = AffiliateCategoryMyProductAdapter(requireActivity(), categoryList, object :
                    AffiliateCategoryMyProductAdapter.CategoryClick {
                    override fun onCategoryClick(position: Int) {
                        if (position==0){
                            selectedCategory = null
                            selectedSubCategory = null
                            getAllSubCategory(false)
                            getMyProduct()
                        }
                        else {
                            selectedCategory = categoryList[position]
                            selectedSubCategory = null
                            getAllSubCategory(false, category = selectedCategory?.id.toString())
                            getMyProduct()
                        }
                    }

                    })
            rvCategory.adapter = affiliateCategoryMyProductAdapter

            affiliateMyProductAdapter?.setData(subCategoryList.isNotEmpty())

            lastQuery = etSearch.text.toString().trim()
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
                            getMyProduct()
                        }
                    }

                }
            })
        }

        getAllCategory()
        getMyProduct()
    }

    override fun initControl() {
        binding?.apply {
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

        viewModel.getAffiliateMyProductResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    productList.clear()
                    response?.data?.data?.let { list ->
                        productList.addAll(list)
                    }
                    binding?.tvNoDataFound?.isVisible = productList.isEmpty()
                    affiliateMyProductAdapter?.notifyDataSetChanged()
                }
                isObserver = false
            }
        }

        viewModel.categoryResult.observe(viewLifecycleOwner) { response ->
            if (isCategoryObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                categoryList.clear()
                categoryList.add(GetCategoryResponse.Data(name = getString(R.string.all), id = 0))
                categoryList.addAll(response.data)
                var pos = 0
                if (selectedCategory!=null){
                    for (i in 0 until categoryList.size){
                        if (categoryList[i].id==selectedCategory?.id){
                            pos = i
                        }
                    }
                }
                if (pos==0){
                    getAllSubCategory(false)
                }
                affiliateCategoryMyProductAdapter?.setSelected(pos)
                isCategoryObserver = false
            }
        }

        viewModel.subCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isSubCategoryObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                subCategoryList.clear()
                subCategoryList.addAll(response.data)
                selectedSubCategory = null
                affiliateSubCategoryMyProductAdapter?.setSelectedPos(-1)
                affiliateMyProductAdapter?.setData(subCategoryList.isNotEmpty())
                isSubCategoryObserver = false
            }
        }
    }

    private val speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                val list = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val text = list?.firstOrNull()

                if (!text.isNullOrEmpty()) {
                    binding?.etSearch?.setText(text)
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
            showToast(getString(R.string.speech_recognition_not_supported))
        }
    }

    private fun getMyProduct(){
        val map = HashMap<String, Any>()
        if (lastQuery!=null&&lastQuery!="null"&&lastQuery!="") {
            map["search"] = lastQuery.toString()
        }
        if (selectedCategory!=null){
            map["category_id"] = selectedCategory?.id.toString()

        }
        if (selectedSubCategory!=null){
            map["subcategory_id"] = selectedSubCategory?.id.toString()
        }
        viewModel.getAffiliateMyProduct(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getAllCategory(){
        viewModel.getCategory()
        isCategoryObserver = true
    }

    private fun getAllSubCategory(loading:Boolean=true,search:String?=null,category: String?=null){
        viewModel.getSubCategory(category,search)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isSubCategoryObserver = true
    }

}