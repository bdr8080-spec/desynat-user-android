package com.desynatuser.ui.home.storyHighlights

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatuser.R
import com.desynatuser.adapter.EventAdapter
import com.desynatuser.base.BaseFragment
import com.desynatuser.data.view_model.HomeViewModel
import com.desynatuser.databinding.FragmentEventBinding
import com.desynatuser.ui.home.storyHighlights.response.GetLiveStreamingRoomResponse
import com.desynatuser.utils.ProgressDialogUtils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class EventFragment : BaseFragment() {
    private var binding: FragmentEventBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var eventAdapter : EventAdapter?=null
    private var eventList = ArrayList<GetLiveStreamingRoomResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(layoutInflater,container,false)
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

            eventList.clear()
            eventAdapter = EventAdapter(requireActivity(),eventList,object : EventAdapter.OnCLick{
                override fun onClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("eventData", Gson().toJson(eventList[position]))
                    }
                    findNavController().navigate(R.id.action_eventFragment_to_liveEventFragment,bundle)
                }

            })
            rvEvents.adapter = eventAdapter

        }

        getLiveStreaming()

        Handler(Looper.getMainLooper()).postDelayed({
            checkData()
        },500)
    }

    override fun initControl() {
        binding?.apply {
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }


        viewModel.getLiveStreamingRoomResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                eventList.clear()
                eventList.addAll(response.data)
                eventAdapter?.notifyDataSetChanged()
                checkData()

                isObserver = false
            }
        }
    }


    private fun getLiveStreaming(){
        viewModel.getLiveStreamingRoom()
        ProgressDialogUtils.getInstance().showProgress(requireActivity(),false)
        isObserver = true
    }

    private fun  checkData(){
        binding?.tvNoDataFound?.isVisible = eventList.isEmpty()
    }
}