package com.desynatuser.ui.home.card

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.CardAdapter
import com.desynatuser.adapter.CurrencyAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentChangeCurrencyBinding
import com.desynatuser.databinding.FragmentMySavedCardBinding
import com.desynatuser.ui.home.card.response.GetSavedCardResponse
import com.desynatuser.utils.CommonMethod
import com.desynatuser.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MySavedCardFragment : BaseFragment() {
    private var binding: FragmentMySavedCardBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var cardAdapter: CardAdapter?=null
    private var cardList= ArrayList<GetSavedCardResponse.Data>()
    var selectedPos = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySavedCardBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "My Saved Cards"

            cardAdapter = CardAdapter(requireActivity(),cardList,object :
                CardAdapter.ClickOnItem{
                override fun onClickItem(position: Int,type: Int) {

                    when(type){
                        1->{
                            selectedPos = position
                            deleteCard()
                        }
                        2->{

                        }
                    }
                }

            })
            rvCard.adapter = cardAdapter
        }

        getCard()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvAddNewCard.setOnClickListener {
                findNavController().navigate(R.id.action_savedCardFragment_to_addCardFragment)
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.getSavedCardResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                cardList.clear()
                cardList.addAll(response.data)
                cardAdapter?.notifyDataSetChanged()
                checkData()
                isObserver = false
            }
        }

        viewModel.deleteCardResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),"Card deleted successfully!"){
                    cardList.removeAt(selectedPos)
                    cardAdapter?.notifyItemRemoved(selectedPos)
                    cardAdapter?.notifyItemRangeChanged(selectedPos, cardList.size - selectedPos)
                    checkData()
                }
                isObserver = false
            }
        }
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = cardList.isEmpty()
    }

    private fun getCard(){
        viewModel.getSavedCard()
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    private fun deleteCard(){
        val map = HashMap<String, Any>()
        map["card_id"] = cardList[selectedPos].id
        viewModel.deleteCard(map)
        isObserver = true
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

}