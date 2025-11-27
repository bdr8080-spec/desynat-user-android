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
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson

class WishListFragment : BaseFragment() {
    private var binding : FragmentWishListBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false

    private var reelAdapter: ReelAdapter? = null
    private var productAdapter: ProductAdapter? = null
    private var productList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()
    var selectedPos = -1
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

            productAdapter = ProductAdapter(requireActivity(),productList,storeName,object: ProductAdapter.ProductClick{
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


            reelAdapter = ReelAdapter(requireActivity(), arrayListOf("","","","","","","",""))
            rvReel.adapter = reelAdapter
        }

        getWishList()

        }

    private fun addToWishList(){
        val map = HashMap<String,Any>()
        map["product_id"] = productList[selectedPos].id
        viewModel.addToWishlist(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getWishList(){
        viewModel.getWishList()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvProduct.setOnClickListener {
                tvProduct.setBackgroundResource(R.drawable.selected_left_tab)
                tvProduct.setTextColor(requireActivity().getColor(R.color.white))
                tvReel.setTextColor(requireActivity().getColor(R.color.black))
                tvReel.background = null
                rvProduct.isVisible = true
                rvReel.isVisible = false
                checkData()
            }

            tvReel.setOnClickListener {
                tvProduct.background = null
                tvProduct.setTextColor(requireActivity().getColor(R.color.black))
                tvReel.setTextColor(requireActivity().getColor(R.color.white))
                tvReel.setBackgroundResource(R.drawable.selected_right_tab)
                rvReel.isVisible = false
                rvReel.isVisible = true
                tvNoDataFound.isVisible = false
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
    }

    fun checkData(){
        binding?.tvNoDataFound?.isVisible = productList.isEmpty()
    }


}