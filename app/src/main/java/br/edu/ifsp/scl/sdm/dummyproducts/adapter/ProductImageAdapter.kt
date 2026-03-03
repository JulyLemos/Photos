package br.edu.ifsp.scl.sdm.dummyproducts.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.sdm.dummyproducts.databinding.TileProductImageBinding

class ProductImageAdapter(val activityContext: Context, val productImageList: MutableList<Bitmap>):
RecyclerView.Adapter<ProductImageAdapter.ProductImageViewHolder>() {
    inner class ProductImageViewHolder(tpib: TileProductImageBinding): RecyclerView.ViewHolder(tpib.productIv) {
        val productIv: ImageView = tpib.productIv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder =
        ProductImageViewHolder(TileProductImageBinding.inflate(LayoutInflater.from(activityContext), parent, false))

    override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) =
        holder.productIv.setImageBitmap(productImageList[position])

    override fun getItemCount(): Int = productImageList.size
}