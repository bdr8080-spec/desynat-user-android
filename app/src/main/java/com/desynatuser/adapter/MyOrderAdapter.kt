package com.desynatuser.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatuser.R
import com.desynatuser.databinding.ItemOrderBinding
import com.desynatuser.ui.home.myOrder.response.GetAllOrderResponse
import com.desynatuser.utils.CommonMethod

class MyOrderAdapter(var context:Context, private var orderList: ArrayList<GetAllOrderResponse.Data>, var orderClick:OrderClick) : RecyclerView.Adapter<MyOrderAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OrderClick{
        fun onOrderClick(position: Int,type:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var orderData = orderList[position]

        holder.binding.apply {

            tvOrderId.text = "Order ID: ${orderData.orderId}"
            tvTitle.text = orderData?.orderDetails?.get(0)?.productDetails?.productNameEn

            tvPrice.text = "${CommonMethod.formatPrice(orderData?.totalAmount?:0.0)} KD"

            ivProduct.load(orderData?.orderDetails?.get(0)?.variationDetails?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

        //    tvPlacedDate.text = CommonMethod.customizeChangeDateFormat(orderData?.createdAt.toString(), outputFormat = "hh a  | dd MM yy")
            tvPlacedDate.text = CommonMethod.customizeChangeDateFormat(orderData?.createdAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mma")
            tvSizeValue.text = orderData?.orderDetails?.get(0)?.variationDetails?.size?.name.toString()
            orderData?.orderDetails?.getOrNull(0)
                ?.variationDetails?.color
                ?.hexCode?.let { ivColor.setColorFilter(Color.parseColor(it)) }
            tvQuantity.text = orderData?.orderDetails?.get(0)?.quantity.toString()

            when(orderData?.orderDetails?.get(0)?.status){//1 all 2 ongoing 3 delivered 4 cancelled 5 return
                "1" -> {
                    btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                    btn.setTextColor(Color.parseColor("#17854C"))
                    btn.text = "Expected Delivery Date : 12 SEP 25"

                    tvStatus.text = "New"
                    btn.setTextColor(Color.parseColor("#FF5F1B"))
                }
                "2" -> {
                    btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                    btn.setTextColor(Color.parseColor("#17854C"))
                    btn.text = "Expected Delivery Date : 12 SEP 25"

                    tvStatus.text = "Ongoing"
                    btn.setTextColor(Color.parseColor("#FF5F1B"))
                }
                "3" -> {
                    btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                    btn.setTextColor(Color.parseColor("#16B364"))
                    btn.text = "Delivered On : 12 SEP 25"

                    tvStatus.text = "Delivered"
                    btn.setTextColor(Color.parseColor("#16B364"))
                }

                "4" -> {
                    btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F4DDDD"))
                    btn.setTextColor(Color.parseColor("#E70000"))
                    btn.text = "Cancelled  Date : 12 SEP 25"

                    tvStatus.text = "Cancelled"
                    btn.setTextColor(Color.parseColor("#E70000"))
                }
                "5" -> {
                    btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#26FF5F1B"))
                    btn.setTextColor(Color.parseColor("#FF5F1B"))
                    btn.text = "Pickup Date : 12 SEP 25"

                    tvStatus.text = "Return Req.."
                    btn.setTextColor(Color.parseColor("#FF5F1B"))
                }
            }

        }

    }

}
