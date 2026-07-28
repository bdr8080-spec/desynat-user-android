package com.desynatuser.ui.home.view_all_product

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
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.FilterBrandAdapter
import com.desynatuser.adapter.FilterCategoryAdapter
import com.desynatuser.adapter.FilterColorAdapter
import com.desynatuser.adapter.FilterSizeAdapter
import com.desynatuser.adapter.ProductAdapter
import com.desynatuser.adapter.SortListAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentViewAllProductBinding
import com.desynatuser.databinding.ProductFilterBinding
import com.desynatuser.databinding.SortProductBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.store.response.GetAllBrandListResponse
import com.desynatuser.ui.home.store.response.GetAllColorListResponse
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.ui.home.store.response.GetSizeListResponse
import com.desynatuser.ui.home.store.response.GetStoreSortListResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class ViewAllProductFragment : BaseFragment() {
    private var binding: FragmentViewAllProductBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()

    private var productAdapter: ProductAdapter? = null
    private var productList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()
    private var sortList = ArrayList<GetStoreSortListResponse.Data>()

    private var type = 1 // 1-Best Selling, 2-Top Rated
    private var selectedPos = -1
    private var sortFilter: String? = null
    private var search: Job? = null
    private var lastQuery: String? = null

    private var sortAdapter: SortListAdapter? = null
    private var filterColorAdapter: FilterColorAdapter? = null
    private var filterBrandAdapter: FilterBrandAdapter? = null
    private var filterSizeAdapter: FilterSizeAdapter? = null
    private var filterCategoryAdapter: FilterCategoryAdapter? = null

    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var sizeList = ArrayList<GetSizeListResponse.Data>()
    private var colorList = ArrayList<GetAllColorListResponse.Data>()
    private var brandList = ArrayList<GetAllBrandListResponse.Data>()

    private var selectedBrandList = ArrayList<GetAllBrandListResponse.Data>()
    private var selectedSizeList = ArrayList<GetSizeListResponse.Data>()
    private var selectedColorList = ArrayList<GetAllColorListResponse.Data>()
    private var selectedCategory: GetCategoryResponse.Data? = null

    private var selectedDiscount: Int? = null
    private var selectedRating: Int? = null
    private var selectedDeliveryTime: String? = null
    private var selectedMinPrice: Int = 0
    private var selectedMaxPrice: Int = 0

    private var isObserver = false
    private var isSortObserver = false
    private var isBrandObserver = false
    private var isSizeObserver = false
    private var isColorObserver = false

    private val newColorList = ArrayList<GetAllColorListResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewAllProductBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        type = arguments?.getInt("type") ?: 1
        categoryList.clear()
        SharedPreference.get(requireActivity()).getCategoryList()?.let { categoryList.addAll(it) }

        binding?.apply {
            tvToolbar.text = if (type == 1) getString(R.string.best_selling) else getString(R.string.top_rated)

            rvProduct.isNestedScrollingEnabled = false

            productAdapter = ProductAdapter(
                requireActivity(), productList, "", 3, object : ProductAdapter.ProductClick {
                    override fun onProductClick(position: Int, type: Int) {
                        when (type) {
                            1 -> {
                                val bundle = Bundle().apply {
                                    putString("productId", productList[position].id.toString())
                                }
                                findNavController().navigate(
                                    R.id.action_viewAllProductFragment_to_productDetailFragment,
                                    bundle
                                )
                            }

                            2 -> {
                                selectedPos = position
                                addToWishList()
                            }
                        }
                    }
                })
            rvProduct.adapter = productAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600.milliseconds)
                        getProducts(isLoading = false)
                    }
                }
            })
        }

        getProducts()
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

            ivMic.setOnClickListener {
                openSpeechDialog()
            }
        }
    }

    private fun getProducts(isLoading: Boolean = true) {
        val map = HashMap<String, Any>()
        if (sortFilter != null) map["sort_filter"] = sortFilter.toString()
        if (lastQuery != null) map["search"] = lastQuery.toString()
        if (selectedCategory != null) map["category_id"] =
            arrayListOf(selectedCategory?.id.toString())
        if (selectedBrandList.isNotEmpty()) map["brand_id"] = selectedBrandList.map { it.id }
        if (selectedMaxPrice != 0) {
            map["price_min"] = selectedMinPrice
            map["price_max"] = selectedMaxPrice
        }
        if (selectedColorList.isNotEmpty()) map["color_id"] = selectedColorList.map { it.id }
        if (selectedSizeList.isNotEmpty()) map["size_id"] = selectedSizeList.map { it.id }
        if (selectedDiscount != 0 && selectedDiscount != null) map["discount_range"] =
            selectedDiscount.toString()
        if (selectedRating != 0 && selectedRating != null) map["rating"] = selectedRating.toString()
        if (selectedDeliveryTime != "" && selectedDeliveryTime != null) map["delivery_time"] =
            selectedDeliveryTime.toString()

        if (type == 1) {
            viewModel.getBestSellingProductList(map)
        } else {
            viewModel.getTopRatedProductList(map)
        }

        if (isLoading) {
            binding?.progressBar?.isVisible = true
        }
        isObserver = true
    }

    private fun addToWishList() {
        val map = HashMap<String, Any>()
        map["product_id"] = productList[selectedPos].id
        viewModel.addToWishlist(map)
        binding?.progressBar?.isVisible = true
        isObserver = true
    }

    private fun getStoreSortList() {
        viewModel.getStoreSorList()
        isSortObserver = true
    }

    private fun getAllFilterList() {
        viewModel.getColorList()
        viewModel.getBrandList()
        viewModel.getSizeList()
        binding?.progressBar?.isVisible = true
        isBrandObserver = true
        isSizeObserver = true
        isColorObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.viewAllProductResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                binding?.progressBar?.isVisible = false
                productList.clear()
                response?.data?.allProduct?.data?.let { productList.addAll(it) }
                productAdapter?.notifyDataSetChanged()
                binding?.tvNoDataFound?.isVisible = productList.isEmpty()
                isObserver = false
            }
        }

        viewModel.addToWishlistResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                binding?.progressBar?.isVisible = false
                if (productList[selectedPos].wishlist == 1) {
                    showToast(getString(R.string.product_removed_from_your_wishlist_successfully))
                    productList[selectedPos].wishlist = 0
                } else {
                    showToast(getString(R.string.product_added_to_your_wishlist_successfully))
                    productList[selectedPos].wishlist = 1
                }
                productAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.brandListResult.observe(viewLifecycleOwner) { response ->
            if (isBrandObserver) {
                binding?.progressBar?.isVisible = false
                brandList.clear()
                brandList.addAll(response.data)
                filterBrandAdapter?.notifyDataSetChanged()
                isBrandObserver = false
            }
        }

        viewModel.sizeListResult.observe(viewLifecycleOwner) { response ->
            if (isSizeObserver) {
                binding?.progressBar?.isVisible = false
                sizeList.clear()
                sizeList.addAll(response.data)
                filterSizeAdapter?.notifyDataSetChanged()
                isSizeObserver = false
            }
        }

        viewModel.colorListResult.observe(viewLifecycleOwner) { response ->
            if (isColorObserver) {
                binding?.progressBar?.isVisible = false
                colorList.clear()
                colorList.addAll(response.data)
                newColorList.clear()
                if (colorList.size > 10) newColorList.addAll(colorList.take(10)) else newColorList.addAll(
                    colorList
                )
                filterColorAdapter?.notifyDataSetChanged()
                isColorObserver = false
            }
        }

        viewModel.storeSortResult.observe(viewLifecycleOwner) { response ->
            if (isSortObserver) {
                binding?.progressBar?.isVisible = false
                sortList.clear()
                sortList.addAll(response.data)
                sortAdapter?.notifyDataSetChanged()
                isSortObserver = false
            }
        }
    }

    private fun sortProductDialog() {
        var sort = ""
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = SortProductBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            tvToolbar.setOnClickListener { dialog.dismiss() }

            for (i in sortList.indices) {
                sortList[i].isSelected = sortList[i].id.toString() == sortFilter
            }

            sortAdapter =
                SortListAdapter(requireActivity(), sortList, object : SortListAdapter.ClickOnItem {
                    override fun onClickItem(position: Int) {
                        sort = sortList[position].id.toString()
                    }
                })
            rvSort.adapter = sortAdapter

            if (sortList.isEmpty()) getStoreSortList()

            tvClear.setOnClickListener {
                sortFilter = null
                getProducts()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {
                if (sort != "") {
                    sortFilter = sort
                    getProducts()
                    dialog.dismiss()
                } else {
                    showToast(getString(R.string.please_select))
                }
            }
        }
        dialog.show()
    }

    private fun productFilterDialog() {
        var category: GetCategoryResponse.Data? = selectedCategory
        var min = selectedMinPrice
        var max = selectedMaxPrice

        var discount: Int? = selectedDiscount
        var rating: Int? = selectedRating
        var deliveryTime: String? = selectedDeliveryTime

        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ProductFilterBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.white)
        dialog.setCancelable(false)

        dialogBinding.apply {
            brandList.forEach { brand ->
                brand.isSelected = selectedBrandList.any { it.id == brand.id }
            }
            colorList.forEach { color ->
                color.isSelected = selectedColorList.any { it.id == color.id }
            }
            sizeList.forEach { size -> size.isSelected = selectedSizeList.any { it.id == size.id } }

            tvMinPrice.text = "0 ${CommonMethod.getCurrency(requireActivity())}"
            tvMaxPrice.text = "1000 ${CommonMethod.getCurrency(requireActivity())}"
            tvCentrePrice.text = if (selectedMaxPrice != 0) "$selectedMaxPrice ${
                CommonMethod.getCurrency(requireActivity())
            }" else "500 ${CommonMethod.getCurrency(requireActivity())}"

            when (selectedDiscount) {
                10 -> radioGroupDiscount.check(R.id.rb10)
                20 -> radioGroupDiscount.check(R.id.rb20)
                30 -> radioGroupDiscount.check(R.id.rb30)
                40 -> radioGroupDiscount.check(R.id.rb40)
                50 -> radioGroupDiscount.check(R.id.rb50)
            }

            when (selectedRating) {
                1 -> radioGroupRating.check(R.id.rb1Star)
                2 -> radioGroupRating.check(R.id.rb2Star)
                3 -> radioGroupRating.check(R.id.rb3Star)
                4 -> radioGroupRating.check(R.id.rb4Star)
                5 -> radioGroupRating.check(R.id.rb5Star)
            }

            when (selectedDeliveryTime) {
                "2" -> rgDeliveryTime.check(R.id.rb2Day)
                "3" -> rgDeliveryTime.check(R.id.rb3day)
                "6" -> rgDeliveryTime.check(R.id.rb6day)
            }

            newColorList.clear()
            if (colorList.size > 10) newColorList.addAll(colorList.take(10)) else newColorList.addAll(
                colorList
            )

            filterColorAdapter = FilterColorAdapter(
                requireActivity(), newColorList, object : FilterColorAdapter.OnClick {
                    override fun onClickItem(position: Int) {
                        colorList.find { it.id == newColorList[position].id }?.isSelected =
                            newColorList[position].isSelected
                    }
                })
            rvColor.adapter = filterColorAdapter

            filterBrandAdapter = FilterBrandAdapter(
                requireActivity(), brandList, object : FilterBrandAdapter.OnClick {
                    override fun onClickItem(position: Int) {}
                })
            rvBrand.adapter = filterBrandAdapter

            filterSizeAdapter =
                FilterSizeAdapter(requireActivity(), sizeList, object : FilterSizeAdapter.OnClick {
                    override fun onClickItem(position: Int) {}
                })
            rvSize.adapter = filterSizeAdapter

            filterCategoryAdapter = FilterCategoryAdapter(
                requireActivity(), categoryList, object : FilterCategoryAdapter.OnClick {
                    override fun onClickItem(position: Int) {
                        category = categoryList[position]
                    }
                })
            filterCategoryAdapter?.setSelectedCategoryId(selectedCategory?.id)
            rvCategory.adapter = filterCategoryAdapter

            if (colorList.isEmpty() || sizeList.isEmpty() || brandList.isEmpty()) getAllFilterList()

            priceSlider.addOnChangeListener { slider, _, _ ->
                val values = slider.values
                min = values[0].toInt()
                max = values[1].toInt()
                tvMinPrice.text = "$min ${CommonMethod.getCurrency(requireActivity())}"
                tvCentrePrice.text = "$max ${CommonMethod.getCurrency(requireActivity())}"
                tvCentrePrice.isVisible = !(max == 0 || max == 1000)
                moveLabel(tvCentrePrice, slider, 1)
            }

            radioGroupDiscount.setOnCheckedChangeListener { _, checkedId ->
                discount = when (checkedId) {
                    R.id.rb10 -> 10
                    R.id.rb20 -> 20
                    R.id.rb30 -> 30
                    R.id.rb40 -> 40
                    R.id.rb50 -> 50
                    else -> null
                }
            }

            radioGroupRating.setOnCheckedChangeListener { _, checkedId ->
                rating = when (checkedId) {
                    R.id.rb1Star -> 1
                    R.id.rb2Star -> 2
                    R.id.rb3Star -> 3
                    R.id.rb4Star -> 4
                    R.id.rb5Star -> 5
                    else -> null
                }
            }

            rgDeliveryTime.setOnCheckedChangeListener { _, checkedId ->
                deliveryTime = when (checkedId) {
                    R.id.rb2Day -> "2"
                    R.id.rb3day -> "3"
                    R.id.rb6day -> "6"
                    else -> null
                }
            }

            tvToolbar.setOnClickListener { dialog.dismiss() }

            tvClear.setOnClickListener {
                selectedCategory = null
                selectedMinPrice = 0
                selectedMaxPrice = 0
                selectedDiscount = null
                selectedRating = null
                selectedDeliveryTime = null
                selectedSizeList.clear()
                selectedColorList.clear()
                selectedBrandList.clear()
                getProducts()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {
                selectedMaxPrice = max
                selectedMinPrice = min
                selectedCategory = category
                selectedDeliveryTime = deliveryTime
                selectedRating = rating
                selectedDiscount = discount
                selectedBrandList.clear()
                brandList.forEach { if (it.isSelected) selectedBrandList.add(it) }
                selectedSizeList.clear()
                sizeList.forEach { if (it.isSelected) selectedSizeList.add(it) }
                selectedColorList.clear()
                colorList.forEach { if (it.isSelected) selectedColorList.add(it) }

                getProducts()
                dialog.dismiss()
            }

            tvShowMoreColor.setOnClickListener {
                if (tvShowMoreColor.text == getString(R.string.show_more_color)) {
                    newColorList.clear()
                    newColorList.addAll(colorList)
                    tvShowMoreColor.text = getString(R.string.show_less_color)
                } else {
                    newColorList.clear()
                    if (colorList.size > 10) newColorList.addAll(colorList.take(10)) else newColorList.addAll(
                        colorList
                    )
                    tvShowMoreColor.text = getString(R.string.show_more_color)
                }
                filterColorAdapter?.notifyDataSetChanged()
            }
        }
        dialog.show()
    }

    private fun moveLabel(label: TextView, slider: RangeSlider, index: Int) {
        val thumbX =
            slider.thumbRadius * 2 + (slider.width - slider.thumbRadius * 4) * slider.values[index] / (slider.valueTo - slider.valueFrom)
        label.x = thumbX - label.width / 2f
    }

    private fun openSpeechDialog() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
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

    private val speechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull()
                if (!text.isNullOrEmpty()) {
                    binding?.etSearch?.setText(text)
                    binding?.etSearch?.setSelection(text.length)
                }
            }
        }
}
