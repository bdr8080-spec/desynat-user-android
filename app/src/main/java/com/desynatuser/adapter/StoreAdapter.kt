package com.desynatuser.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemStoreBinding
import com.desynatuser.ui.home.response.GetCategoryResponse
import com.desynatuser.ui.home.store.response.GetStoreResponse

class StoreAdapter(var context:Context, private var storeList: ArrayList<GetStoreResponse.Data>, var storeClick:StoreClick) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemStoreBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface StoreClick{
        fun onStoreClick(position: Int)
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
                tvStatus.text = "Busy"
                tvStatus.isVisible = true
            }
            else{
                tvStatus.isVisible = false
                tvStatus.text = "Available"
            }

        }

        holder.itemView.setOnClickListener {
            storeClick.onStoreClick(position)

        }
    }

}
