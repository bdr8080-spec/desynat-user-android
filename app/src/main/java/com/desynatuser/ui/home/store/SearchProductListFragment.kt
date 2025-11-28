package com.desynatuser.ui.home.store

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.FilterBrandAdapter
import com.desynatuser.adapter.FilterColorAdapter
import com.desynatuser.adapter.ProductAdapter
import com.desynatuser.adapter.SortListAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentProductDetailBinding
import com.desynatuser.databinding.FragmentSearchProductListBinding
import com.desynatuser.databinding.ProductFilterBinding
import com.desynatuser.databinding.SortProductBinding
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreSortListResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchProductListFragment : BaseFragment() {
    private var binding : FragmentSearchProductListBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()

    var isObserver = false
    var isSortObserver = false
    private var productAdapter: ProductAdapter? = null
    private var productList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()

    var selectedPos = -1
    private var sortFilter: String?=null
    private var sortList = ArrayList<GetStoreSortListResponse.Data>()
    private var search: Job? = null
    private var lastQuery: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchProductListBinding.inflate(layoutInflater, container,false)
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
            getStoreSortList()
            productAdapter = ProductAdapter(requireActivity(),productList,"",object: ProductAdapter.ProductClick{
                override fun onProductClick(position: Int,type:Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("productData", Gson().toJson(productList[position]))
                              //  putString("storeData", Gson().toJson(storeData))
                            }
                            findNavController().navigate(R.id.action_storeDetailFragment_to_productDetailFragment,bundle)
                        }

                        2->{
                            selectedPos = position
                            addToWishList()
                        }
                    }

                }

            })
            rvProduct.adapter = productAdapter

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
                            getStoreDetail()
                    }

                }
            })
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvFilter.setOnClickListener {
                productFilterDialog()
            }

            tvSort.setOnClickListener {
                sortProductDialog()
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

        viewModel.storeSortResult.observe(viewLifecycleOwner) { response ->
            if (isSortObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                sortList.clear()
                sortList.addAll(response.data)
                isSortObserver = false
            }
        }

        viewModel.storeDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.addToWishlistResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                if (productList[selectedPos].wishlist==1){
                    showToast("Product removed from your wishlist successfully")
                    productList[selectedPos].wishlist = 0
                }
                else{
                    showToast("Product added to your wishlist successfully")
                    productList[selectedPos].wishlist = 1
                }

                productAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
                isObserver = false
            }
        }
    }


    private fun getStoreSortList(){
        viewModel.getStoreSorList()
        isSortObserver = true
    }

    private fun getStoreDetail(){
        val map = HashMap<String,Any>()
      //  map["store_id"] = storeData?.id.toString()
        if (sortFilter!=null) {
            map["sort_filter"] = sortFilter.toString()
        }
        if (lastQuery!=null) {
            map["search"] = lastQuery.toString()
        }
        viewModel.getSingleStoreInfo(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun addToWishList(){
        val map = HashMap<String,Any>()
        map["product_id"] = productList[selectedPos].id
        viewModel.addToWishlist(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun sortProductDialog() {
        var sort = ""
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = SortProductBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            if (sortFilter!=null) {
                for (i in 0 until sortList.size) {
                    if (sortList[i].id.toString() == sortFilter) {
                        sortList[i].isSelected = true
                    }
                }
            }
            val adapter = SortListAdapter(requireActivity(),sortList,object: SortListAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    sort = sortList[position].id.toString()
                }

            })

            rvSort.adapter = adapter

            tvClear.setOnClickListener {
                sortFilter = null
                dialog.dismiss()
            }

            tvApply.setOnClickListener {
                if (sort!=""){
                    sortFilter = sort
                    getStoreDetail()
                    dialog.dismiss()
                }
                else{
                    showToast("Please select")
                }


            }

        }

        dialog.show()
    }

    private fun productFilterDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ProductFilterBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.white)
        dialog.setCancelable(false)

        dialogBinding.apply {

         /*   val colorAdapter = FilterColorAdapter(requireActivity(), arrayListOf("",""),object :
                FilterColorAdapter.OnClick{
                override fun onClickItem(position: Int) {

                }

            })
            rvColor.adapter = colorAdapter


            val brandAdapter = FilterBrandAdapter(requireActivity(), arrayListOf("",""),object :
                FilterBrandAdapter.OnClick{
                override fun onClickItem(position: Int) {

                }

            })
            rvBrand.adapter = brandAdapter
*/
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

}