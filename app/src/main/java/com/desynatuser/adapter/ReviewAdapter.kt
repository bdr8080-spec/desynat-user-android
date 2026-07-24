package com.desynatuser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemRatingBinding
import com.desynatuser.ui.home.store.response.GetProductDetailResponse
import com.desynatuser.utils.CommonMethod

class ReviewAdapter(var context:Context, var reviewList: ArrayList<GetProductDetailResponse.Data.Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRatingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRatingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var reviewData = reviewList[position]
        holder.binding.apply {
            ivUser.load(reviewData?.userDetails?.profileImage) {
                crossfade(true)
                placeholder(R.drawable.ic_black_user)
                error(R.drawable.ic_black_user)
            }
            tvDate.text = CommonMethod.customizeChangeDateFormat(reviewData?.createdAt.toString(), outputFormat = "dd MMM yyyy")
            ratingBar.rating = reviewData?.productRating?:0f
            tvName.text = reviewData?.userDetails?.fullName
            tvReviews.text = reviewData?.productReview
            if (!reviewData?.productImage.isNullOrEmpty()) {
                ivProductRating.isVisible = true
                ivProductRating.load(reviewData?.productImage?.get(0)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
            }
            else{
                ivProductRating.isVisible = false
            }

        }
    }


}
