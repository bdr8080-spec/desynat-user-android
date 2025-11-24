package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatuser.databinding.ItemSpecificationBinding
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse

class SpecificationAdapter(var context:Context, var specificationList: ArrayList<GetSingleStoreResponse.Data.AllProduct.Data.ProductSpecification>) : RecyclerView.Adapter<SpecificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSpecificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSpecificationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return specificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvDescription.text = "${specificationList[position].nameEn} - ${specificationList[position].featureEn}"

        }
    }


}
