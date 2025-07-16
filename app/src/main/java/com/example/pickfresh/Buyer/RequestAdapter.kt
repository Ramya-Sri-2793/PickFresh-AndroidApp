package com.example.pickfresh.Buyer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pickfresh.Model.Items
import com.example.pickfresh.Model.Seconddata
import com.example.pickfresh.Model.Seconditem
import com.example.pickfresh.databinding.ItemsBinding

class RequestAdapter(
    val context: Context, val data: ArrayList<Items>,
    val user: ArrayList<Seconddata>,
    val size: click,
    var trans: String, val realdata: ArrayList<Seconditem>
) : RecyclerView.Adapter<RequestAdapter.Viewed>() {
    class Viewed(val item: ItemsBinding) : RecyclerView.ViewHolder(item.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Viewed(
        ItemsBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
    )

    override fun onBindViewHolder(holder: Viewed, position: Int) {
        val real = realdata[position]
        val dd = data[position]


        with(holder.item) {
            if (real.status.lowercase() == "all completed") {
                holder.item.cardmine.isVisible = false
            }


            shapable2.load(dd.itemphoto) {
                size(50)
            }
            if ("trans" != trans) {
                val text = "<b>Item Name : </b>${dd.itemname}<br></br>" +
                        "<b>Price : </b>${dd.price}<br></br>" +
                        "<b>Status : </b>${dd.status}<br></br>"
                details4.text = HtmlCompat.fromHtml(text, FROM_HTML_MODE_LEGACY)
            } else {
                val tex = "${dd.itemname}\n" +
                        "${dd.price}\n" +
                        "${dd.status}\n"
                details4.text = tex
            }
            var num = 0
            val firsttime = Seconddata(
                itemid = real.id,
                itemname = real.itemname,
                itemphoto = real.itemphoto,
                price = real.price,
                quatity = num,
            )
            first.setOnClickListener {
                num = 1

                if (!user.contains(firsttime)) {
                    user.add(firsttime)
                    first.isVisible = false
                    second.isVisible = true
                    qty.text = "$num"
                } else {
                    user.remove(firsttime)
                }
                size.item(user.size)
            }

            minus.setOnClickListener {
                if (num != 0) {
                    num--
                    user.forEach {
                        if (it.itemid == real.id) {
                            it.quatity = num
                        }
                    }
                    qty.text = "$num"
                    if (num == 0) {
                        first.isVisible = true
                        second.isVisible = false
                        user.remove(firsttime)
                    }

                }
                size.item(user.size)
            }
            plus.setOnClickListener {
                if (num != 0) {
                    num++
                    user.forEach {
                        if (it.itemid == real.id) {
                            it.quatity = num
                        }
                    }
                    qty.text = "$num"

                }
                size.item(user.size)
            }

        }

    }


    override fun getItemCount() = data.size
}