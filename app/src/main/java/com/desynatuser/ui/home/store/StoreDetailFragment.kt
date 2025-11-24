package com.desynatuser.ui.home.store

import android.app.Dialog
import android.os.Bundle
import android.text.Highlights
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatuser.R
import com.desynatuser.adapter.HighlightAdapter
import com.desynatuser.adapter.ProductAdapter
import com.desynatuser.adapter.ReelAdapter
import com.desynatuser.adapter.StoreAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentProductDetailBinding
import com.desynatuser.databinding.FragmentStoreBinding
import com.desynatuser.databinding.FragmentStoreDetailBinding
import com.desynatuser.databinding.SizeGuideChartDialogBinding
import com.desynatuser.databinding.SortProductBinding
import com.desynatuser.ui.home.response.GetSubCategoryResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreDetailFragment : BaseFragment() {
    private var binding : FragmentStoreDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false

    private var reelAdapter: ReelAdapter? = null
    private var highlightAdapter: HighlightAdapter? = null
    private var productAdapter: ProductAdapter? = null
    private var productList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()
    private var storeData: GetStoreResponse.Data?=null
    private var storeDetailData: GetSingleStoreResponse.Data?=null
    var selectedPos = -1
    private var sortFilter: String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreDetailBinding.inflate(layoutInflater, container,false)
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
        ProgressDialogUtils.getInstance().hideProgress()
        val itemJson = arguments?.getString("storeData")
        if (!itemJson.isNullOrEmpty()) {
            storeData = Gson().fromJson(itemJson, GetStoreResponse.Data::class.java)
            setData()
        }

        binding?.apply {

            productAdapter = ProductAdapter(requireActivity(),productList,storeData?.storeName.toString(),object: ProductAdapter.ProductClick{
                override fun onProductClick(position: Int,type:Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("productData", Gson().toJson(productList[position]))
                                putString("storeData", Gson().toJson(storeData))
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

            highlightAdapter = HighlightAdapter(requireActivity(), arrayListOf("","","","","","","",""))
            rvHighlights.adapter = highlightAdapter

            reelAdapter = ReelAdapter(requireActivity(), arrayListOf("","","","","","","",""))
            rvReel.adapter = reelAdapter

        }

        getStoreDetail()
    }

    private fun setData() {
        binding?.apply {
            tvToolbar.text = storeData?.storeName
            tvBrand.text = storeData?.storeName
            tvDescription.text = storeData?.bio
            tvName.text = storeData?.sellerInformation?.firstName+" "+storeData?.sellerInformation?.lastName

            ivProfileImage.load(storeData?.sellerInformation?.profilePic) {
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }

            ivStore.load(storeData?.storeImage) {
                crossfade(true)
                placeholder(R.color.black)
                error(R.color.black)
            }

            if (storeData?.storeAvailability==1){
                tvStatus.text = "Busy"
            }
            else{
                tvStatus.text = "Available"
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivShare.setOnClickListener {
                CommonMethod.shareProduct(requireActivity(),
                    storeData?.storeName ?: "",
                    storeData?.bio ?: "",
                    storeData?.storeImage ?: ""
                )
            }

            tvFilter.setOnClickListener {

            }

            tvSort.setOnClickListener {
                sortProductDialog()
            }

            tvProduct.setOnClickListener {
                tvProduct.setBackgroundResource(R.drawable.selected_left_tab)
                tvProduct.setTextColor(requireActivity().getColor(R.color.white))
                tvReel.setTextColor(requireActivity().getColor(R.color.black))
                tvReel.background = null
                clProduct.isVisible = true
                rvReel.isVisible = false
            }

            tvReel.setOnClickListener {
                tvProduct.background = null
                tvProduct.setTextColor(requireActivity().getColor(R.color.black))
                tvReel.setTextColor(requireActivity().getColor(R.color.white))
                tvReel.setBackgroundResource(R.drawable.selected_right_tab)
                clProduct.isVisible = false
                rvReel.isVisible = true
            }

            ivChat.setOnClickListener {

            }
        }
    }



    private fun getStoreDetail(){
        val map = HashMap<String,Any>()
        map["store_id"] = storeData?.id.toString()
        if (sortFilter!=null) {
            map["sort_filter"] = sortFilter.toString()
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

    override fun observe() {

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.storeDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                storeDetailData = response.data
                setProductData()
                isObserver = false
            }
        }

        viewModel.addToWishlistResult.observe(viewLifecycleOwner) { response ->
        if (isObserver) {
            ProgressDialogUtils.getInstance().hideProgress()
            showToast(response.message)
            if (productList[selectedPos].wishlist==1){
                productList[selectedPos].wishlist = 0
            }
            else{
                productList[selectedPos].wishlist = 1
            }

            productAdapter?.notifyItemChanged(selectedPos)
            selectedPos = -1
            isObserver = false
        }
    }
    }

    private fun setProductData() {
        binding?.apply {
            productList.clear()
            storeDetailData?.allProduct?.data?.let { productList.addAll(it) }
            productAdapter?.notifyDataSetChanged()
        }
    }

    private fun sortProductDialog() {
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

           radioGroup.setOnCheckedChangeListener { group, checkedId ->

                val selectedRB = group.findViewById<RadioButton>(checkedId)
                val selectedText = selectedRB.text.toString()
                sortFilter = group.indexOfChild(selectedRB).toString()
            }


            tvClear.setOnClickListener {
                dialog.dismiss()
            }

            tvApply.setOnClickListener {
                if (sortFilter!=null){
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

}