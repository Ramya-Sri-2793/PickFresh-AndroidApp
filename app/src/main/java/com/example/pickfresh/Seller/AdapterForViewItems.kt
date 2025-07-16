package com.example.pickfresh.Seller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pickfresh.Model.Items
import com.example.pickfresh.databinding.ViewitemsBinding

class AdapterForViewItems(val context: Context, val  data: ArrayList<Items>, val string:String) : RecyclerView.Adapter<AdapterForViewItems.Holder>() {
    class Holder(val item:ViewitemsBinding):RecyclerView.ViewHolder(item.root)

    override fun onCreateViewHolder(parent:ViewGroup, viewType: Int)
    =Holder(ViewitemsBinding.inflate(LayoutInflater.from(context),parent,false))
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
val k=data[position]
        with(holder.item){
    shapable.load(Uri.parse(k.itemphoto))

            if("not change"==string){
                details2.text= HtmlCompat.fromHtml("<b>Name : </b>${k.itemname}<br></br><b>Price : </b>₹${k.price}/-<br></br><b>Status : </b>${k.status}<br></br>"
                ,HtmlCompat.FROM_HTML_MODE_LEGACY)
                k.itemname="Name : ${k.itemname}"
                k.price="Price :₹${k.price}/-"
                k.status="Status :${k.status}"
            }else{
                details2.text="${k.itemname}\n${k.price}\n${k.status}"
                }
        }
        holder.itemView.setOnClickListener {
            Intent(context,OrderupdateActivity::class.java).apply {
                putExtra("data",k)
                context.startActivity(this)
            }
        }
    }

    override fun getItemCount()=data.size

}
