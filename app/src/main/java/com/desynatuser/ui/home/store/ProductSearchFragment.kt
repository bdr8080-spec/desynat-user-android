package com.desynatuser.ui.home.store

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
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
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductSearchFragment : BaseFragment() {
    private var binding : FragmentProductSearchBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var isObserver = false
    var isRecommendedObserver = false
    var searchList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()

    var recentSearchAdapter: RecentSearchAdapter?=null
    var recentSearchList = ArrayList<GetRecentSearchListResponse.Data>()
    var recommendedList = ArrayList<GetRecentSearchListResponse.Data>()
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
                    detectObjects(uri)
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

            latestSearchAdapter = LatestSearchAdapter(requireActivity(), recentSearchList,object: LatestSearchAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    val bundle = Bundle().apply {
                        putString("search", recentSearchList[position].name)
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
                        putString("search", recommendedList[position].name)
                    }
                    findNavController().navigate(R.id.action_searchProductFragment_to_searchProductListFragment,bundle)
                }

            })
            rvRecommended.adapter = recommendedAdapter

            getRecentSearch()
            getRecommendedSearchList()
        }
    }

    private fun getRecentSearch() {
        viewModel.getRecentSearchList()
        isObserver = true
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
                    latestSearchAdapter?.notifyDataSetChanged()
                }
                isObserver = false
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

    private fun detectObjects(uri: Uri) {
        val image = InputImage.fromFilePath(requireContext(), uri)

        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        val detector = ObjectDetection.getClient(options)

        detector.process(image)
            .addOnSuccessListener { objects ->
                Log.d("ML_KIT", "Detected objects: ${objects.size}")

                if (objects.isEmpty()) return@addOnSuccessListener

                val detectedLabel = objects
                    .firstOrNull { it.labels.isNotEmpty() }
                    ?.labels
                    ?.firstOrNull()
                    ?.text

                if (detectedLabel.isNullOrEmpty()) {
                    Log.d("ML_KIT", "No valid label found")
                    return@addOnSuccessListener
                }

                val navController = findNavController()
                if (
                    !hasNavigatedFromML &&
                    navController.currentDestination?.id == R.id.home
                ) {
                    hasNavigatedFromML = true

                    val bundle = Bundle().apply {
                        putString("search", detectedLabel)
                    }

                    navController.navigate(
                        R.id.action_searchProductFragment_to_searchProductListFragment,
                        bundle
                    )
                }
            }
    }


    private fun cropAndSend(
        objects: List<DetectedObject>,
        uri: Uri
    ) {
        val bitmap = MediaStore.Images.Media.getBitmap(
            requireContext().contentResolver,
            uri
        )

        objects.forEachIndexed { index, obj ->
            val box = obj.boundingBox

            val cropped = Bitmap.createBitmap(
                bitmap,
                box.left.coerceAtLeast(0),
                box.top.coerceAtLeast(0),
                box.width().coerceAtMost(bitmap.width),
                box.height().coerceAtMost(bitmap.height)
            )

           // sendToBackend(cropped, obj.labels.firstOrNull()?.text)
        }
    }


}