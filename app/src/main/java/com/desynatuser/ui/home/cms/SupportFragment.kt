package com.desynatuser.ui.home.cms

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.TicketAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentSupportBinding
import com.desynatuser.databinding.RaiseTicketDialogBinding
import com.desynatuser.ui.home.cms.response.GetAllRaisedTicketResponse
import com.desynatuser.ui.home.cms.response.GetContactResponse
import com.desynatuser.ui.home.cms.response.GetTicketTypeResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.ImagePicker
import com.desynatuser.utils.ProgressDialogUtils
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.getValue

@AndroidEntryPoint
class SupportFragment : BaseFragment() {
    private var binding: FragmentSupportBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var isRaiseObserver = false
    private var isRaised = false
    private var isTicketTypeObserver = false
    private var isContactUs = false
    private var whatsappNumber = ""

    private var ticketList = ArrayList<GetAllRaisedTicketResponse.Data.Data>()
    private var ticketTypeList = ArrayList<GetTicketTypeResponse.Data>()
    private var ticketAdapter: TicketAdapter?=null
    private var search: Job? = null
    private var lastQuery: String? = null
    private var type = 0

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var pagination=false

    private var dialog: Dialog?=null
    private var dialogBinding: RaiseTicketDialogBinding?=null

    var isImageObserver = false
    private lateinit var imagePicker: ImagePicker
    private var imageFile: File? = null
    private var imageUrl:String?=null

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

