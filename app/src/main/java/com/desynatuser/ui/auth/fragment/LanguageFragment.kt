package com.desynatuser.ui.auth.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.base.BaseFragment
import com.desynatuser.databinding.FragmentLanguageBinding
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.Constants
import com.desynatuser.utils.SharedPreference
import com.desynatuser.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : BaseFragment() {
    private var binding: FragmentLanguageBinding? = null
    private var from = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        from = arguments?.getString("from").toString()
        requestPermissions()
        binding?.apply {
            if (SharedPreference.get(requireActivity()).language != "") {
                Constants.language = SharedPreference.get(requireActivity()).language
            } else {
                Constants.language = "ar"
            }

            if (Constants.language == "ar") {
                root.layoutDirection= View.LAYOUT_DIRECTION_RTL
                cbArabic.text = "العربية"
                cbEnglish.text = "الإنجليزية"
                tvInfo.text = "اختر اللغة واستمر للحصول على تجربة التطبيق بلغتك المفضلة."
                tvSelectLanguage.text = "اختر اللغة"
                btnContinue.text = "متابعة"
                cbEnglish.isChecked = false
                cbArabic.isChecked = true
            } else {
                cbArabic.text = "Arabic"
                cbEnglish.text = "English"
                tvInfo.text =
                    "Choose the language and continue to get the experience the app in your preferred language."
                tvSelectLanguage.text = "Select language"
                btnContinue.text = "Continue"
                root.layoutDirection= View.LAYOUT_DIRECTION_LTR
                cbEnglish.isChecked = true
                cbArabic.isChecked = false
            }

            if (from == "profile") {
                btnContinue.text = getString(R.string.save)
                tvToolbar.isVisible = true
                tvSelectLanguage.isVisible = false
                tvInfo.isVisible = false
            } else {
                btnContinue.text = getString(R.string.continue_text)
                tvToolbar.isVisible = false
                tvSelectLanguage.isVisible = true
                tvInfo.isVisible = true
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            cbArabic.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbEnglish.isChecked = false
                    Constants.language = Constants.ARABIC
                    root.layoutDirection= View.LAYOUT_DIRECTION_RTL

                    cbArabic.text = "العربية"
                    cbEnglish.text = "الإنجليزية"
                    tvInfo.text = "اختر اللغة واستمر للحصول على تجربة التطبيق بلغتك المفضلة."
                    tvSelectLanguage.text = "اختر اللغة"
                    btnContinue.text = "متابعة"

                } else if (!cbEnglish.isChecked) {
                    cbArabic.isChecked = true
                }
            }

            cbEnglish.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbArabic.isChecked = false
                    Constants.language = Constants.ENGLISH
                    root.layoutDirection= View.LAYOUT_DIRECTION_LTR

                    cbArabic.text = "Arabic"
                    cbEnglish.text = "English"
                    tvInfo.text =
                        "Choose the language and continue to get the experience the app in your preferred language."
                    tvSelectLanguage.text = "Select language"
                    btnContinue.text = "Continue"

                } else if (!cbArabic.isChecked) {
                    cbEnglish.isChecked = true
                }
                //,,,
            }
            if (from != "profile") {
                cbArabic.isChecked = true
                Constants.language = Constants.ARABIC
            }

            btnContinue.setOnClickListener {
                if (Constants.language == "") {
                    showToast(getString(R.string.please_select_language))
                } else {
                    SharedPreference.get(requireActivity()).language = Constants.language
                    CommonMethod.setLocale(requireActivity(), Constants.language)

                    val intent = requireActivity().intent
                    intent.putExtra("open_screen", from)
                    requireActivity().finish()
                    startActivity(intent)

                    /*if (from=="profile"){
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.language_changed_successfully)){
                            findNavController().popBackStack()
                        }
                    }
                    else{
                        if (SharedPreference.get(requireActivity()).isTutorial){
                            findNavController().navigate(R.id.action_languageFragment_to_loginFragment)
                        }
                        else{
                            findNavController().navigate(R.id.action_languageFragment_to_onBoardingFragment)
                        }
                    }*/
                }
            }
        }
    }

    override fun observe() {
    }

    private val requestAllPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
            } else {
                true
            }
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (notificationGranted && (fineLocationGranted || coarseLocationGranted)) {

            } else {

            }
        }

    private fun requestPermissions() {
        val permissionsList = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        requestAllPermissions.launch(permissionsList.toTypedArray())
    }

}