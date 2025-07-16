package com.example.pickfresh.Admin

import android.content.Context
import android.net.Uri
import android.text.Html
import android.text.Layout
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pickfresh.Model.User
import com.example.pickfresh.databinding.ActivitySellerMainBinding

class AdapterForSellerList(val context: Context,val data: ArrayList<User>):RecyclerView.Adapter<AdapterForSellerList.Viewed>() {
    class Viewed(val item :ActivitySellerMainBinding):RecyclerView.ViewHolder(item.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        Viewed(ActivitySellerMainBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun onBindViewHolder(holder: Viewed, position: Int) {
val k=data[position]
        with(holder.item){
    val string="<b>Name : </b>${k.name}<br></br>" +
            "<b>Joined in:</b>${k.date}"
    details.text=HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_LEGACY)
            shapeimage.load(Uri.parse(k.image))
}
    }

    override fun getItemCount()=data.size

}
