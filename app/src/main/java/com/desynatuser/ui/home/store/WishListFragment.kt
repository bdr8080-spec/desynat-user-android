package com.desynatuser.ui.home.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.HighlightAdapter
import com.desynatuser.adapter.ProductAdapter
import com.desynatuser.adapter.ReelAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentStoreDetailBinding
import com.desynatuser.databinding.FragmentWishListBinding
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse
import com.desynatuser.ui.home.storyHighlights.response.GetSellerStoryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson

class WishListFragment : BaseFragment() {
    private var binding : FragmentWishListBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    private var reelList = ArrayList<GetSellerStoryResponse.Data>()

    private var reelAdapter: ReelAdapter? = null
    private var productAdapter: ProductAdapter? = null
    private var productList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()
    var selectedPos = -1
    var type = 1
    var selectedReelPos = -1
    var storeName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWishListBinding.inflate(layoutInflater, container,false)
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
        binding?.apply {
            toolbar.tvToolbar.text = "Favourite"

            productAdapter = ProductAdapter(requireActivity(),productList,storeName,1,object: ProductAdapter.ProductClick{
                override fun onProductClick(position: Int,type:Int) {
                    when(type){
                        1->{
                          /*  val bundle = Bundle().apply {
                                putString("productData", Gson().toJson(productList[position]))
                                putString("storeData", Gson().toJson(storeData))
                            }
                            findNavController().navigate(R.id.action_storeDetailFragment_to_productDetailFragment,bundle)*/
                        }

                        2->{
                            selectedPos = position
                            addToWishList()
                        }
                    }

                }

            })
            rvProduct.adapter = productAdapter


            reelAdapter = ReelAdapter(requireActivity(), reelList,1,object : ReelAdapter.OnClick{
                override fun onItemClick(position: Int,type:Int) {
                    when(type){
                        1->{
                            if (reelList.size!=0) {
                                val bundle = Bundle().apply {
                                    putString("from", "reel")
                                    putString("storyData", Gson().toJson(reelList[position]))
                                }
                                findNavController().navigate(
                                    R.id.action_wishlistFragment_to_viewReelFragment,
                                    bundle
                                )
                            }
                        }
                        2->{
                            selectedReelPos = position
                            removeReelToWishlist()
                        }
                        3->{
                            selectedReelPos = position
                            shareReel()
                        }
                    }
                }

            })
            rvReel.adapter = reelAdapter

            when(type){
                1->{
                    product()
                }
                2->{
                    reel()
                }
            }
        }

        }

    private fun addToWishList(){
        val map = HashMap<String,Any>()
        map["product_id"] = productList[selectedPos].id
        viewModel.addToWishlist(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun removeReelToWishlist(){
        val map = HashMap<String,Any>()
        map["reel_id"] = reelList[selectedReelPos].id
        viewModel.addRemoveReelToWishlist(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun shareReel(){
        val map = HashMap<String,Any>()
        map["reel_id"] = reelList[selectedReelPos].id
        viewModel.shareReel(map)
        isObserver = true
    }

    private fun getWishList(){
        viewModel.getWishList()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }
    private fun getWishlistedReel(){
        val map = HashMap<String, Any>()
        viewModel.getWishlistedReel(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvProduct.setOnClickListener {
                product()

            }

            tvReel.setOnClickListener {
               reel()
            }


        }
    }

    private fun product(){
        binding?.apply {
            type = 1
            getWishList()
            tvProduct.setBackgroundResource(R.drawable.selected_left_tab)
            tvProduct.setTextColor(requireActivity().getColor(R.color.white))
            tvReel.setTextColor(requireActivity().getColor(R.color.black))
            tvReel.background = null
            rvProduct.isVisible = true
            rvReel.isVisible = false
            checkData()
        }
    }

    private fun reel(){
        binding?.apply {
            type = 2
            tvProduct.background = null
            getWishlistedReel()
            tvProduct.setTextColor(requireActivity().getColor(R.color.black))
            tvReel.setTextColor(requireActivity().getColor(R.color.white))
            tvReel.setBackgroundResource(R.drawable.selected_right_tab)
            rvProduct.isVisible = false
            rvReel.isVisible = true
            checkReelData()
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

        viewModel.addToWishlistResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                    showToast("Product removed from your wishlist successfully")
                    productList.removeAt(selectedPos)


                productAdapter?.notifyItemRemoved(selectedPos)
                productAdapter?.notifyItemRangeChanged(
                    selectedPos,
                    productList.size - selectedPos
                )
                checkData()
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.addRemoveReelToWishlistResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                    showToast("Reel removed from your wishlist successfully")
                reelList.removeAt(selectedReelPos)


                reelAdapter?.notifyItemRemoved(selectedReelPos)
                reelAdapter?.notifyItemRangeChanged(
                    selectedReelPos,
                    reelList.size - selectedReelPos
                )
                checkReelData()
                selectedReelPos = -1
                isObserver = false
            }
        }

        viewModel.wishlistResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                productList.clear()
                productList.addAll(response.data)
                productAdapter?.notifyDataSetChanged()
                checkData()
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.getWishlistedReelResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                reelList.clear()
                for(i in 0 until response.data.size){
                    if (response.data[i].reelDetails!=null){
                        reelList.add(response.data[i].reelDetails)
                    }
                }
                reelAdapter?.notifyDataSetChanged()
                checkReelData()
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.shareReelResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                val fileUrl = reelList[selectedReelPos]?.file ?: ""

                val isVideo = fileUrl.contains(".mp4", true) ||
                        fileUrl.contains(".mov", true) ||
                        fileUrl.contains(".mkv", true)
                CommonMethod.shareReel(requireActivity(),
                    caption = reelList[selectedReelPos]?.caption ?: "",
                    mediaUrl = fileUrl,
                    link = response.data.file ?: "",
                    isVideo = isVideo

                )
                selectedPos = -1
                isObserver = false
            }
        }
    }

    fun checkData(){
        binding?.tvNoDataFound?.isVisible = productList.isEmpty()
        binding?.tvNoDataFound?.text = "No product found in wishlist"
    }

    fun checkReelData(){
        binding?.tvNoDataFound?.isVisible = reelList.isEmpty()
        binding?.tvNoDataFound?.text = "No reel found in wishlist"
    }


}