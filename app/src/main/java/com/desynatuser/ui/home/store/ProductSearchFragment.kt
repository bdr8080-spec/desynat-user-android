package com.desynatuser.ui.home.store

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
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
import com.desynatuser.ui.home.fragment.response.GetRecentSearchListResponse
import com.desynatuser.ui.home.fragment.response.GetRecommendedResponse
import com.desynatuser.ui.home.imageDetector.AnnotateRequest
import com.desynatuser.ui.home.imageDetector.Feature
import com.desynatuser.ui.home.imageDetector.ImageContent
import com.desynatuser.ui.home.imageDetector.VisionRequestBody
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.desynatuser.vision.VisionRetrofit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class ProductSearchFragment : BaseFragment() {
    private var binding : FragmentProductSearchBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var isLatestObserver = false
    var isRecommendedObserver = false
    var searchList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()

    var recentSearchAdapter: RecentSearchAdapter?=null
    var recentSearchList = ArrayList<GetRecentSearchListResponse.Data>()
    var latestSearchList = ArrayList<GetRecentSearchListResponse.Data>()
    var recommendedList = ArrayList<GetRecommendedResponse.Data>()
    var recommendedAdapter: RecommendedAdapter?=null
    var latestSearchAdapter: LatestSearchAdapter?=null

    private var search: Job? = null
    private var lastQuery: String? = null

    private lateinit var imagePicker: ImagePicker
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            imagePicker.onCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePicker.onGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            imagePicker.onPermissionResult(granted)
        }

    private var hasNavigatedFromML = false

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

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    val bitmap = uriToBitmap(requireContext(), uri)
                    callVisionApi(bitmap)
                }
            }

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

            latestSearchAdapter = LatestSearchAdapter(requireActivity(), latestSearchList,object: LatestSearchAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    val bundle = Bundle().apply {
                        putString("search", latestSearchList[position].name)
                    }
                    findNavController().navigate(R.id.action_searchProductFragment_to_searchProductListFragment,bundle)
                }

            })
            rvLatestSearch.adapter = latestSearchAdapter

            recentSearchList.clear()
            recentSearchAdapter = RecentSearchAdapter(requireActivity(), recentSearchList,object: RecentSearchAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    val bundle = Bundle().apply {
                        putString("search", recentSearchList[position].name)
                    }
                    findNavController().navigate(R.id.action_searchProductFragment_to_searchProductListFragment,bundle)
                }

            })
            rvRecentSearch.adapter = recentSearchAdapter

            recommendedAdapter = RecommendedAdapter(requireActivity(), recommendedList,object: RecommendedAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    val bundle = Bundle().apply {
                        putString("search", recommendedList[position].nameEnglish)
                    }
                    findNavController().navigate(R.id.action_searchProductFragment_to_searchProductListFragment,bundle)
                }

            })
            rvRecommended.adapter = recommendedAdapter

            getRecentSearch()
            getLatestSearch()
            getRecommendedSearchList()
        }
    }

    private fun getRecentSearch() {
        viewModel.getRecentSearchList()
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

 private fun getLatestSearch() {
        viewModel.getLatestSearchList()
     isLatestObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    private fun getRecommendedSearchList() {
        viewModel.getRecommendedSearchList()
        isRecommendedObserver = true
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
                    val bundle = Bundle().apply {
                        putString("search", etSearch.text.toString())
                    }
                    findNavController().navigate(
                        R.id.action_searchProductFragment_to_searchProductListFragment,
                        bundle
                    )

             //   getHomeSearch()
            }

            ivMic.setOnClickListener {
                openSpeechDialog()
            }

            ivCamera.setOnClickListener {
                imagePicker.showImageSourceDialog()
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
        viewModel.getLatestSearchListResult.observe(viewLifecycleOwner) { response ->
            if (isLatestObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                   latestSearchList.clear()
                    latestSearchList.addAll(response.data)
                    latestSearchAdapter?.notifyDataSetChanged()
                }
                isLatestObserver = false
            }
        }
        viewModel.getRecommendedSearchListResult.observe(viewLifecycleOwner) { response ->
            if (isRecommendedObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    recommendedList.clear()
                    recommendedList.addAll(response.data)
                    recommendedAdapter?.notifyDataSetChanged()
                }
                isRecommendedObserver = false
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


    fun callVisionApi(bitmap: Bitmap) {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())

        val base64Image = bitmapToBase64(bitmap)

        val request = VisionRequestBody(
            requests = listOf(
                AnnotateRequest(
                    image = ImageContent(base64Image),
                    features = listOf(
                        Feature(type = "LABEL_DETECTION", maxResults = 5),
                        Feature(type = "OBJECT_LOCALIZATION", maxResults = 5),

                        Feature(type = "LOGO_DETECTION", maxResults = 5),
                        Feature(type = "WEB_DETECTION", maxResults = 5),
                        Feature(type = "IMAGE_PROPERTIES", maxResults = 5),
                        Feature(type = "TEXT_DETECTION", maxResults = 5),
                        Feature(type = "BARCODE_DETECTION", maxResults = 5)
                    )
                )
            )
        )

        lifecycleScope.launch {
            try {
                val response = VisionRetrofit.api.detectImage(
                    apiKey = "AIzaSyBk1iHVph8nvXAn0uigTvPzKDpb9cslR18",
                    request = request
                )
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("VISION_RESPONSE_123", response.body().toString() ?: "Error")
                val labels = response.body()?.responses
                    ?.firstOrNull()
                    ?.labelAnnotations
                    ?.map { it.description }
                    ?: emptyList()

                searchThroughImage(labels)

            } catch (e: Exception) {
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("VISION_ERROR", e.message ?: "Error")
            }
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }


    private fun searchThroughImage(detectedLabel:List<String>){
        val navController = findNavController()
        if (
            !hasNavigatedFromML &&
            navController.currentDestination?.id == R.id.searchProduct
        ) {
            hasNavigatedFromML = true

            val bundle = Bundle().apply {
                putStringArrayList("searchList", ArrayList(detectedLabel))
            }
            findNavController().navigate(
                R.id.action_searchProductFragment_to_searchProductListFragment,
                bundle
            )
        }
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

}