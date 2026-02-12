package com.desynatuser.ui.affiliateUser.home.product

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.AffiliateProductAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.AuthViewModel
import com.desynatuser.databinding.FragmentAffiliateAllProductBinding
import com.desynatuser.databinding.GetProductLinkDialogBinding
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AffiliateAllProductFragment : BaseFragment() {
    private var binding: FragmentAffiliateAllProductBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    private var isObserver = false
    private var affiliateProductAdapter: AffiliateProductAdapter?=null
    private var productList = ArrayList<GetSingleStoreResponse.Data.AllProduct.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateAllProductBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "All Products"

            affiliateProductAdapter = AffiliateProductAdapter(requireActivity(),productList,object :
                AffiliateProductAdapter.ProductClick{
                override fun onProductClick(position: Int, type: Int) {
                    getLinkDialog()
                }

            })
            rvProduct.adapter = affiliateProductAdapter
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }

    }

    override fun observe() {
    }

    private fun getLinkDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = GetProductLinkDialogBinding.inflate(LayoutInflater.from(requireActivity()))
        dialogBinding.root.let { dialog?.setContentView(it) }

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

        dialogBinding.apply {

            tvToolbar.setOnClickListener {
                dialog?.dismiss()
            }

            tvCopyLink.setOnClickListener {

            }

            btnAdd.setOnClickListener {

            }
        }
        dialog?.show()
    }

}