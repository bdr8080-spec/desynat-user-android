package com.desynatuser.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatuser.R
import com.desynatuser.databinding.ItemSuggestedStoreBinding
import com.desynatuser.ui.home.fragment.response.GetAllSuggestStoreResponse

class SuggestedStoreAdapter(private val context: Context, private val suggestedStoreList: List<GetAllSuggestStoreResponse.Data>, var onClick: OnClick) :
    RecyclerView.Adapter<SuggestedStoreAdapter.ViewHolder>() {
    interface OnClick{
        fun onItemClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemSuggestedStoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val suggestedStoreData = suggestedStoreList[position]
            Glide.with(context).load(suggestedStoreData?.storeImage).placeholder(R.drawable.ic_placeholder).into(ivStore)
            tvName.text = suggestedStoreData?.storeName

            root.setOnClickListener {
                onClick.onItemClick(position)
            }
        }


    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(
            ItemSuggestedStoreBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int = suggestedStoreList.size
}
