package com.example.pickfresh.Buyer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pickfresh.Model.Orderid
import com.example.pickfresh.Seller.ViewAccepted
import com.example.pickfresh.Seller.ViewBuyerItems
import com.example.pickfresh.databinding.IdviewBinding

class AdapterForPendings(val context:Context,val  data: ArrayList<Orderid>,var string:String) : RecyclerView.Adapter<AdapterForPendings.Holder>() {
    class Holder(val item :IdviewBinding):RecyclerView.ViewHolder(item.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= Holder(IdviewBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
val id=data[position]
        with(holder.item){
    details5.text=id.orderid
}
        holder.itemView.setOnClickListener {
            if(string=="seller") {
                Intent(context, ViewBuyerItems::class.java).apply {
                    putExtra("id",id.orderid)
                    context.startActivity(this)
                }
            } else if(string=="buyer"){
                Intent(context, ViewOrderitems::class.java).apply {
                    putExtra("id", id.orderid)
                    context.startActivity(this)
                }
            }else if(string=="completed"){
                Intent(context, CompletedActivity::class.java).apply {
                    putExtra("id", id.orderid)
                    context.startActivity(this)
                }
            }
            else if(string=="accepted"){
Intent(context, ViewAccepted::class.java).apply {
    putExtra("id",id.orderid)
    context.startActivity(this)
}
            }

        }
    }

    override fun getItemCount()=data.size

}
