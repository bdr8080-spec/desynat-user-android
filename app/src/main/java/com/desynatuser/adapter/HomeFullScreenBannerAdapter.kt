package com.desynatuser.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.desynatuser.R
import com.desynatuser.databinding.ItemFullScreenImageBinding

class HomeFullScreenBannerAdapter(var context:Context, var productList: ArrayList<String>, var click:OnClick, var onImageLoaded: () -> Unit) : RecyclerView.Adapter<HomeFullScreenBannerAdapter.ViewHolder>() {

    interface OnClick{
        fun onClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemFullScreenImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFullScreenImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productData = productList[position]
        holder.binding.apply {
//
            if (productData.endsWith(".gif", true)) {
                Glide.with(context).asGif()
                    .load(productData)
                    .listener(object : RequestListener<GifDrawable> {
                        override fun onResourceReady(
                            resource: GifDrawable,
                            model: Any,
                            target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource.setLoopCount(1)

                            resource.registerAnimationCallback(
                                object : Animatable2Compat.AnimationCallback() {
                                    override fun onAnimationEnd(drawable: Drawable?) {
                                        onImageLoaded()
                                    }
                                }
                            )

                            resource.start()
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<GifDrawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            onImageLoaded()
                            return false
                        }
                    })
                    .placeholder(R.color.white)
                    .into(holder.binding.ivProduct)


            } else {
                Glide.with(context)
                    .load(productData)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            onImageLoaded()
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            onImageLoaded()
                            return false
                        }
                    })
                    .placeholder(R.color.white)
                    .into(holder.binding.ivProduct)

            }

            ivProduct.setOnClickListener {
                click.onClick(position)
            }

        }
    }
}
