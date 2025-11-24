package com.desynatuser.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemProductBinding
import com.desynatuser.databinding.ItemProductImageBinding
import com.desynatuser.ui.home.store.response.GetSingleStoreResponse
import com.desynatuser.utils.CommonMethod

class ProductImageAdapter(var context:Context, var productList: ArrayList<String>) : RecyclerView.Adapter<ProductImageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productData = productList[position]
        holder.binding.apply {
            ivProduct.load(productData) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

        }
    }


}
