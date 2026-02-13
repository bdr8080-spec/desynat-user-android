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
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.utils.showToast
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
    private var productList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()
    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var search: Job? = null
    private var lastQuery: String? = null

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
                        findNavController().navigate(R.id.action_affiliateMyProductFragment_to_affiliateItemPerformanceFragment)
                    }

                })
            rvProduct.adapter = affiliateMyProductAdapter

            affiliateSubCategoryMyProductAdapter =
                AffiliateSubCategoryMyProductAdapter(requireActivity(), subCategoryList, object :
                    AffiliateSubCategoryMyProductAdapter.SubCategoryClick {
                    override fun onSubCategoryClick(position: Int) {

                    }

                })
            rvSubCategory.adapter = affiliateSubCategoryMyProductAdapter

            affiliateCategoryMyProductAdapter =
                AffiliateCategoryMyProductAdapter(requireActivity(), categoryList, object :
                    AffiliateCategoryMyProductAdapter.CategoryClick {
                    override fun onCategoryClick(position: Int) {
                    }

                    })
            rvCategory.adapter = affiliateCategoryMyProductAdapter

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

                        }
                    }

                }
            })
        }
    }

    override fun initControl() {
        binding?.apply {
            ivMic.setOnClickListener {
                openSpeechDialog()
            }
        }

    }

    override fun observe() {
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

}