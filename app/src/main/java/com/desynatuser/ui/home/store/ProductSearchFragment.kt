package com.desynatuser.ui.home.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.LatestSearchAdapter
import com.desynatuser.adapter.RecentSearchAdapter
import com.desynatuser.adapter.RecommendedAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentProductDetailBinding
import com.desynatuser.databinding.FragmentProductSearchBinding
import com.desynatuser.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductSearchFragment : BaseFragment() {
    private var binding : FragmentProductSearchBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var recentSearchAdapter: RecentSearchAdapter?=null
    var recommendedAdapter: RecommendedAdapter?=null
    var latestSearchAdapter: LatestSearchAdapter?=null
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

            latestSearchAdapter = LatestSearchAdapter(requireActivity(), arrayListOf("","",""),object: LatestSearchAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }

            })
            rvLatestSearch.adapter = latestSearchAdapter

            recentSearchAdapter = RecentSearchAdapter(requireActivity(), arrayListOf("","",""),object: RecentSearchAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }

            })
            rvRecentSearch.adapter = recentSearchAdapter

            recommendedAdapter = RecommendedAdapter(requireActivity(), arrayListOf("","",""),object: RecommendedAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }

            })
            rvRecommended.adapter = recommendedAdapter
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvGo.setOnClickListener {
                findNavController().navigate(R.id.action_searchProductFragment_to_searchProductListFragment)
            }
        }
    }

    override fun observe() {
    }

}