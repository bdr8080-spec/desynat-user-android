package com.desynatuser.ui.home.catalog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatuser.R
import com.desynatuser.adapter.SubCategoryAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentSubCategoryBinding
import com.desynatuser.databinding.MicDialogBinding
import com.desynatuser.databinding.SizeGuideChartDialogBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.profile.response.GetSubCategoryResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubCategoryFragment : BaseFragment() {
    private var binding: FragmentSubCategoryBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var subCategoryAdapter : SubCategoryAdapter?=null
    private var isFromSwipe=false
    private var searchSubCategory: Job? = null
    private var lastQuery: String? = null
    private var categoryData: GetCategoryResponse.Data?=null

    private var isMicDialogOpen = false
    private var speechRecognizer: SpeechRecognizer? = null
    private var speechIntent: Intent? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubCategoryBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        val itemJson = arguments?.getString("categoryData")
        if (!itemJson.isNullOrEmpty()) {
            categoryData = Gson().fromJson(itemJson, GetCategoryResponse.Data::class.java)
        }
        binding?.apply {
            CommonMethod.removeSpace(etSearch)
            toolbar.tvToolbar.text = getString(R.string.sub_category)

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            subCategoryList.clear()
            subCategoryAdapter = SubCategoryAdapter(requireActivity(),subCategoryList,object :SubCategoryAdapter.SubCategoryClick{
                override fun onSubCategoryClick(position: Int,type:Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("subCategoryData", Gson().toJson(subCategoryList[position]))
                            }
                            findNavController().navigate(R.id.action_subCategoryFragment_to_storeFragment,bundle)
                        }
                        2->{
                            if(position==-1) {
                                binding?.tvNoDataFound?.isVisible = true
                            }
                            else{
                                binding?.tvNoDataFound?.isVisible = false
                            }
                        }

                    }

                }

            })
            rvSubcategory.adapter = subCategoryAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    subCategoryAdapter?.filter(query)

                    /* if (query == lastQuery) return
                     lastQuery = query
                     searchSubCategory?.cancel()
                     searchSubCategory = lifecycleScope.launch {
                         delay(600)
                         isFromSwipe = false
                         if (!isObserver)
                             getAllSubCategory(search =  query.ifEmpty { null })
                     }*/

                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getAllSubCategory(false)
            }

           /* nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
                val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

                if (lastChild != null) {
                    val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                    if (diff == 0 && scrollY > oldScrollY) {
                        if(totalItem==0)
                            return@setOnScrollChangeListener
                        if (!isObserver && currentPage < lastPage) {
                            pagination = true
                            isFromSwipe = false
                            currentPage += 1
                            getAllSubCategory(false)
                            binding?.progressBar?.isVisible = true
                        }
                    }
                }
            }*/


        }
        getAllSubCategory()
    }

    private fun getAllSubCategory(loading:Boolean=true,search:String?=null){
        viewModel.getSubCategory(categoryData?.id.toString(),search)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdminStory.setOnClickListener {

            }

           /* etSearch.setOnClickListener {
                findNavController().navigate(R.id.action_subCategoryFragment_to_searchProductFragment)
            }*/

            ivMic.setOnClickListener {
                openSpeechDialog()
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                errorMessage?.let { showToast(it) }
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.subCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                subCategoryList.clear()
                subCategoryList.addAll(response.data)
                subCategoryAdapter?.setData(subCategoryList)
                checkData()
                subCategoryAdapter?.notifyDataSetChanged()
                isFromSwipe = false
                isObserver = false
            }
        }

    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = subCategoryList.isEmpty()
    }

    private fun micDialog() {
        isMicDialogOpen = true
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = MicDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            initSpeechRecognizer(dialog, dialogBinding)
        }

        dialog.show()

        dialog.setOnDismissListener {
            isMicDialogOpen = false

            try { speechRecognizer?.stopListening() } catch (_: Exception) {}

            try { speechRecognizer?.destroy() } catch (_: Exception) {}

            speechRecognizer = null
        }
    }



    private fun initSpeechRecognizer(dialog: Dialog, dialogBinding: MicDialogBinding) {

        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.mic_ripple)
        dialogBinding.ivMic.startAnimation(anim)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer!!.setRecognitionListener(object : RecognitionListener {
            override fun onError(error: Int) {

                if (!isMicDialogOpen) return

                dialogBinding.ivMic.clearAnimation()

                if (error == SpeechRecognizer.ERROR_NO_MATCH ||
                    error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {

                    dialog.dismiss()
                    return
                }

                showToast(getString(R.string.speak_clearly_or_try_again))
                dialog.dismiss()
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }

            override fun onPartialResults(p0: Bundle?) {
            }

            override fun onReadyForSpeech(p0: Bundle?) {
            }

            override fun onResults(results: Bundle?) {
                if (!isMicDialogOpen) return

                dialogBinding.ivMic.clearAnimation()

                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!text.isNullOrEmpty()) {
                    binding?.etSearch?.setText(text)
                    binding?.etSearch?.setSelection(text.length)
                }

                dialog.dismiss()
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBeginningOfSpeech() {

            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
                if (!isMicDialogOpen) return
                dialogBinding.ivMic.clearAnimation()
            }
        })

        lifecycleScope.launch {
            delay(400)

            if (isMicDialogOpen) {
                speechRecognizer?.startListening(speechIntent)
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
            showToast(getString(R.string.speech_recognition_not_supported))
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