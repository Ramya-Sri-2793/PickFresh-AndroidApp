package com.example.pickfresh.Seller

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pickfresh.Buyer.BuyerMainActivity
import com.example.pickfresh.Buyer.ItemsAdapter
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.Model.OrderLiveData
import com.example.pickfresh.Model.Orders
import com.example.pickfresh.R
import com.example.pickfresh.Responses.CommonReponse
import com.example.pickfresh.Responses.OrderResponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.databinding.ActivityViewBuyerItemsBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewBuyerItems : AppCompatActivity() {
    lateinit var dialog:Dialog
    private lateinit var onewordchange: Onewordchange
    val string=ArrayList<String>()
    private lateinit var orderLiveData: OrderLiveData
    private lateinit var bind:ActivityViewBuyerItemsBinding
    private lateinit var data2:ArrayList<Orders>
    var title="Do you want to update this order ?"
    var message="Press 'yes' to Accept the order or  Press 'cancel' to cancel the request"
    var yes="Yes"
    var no="Cancel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivityViewBuyerItemsBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.cycle3.layoutManager=LinearLayoutManager(this)
        data2= ArrayList()
        orderLiveData=ViewModelProvider(this)[OrderLiveData::class.java]
        onewordchange=ViewModelProvider(this)[Onewordchange::class.java]
        dialog=Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        show()
        }
bind.appCompatButton.setOnClickListener {
    if(data2.isNotEmpty()){
        AlertDialog.Builder(this).apply {
setTitle(title)
            setCancelable(false)
            setMessage(message)
            setPositiveButton(yes){
                dialog,_->
                updatetion("accepted")
                dialog.dismiss()
            }
            setNegativeButton(no){
                dialog,_->
                updatetion("cancelled")
                dialog.dismiss()
            }

            show()
        }
    }
}

        CoroutineScope(IO).launch {
    Retrofit.instance.getbuyerdetails(
        id = "${intent.getStringExtra("id")}",
        condition = "viewbuyerorders"
    ).enqueue(object :Callback<OrderResponse>{
        @SuppressLint("SetTextI18n")
        override fun onResponse(
            call: Call<OrderResponse>,
            response: Response<OrderResponse>
        ) {
            dialog.dismiss()
            response.body().apply {
                if(this!=null){
                var num=0
                    data2=data
                    data.forEach {
                    num+=it.qty.toInt()+it.price.toInt()
                }
                    bind.total.text="Total Cost ₹$num/-"
                    string.add(bind.appCompatButton.text.toString())
                    string.add(bind.total.text.toString())
                    string.add(title)
                    string.add(message)
                    string.add(yes)
                    string.add(no)
                    data2=data
                    val type=getSharedPreferences("user", MODE_PRIVATE).getString("language","")
if(type=="English"){
bind.cycle3.adapter=ItemsAdapter(this@ViewBuyerItems,data,"nontans")
}else {
    data()
}           }
            }
        }

        override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
       dialog.dismiss()
            Toast.makeText(this@ViewBuyerItems, "${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}
    }

    private fun updatetion(text: String) {
        dialog.show()
CoroutineScope(IO).launch {
    Retrofit.instance.updaterequest(
        condition = "updateorder",
        id = "${intent.getStringExtra("id")}",
        state=text
    ).enqueue(object :Callback<CommonReponse>{
        override fun onResponse(call: Call<CommonReponse>, response: Response<CommonReponse>) {
            dialog.dismiss()
            response.body().apply {
                if(this!=null){
                    if(message=="success"){
                        finishAffinity()
                        startActivity(Intent(this@ViewBuyerItems,SellerMainActivity::class.java))
                    }else{
                        Toast.makeText(this@ViewBuyerItems, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        override fun onFailure(call: Call<CommonReponse>, t: Throwable) {
        dialog.dismiss()
            Toast.makeText(this@ViewBuyerItems, "${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}
    }



    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
    data()
        val type=getSharedPreferences("user", MODE_PRIVATE).getString("language","")
    var num=0
        if(type!="English") {
            orderLiveData.viewdata().observe(this) {
                if (it.size == data2.size) {
                   bind.cycle3.adapter=ItemsAdapter(this,it,"trans")
                }
            }
            onewordchange.observer().observe(this){
                if(it.size==string.size){
                    bind.appCompatButton.text=it[0]
                    bind.total.text=it[1]
                    title=it[2]
                    message=it[3]
                    yes=it[4]
                    no=it[5]
                }
            }
        }

    }

    private fun data() {
        val type=getSharedPreferences("user", MODE_PRIVATE).getString("language","")
        if(type=="Tamil"){
            translate(TranslateLanguage.TAMIL)
        }else if(type=="Kannada"){
            translate(TranslateLanguage.KANNADA)
        }else if(type=="Telugu"){
            translate(TranslateLanguage.TELUGU)
        }else if(type=="Hindi"){
            translate(TranslateLanguage.HINDI)
        }
    }


    private fun translate(language: String) {
dialog.show()
        val option= TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(language)
            .build()
        val download= DownloadConditions.Builder().build()
        Translation.getClient(option)
            .downloadModelIfNeeded(download)
            .addOnSuccessListener {
                onewordchange.option(option,string)
                orderLiveData.getdata(option,data2)
                dialog.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

    }
}