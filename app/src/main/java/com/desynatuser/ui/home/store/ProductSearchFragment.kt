package com.desynatuser.ui.home.store

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.LatestSearchAdapter
import com.desynatuser.adapter.RecentSearchAdapter
import com.desynatuser.adapter.RecommendedAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentProductSearchBinding
import com.desynatuser.ui.home.fragment.response.GetHomeSearchResponse
import com.desynatuser.ui.home.fragment.response.GetRecentSearchListResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductSearchFragment : BaseFragment() {
    private var binding : FragmentProductSearchBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var searchList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()

    var recentSearchAdapter: RecentSearchAdapter?=null
    var recentSearchList = ArrayList<GetRecentSearchListResponse.Data>()
    var recommendedAdapter: RecommendedAdapter?=null
    var latestSearchAdapter: LatestSearchAdapter?=null

    private var search: Job? = null
    private var lastQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductSearchBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ProgressDialogUtils.getInstance().hideProgress()
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = "Search"

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
                        if (!isObserver) {
                         //   getStoreList(search = query.ifEmpty { null })
                        }
                    }

                }
            })

            latestSearchAdapter = LatestSearchAdapter(requireActivity(), arrayListOf("","",""),object: LatestSearchAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }

            })
            rvLatestSearch.adapter = latestSearchAdapter

            recentSearchList.clear()
            recentSearchAdapter = RecentSearchAdapter(requireActivity(), recentSearchList,object: RecentSearchAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    etSearch.setText(recentSearchList[position].name)
                }

            })
            rvRecentSearch.adapter = recentSearchAdapter

            recommendedAdapter = RecommendedAdapter(requireActivity(), arrayListOf("","",""),object: RecommendedAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }

            })
            rvRecommended.adapter = recommendedAdapter

            getRecentSearch()
        }
    }

    private fun getRecentSearch() {
        viewModel.getRecentSearchList()
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    private fun getHomeSearch() {
        val map = HashMap<String,Any>()
        if (lastQuery!=null) {
            map["search"] = lastQuery.toString()
        }
        viewModel.homePageSearch(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvGo.setOnClickListener {
                getHomeSearch()
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

        viewModel.getRecentSearchListResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                   recentSearchList.clear()
                    recentSearchList.addAll(response.data)
                    recentSearchAdapter?.notifyDataSetChanged()
                }
                isObserver = false
            }
        }
        viewModel.homePageSearchResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                   searchList.clear()
                    searchList.addAll(response.data.data)
                    findNavController().navigate(R.id.action_searchProductFragment_to_searchProductListFragment)
                }
                isObserver = false
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