    private var selectedTicketType: GetTicketTypeResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSupportBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.support)

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
                        if (!isObserver){
                            currentPage = 1
                            getRaisedTicket(false)
                        }
                    }

                }
            })

            ticketAdapter = TicketAdapter(requireActivity(),ticketList,object : TicketAdapter.OnClick{
                override fun click(position: Int, type: Int) {
                    val bundle = Bundle().apply {
                        putString("ticketData", Gson().toJson(ticketList[position]))
                    }
                    findNavController().navigate(R.id.action_supportFragment_to_ticketDetailFragment,bundle)
                }

            })
            rvTicket.adapter = ticketAdapter

            nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
                val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

                if (lastChild != null) {
                    val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                    if (diff == 0 && scrollY > oldScrollY) {
                        if (type==2) {
                            if (totalItem == 0)
                                return@setOnScrollChangeListener
                            if (!isObserver && !pagination && currentPage < lastPage) {
                                pagination = true
                                currentPage += 1
                                getRaisedTicket(false)
                                binding?.progressBar?.isVisible = true
                            }
                        }
                    }
                }
            }

            when(type){
                0->{
                   contact()
                }
                1->{
                   ticketManagement()
                }
            }

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    imageFile = CommonMethod.uriToFile(requireActivity(), it)
                    dialogBinding?.ivDoc?.setImageURI(it)
                    dialogBinding?.clDocument1Placeholder?.isVisible = false
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            clSupportEmail.setOnClickListener {
                val email = tvSupportEmailAddress.text.trim()
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
//                    putExtra(Intent.EXTRA_SUBJECT, "Your Subject Here")
//                    putExtra(Intent.EXTRA_TEXT, "Hello, this is the email body.")
                }

                startActivity(emailIntent)
            }

            clSupportNumber.setOnClickListener {
                val number = tvSupportMobileNumber.text.trim()
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${number}")
                }
                startActivity(dialIntent)
            }

            clWhatsapp.setOnClickListener {
                val number = whatsappNumber
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/${number}")
                }
                startActivity(intent)
            }

            clSupportTeam.setOnClickListener {
                if (SharedPreference.get(requireActivity()).accessToken!="") {
                    findNavController().navigate(R.id.action_supportFragment_to_tidioChatFragment)
                }
                else{
                    CommonMethod.showLoginAlert(requireActivity())
                }
            }

            tvContact.setOnClickListener {
              contact()
            }

            tvTicketManagement.setOnClickListener {
               ticketManagement()
            }

            ivAddTicket.setOnClickListener {
                raiseTicketDialog()
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
                binding?.progressBar?.isVisible = false
                isObserver = false
            }
        }


        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    imageUrl = response.data.url
                    raiseTicket()

                }
                isImageObserver = false
            }
        }

        viewModel.getRaisedTicketResult.observe(viewLifecycleOwner){response ->
            if (isObserver){
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false

                if (isRaised){
                    isRaised = false
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.ticket_raised_successfully))
                }

                currentPage = response.data.currentPage
                totalItem = response.data.total

                lastPage = response.data.lastPage

                val oldSize = ticketList.size
                if (!pagination){
                    ticketList.clear()
                }
                val newItems = response.data.data ?: emptyList()

                ticketList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        ticketAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    ticketAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isObserver = false
            }
        }

        viewModel.raiseTicketResult.observe(viewLifecycleOwner) { response ->
            if (isRaiseObserver) {
                isRaised = true
                selectedTicketType = null
                imageUrl = null
                imageFile = null
                currentPage = 1
                getRaisedTicket(false)
                isRaiseObserver = false
            }
        }
        viewModel.contactUsResult.observe(viewLifecycleOwner) { response ->
            if (isContactUs) {
                ProgressDialogUtils.getInstance().hideProgress()
                setContactUsData(response.data.contentEnglish)
                isContactUs = false
            }
        }

        viewModel.affiliateRaiseTicketResult.observe(viewLifecycleOwner) { response ->
            if (isRaiseObserver) {
                isRaised = true
                selectedTicketType = null
                imageUrl = null
                imageFile = null
                currentPage = 1
                getRaisedTicket(false)
                isRaiseObserver = false
            }
        }

        viewModel.getTicketTypeResult.observe(viewLifecycleOwner) { response ->
            if (isTicketTypeObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                ticketTypeList.clear()
                ticketTypeList.addAll(response.data)
                isTicketTypeObserver = false
            }
        }

        viewModel.getAffiliateRaisedTicketResult.observe(viewLifecycleOwner){response ->
            if (isObserver){
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isRaised){
                    isRaised = false
                    CommonMethod.showSuccessDialog(requireActivity(),getString(R.string.ticket_raised_successfully))
                }
                currentPage = response.data.currentPage
                totalItem = response.data.total

                lastPage = response.data.lastPage

                val oldSize = ticketList.size
                if (!pagination){
                    ticketList.clear()
                }
                val newItems = response.data.data ?: emptyList()

                ticketList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        ticketAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    ticketAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isObserver = false
            }
        }

    }

    private fun setContactUsData(contactData: GetContactResponse.Data.ContentEnglish) {
            binding?.apply {
                tvSupportEmailAddress.text = contactData?.email
                tvSupportMobileNumber.text = contactData?.mobile
                whatsappNumber = contactData?.whatsapp.toString()
            }
    }

    private fun ticketManagement(){
        binding?.apply {
            type = 1
            getRaisedTicket()
            if (ticketList.isEmpty()){
                getTicketType()
            }

            llTicket.isVisible = true
            llContact.isVisible = false
            tvContact.background = null
            tvContact.setTextColor(requireActivity().getColor(R.color.black))
            tvTicketManagement.setTextColor(requireActivity().getColor(R.color.white))
            tvTicketManagement.setBackgroundResource(R.drawable.selected_right_tab)
        }
    }

    private fun contact(){
        binding?.apply {
            type = 0
            getContactUs()
            llTicket.isVisible = false
            llContact.isVisible = true
            tvContact.setBackgroundResource(R.drawable.selected_left_tab)
            tvContact.setTextColor(requireActivity().getColor(R.color.white))
            tvTicketManagement.setTextColor(requireActivity().getColor(R.color.black))
            tvTicketManagement.background = null
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
                }
            }
        }

    private fun raiseTicketDialog() {
        dialog = Dialog(requireActivity(), R.style.CustomDialog)
        dialogBinding = RaiseTicketDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding?.root.let { it?.let { it1 -> dialog?.setContentView(it1) } }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding?.apply {

            CommonMethod.removeSpace(etTitle)
            CommonMethod.removeSpace(etDescription)

            ivDoc.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }

            etTicketType.setOnClickListener {
                showTicketTypePopup(dialogBinding)
            }

            btnSubmit.setOnClickListener{

                if (validation()){

                    if (imageFile!=null){
                        uploadFile(imageFile!!)
                    }
                    else{
                        raiseTicket()
                    }


                    dialog?.dismiss()
                }

            }

        }

        dialog?.show()
    }

    private fun validation():Boolean{

        if (dialogBinding?.etTicketType?.text?.isEmpty() == true) {
            showToast(getString(R.string.please_select_ticket_type))
            return false
        }
        else if (dialogBinding?.etTitle?.text?.isEmpty() == true) {
            showToast(getString(R.string.please_enter_title))
            return false
        }
        else if (dialogBinding?.etDescription?.text?.isEmpty() == true) {
            showToast(getString(R.string.please_enter_description))
            return false
        }

        return true
    }


    private fun getRaisedTicket(isLoading: Boolean=true){
        val map = HashMap<String,Any>()
        if (lastQuery!=null){
            map["search"] = lastQuery.toString()
        }
        map["limit"] = "10"
        map["page"] = currentPage
        if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
            viewModel.getAffiliateRaisedTicket(map)
        }
        else{
            viewModel.getRaisedTicket(map)
        }
        if (isLoading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun raiseTicket() {
        val map = HashMap<String,Any>()
        map["ticket_type_id"] = selectedTicketType?.id.toString()
        map["title"] = dialogBinding?.etTitle?.text?.trim().toString()
        map["description"] = dialogBinding?.etDescription?.text?.trim().toString()
        if (imageUrl!=null){
            map["file"] = imageUrl.toString()
        }

        if (SharedPreference.get(requireActivity()).affiliateAccessToken!=""){
            viewModel.affiliateRaiseTicket(map)
        }
        else{
            viewModel.raiseTicket(map)
        }
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isRaiseObserver = true
    }

    fun uploadFile(image:File){
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    private fun getTicketType(){
        viewModel.getTicketType()
        isTicketTypeObserver = true
    }

    private fun getContactUs(){
        val map = HashMap<String, Any>()
        map["type"] = 1
        viewModel.contactUs(map)
        isContactUs = true
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = ticketList.isEmpty()
    }

    private fun showTicketTypePopup(dialogBinding: RaiseTicketDialogBinding?) {

        val popup = android.widget.PopupMenu(requireContext(), dialogBinding?.etTicketType)

        ticketTypeList.forEachIndexed { index, item ->
            popup.menu.add(0, index, index, if (Constants.language=="ar")item.nameArabic else item.name) // use correct field name
        }

        popup.setOnMenuItemClickListener { menuItem ->
            selectedTicketType = ticketTypeList[menuItem.itemId]

            dialogBinding?.etTicketType?.setText(if (Constants.language=="ar")selectedTicketType?.nameArabic else selectedTicketType?.name)

            true
        }

        popup.show()
    }

}
