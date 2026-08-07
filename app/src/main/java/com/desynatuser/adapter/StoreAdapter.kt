package com.desynatuser.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemStoreBinding
import com.desynatuser.ui.home.profile.response.GetCategoryResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse

class StoreAdapter(var context:Context, private var storeList: ArrayList<GetStoreResponse.Data>, var storeClick:StoreClick) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemStoreBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface StoreClick{
        fun onStoreClick(position: Int,type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStoreBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var storeData = storeList[position]

        holder.binding.apply {
         //   tvName.text =  storeData.sellerInformation.firstName+" "+storeData.sellerInformation.lastName
            tvName.text =  storeData.storeName
           // tvRating.text = storeData
            if (storeData?.averageStoreRating!=null){
                tvRating.text = "(${storeData?.averageStoreRating})"
            }
            else{
                tvRating.text = "(0)"
            }
            rating.rating = storeData?.averageStoreRating?.toFloatOrNull()?:0f
            ivProfile.load(storeData.sellerInformation.profilePic) {
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }

            ivStore.load(storeData.storeImage) {
                crossfade(true)
                placeholder(R.color.black)
                error(R.color.black)
            }

            if (storeData.storeAvailability==1){
                tvStatus.isVisible = false
                tvStatus.text = context.getString(R.string.available)
            }
            else{
                tvStatus.text = context.getString(R.string.busy)
                tvStatus.isVisible = true

            }

            if (storeData.sellerStories.size!=0){
                ivStoryHighlight.isVisible = true
                if (!storeData?.sellerStories.isNullOrEmpty()) {

                    ivStoryHighlight?.isVisible = true

                    val allStoriesSeen = storeData?.sellerStories?.all { it.isSeen == 1 } == true

                    ivStoryHighlight?.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            if (allStoriesSeen) {
                                R.color.light_brown   // All stories read
                            } else {
                                R.color.brown         // At least one unread story
                            }
                        )
                    )

                } else {
                    ivStoryHighlight?.isInvisible = true
                }
            }
            else{
                ivStoryHighlight.isInvisible = true
            }

            ivProfile.setOnClickListener {
                if (storeList.isNotEmpty()) {
                    storeClick.onStoreClick(position, 2)
                }
            }

        }

        holder.itemView.setOnClickListener {
            storeClick.onStoreClick(position,1)
        }
    }

}
