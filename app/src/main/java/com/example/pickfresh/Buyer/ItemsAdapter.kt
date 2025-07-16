package com.example.pickfresh.Buyer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pickfresh.Model.Orders
import com.example.pickfresh.databinding.ActivitySellerMainBinding

class ItemsAdapter(val context: Context, val data: ArrayList<Orders>, val trans: String) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder2>() {
    class ViewHolder2(val item: ActivitySellerMainBinding) : RecyclerView.ViewHolder(item.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder2(
        ActivitySellerMainBinding.inflate(LayoutInflater.from(context), parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        val values = data[position]
        with(holder.item) {
            if (trans == "nontans") {
                val text = "<b>Item name :</b> ${values.itemname}<br></br>" +
                        "<b>Price :</b> ₹${values.price}/-<br></br>" +
                        "<b>Quantity :</b> ${values.qty}<br></br>" +
                        "<b>Ordered date:</b> ${values.date}<br></br>" +
                        "<b>Status :</b> ${values.status}"
                details.text = HtmlCompat.fromHtml(text, FROM_HTML_MODE_LEGACY)
                details.width = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                details.text =
                    "${values.itemname}\n${values.price}\n${values.qty}\n${values.date}\n${values.status}"
                details.width = ViewGroup.LayoutParams.WRAP_CONTENT
            }

            shapeimage.load(values.itemphoto) {
                size(20)
            }
        }

    }

    override fun getItemCount() = data.size
}