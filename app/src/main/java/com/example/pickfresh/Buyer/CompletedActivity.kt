package com.example.pickfresh.Buyer

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.Model.OrderLiveData
import com.example.pickfresh.Model.Orders
import com.example.pickfresh.R
import com.example.pickfresh.Responses.CommonReponse
import com.example.pickfresh.Responses.CustomeResponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.databinding.ActivityCompletedBinding
import com.example.pickfresh.toast
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.android.gms.common.internal.service.Common
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

class CompletedActivity : AppCompatActivity() {
    private lateinit var bind:ActivityCompletedBinding
    private lateinit var liveData: OrderLiveData
    private lateinit var onewordchange: Onewordchange
    private lateinit var dialog: Dialog
    private lateinit var dataview:ArrayList<Orders>
    var string1="Please Give Rating"
    var string2="Please Enter Your Review"
    var string3="wait until data arrives"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivityCompletedBinding.inflate(layoutInflater)
        setContentView(bind.root)
        dataview= ArrayList()
        dialog=Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        bind.btncompleted.setOnClickListener {
 val text=bind.edittext.text.toString()
            val rating=bind.rating.rating
            if(rating==0.0f){
                it.toast(string1)
            }else if(text.isEmpty()){
                it.toast(string2)
            }else if(dataview.isEmpty()){
                it.toast(string3)
            }else {

                dialog.show()
                CoroutineScope(IO).launch {
                    Retrofit.instance.addreview(
                                rating=rating.toString(),
                                review=text,
                                buyerid	=dataview[0].sellerid,
                                productid=dataview[0].orderid,
                    ).enqueue(object :Callback<CommonReponse>{
                        override fun onResponse(
                            call: Call<CommonReponse>,
                            response: Response<CommonReponse>
                        ) {
                            response.body().apply {
                            if(this!=null) {
                                if (message == "success") {
                                    finishAffinity()
                                    startActivity(Intent(this@CompletedActivity,BuyerMainActivity::class.java))
                                }
                            }
                            }
                            dialog.dismiss()
                        }

                        override fun onFailure(call: Call<CommonReponse>, t: Throwable) {
                        dialog.dismiss()
                            Toast.makeText(this@CompletedActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }}

        liveData=ViewModelProvider(this)[OrderLiveData::class.java]
        onewordchange=ViewModelProvider(this)[Onewordchange::class.java]
        dialog.show()
        CoroutineScope(IO).launch {
            Retrofit.instance.getorderdetails(condition = "getbothreviewandorder",id="${intent.getStringExtra("id")}")
                .enqueue(object :Callback<CustomeResponse>{
                    override fun onResponse(
                        call: Call<CustomeResponse>,
                        response: Response<CustomeResponse>
                    ) {
                        response.body().apply {
                            if(this!=null){
                                dataview=data
                            if(data2.isNotEmpty()){
                                bind.linearLayout.isFocusable=false
                                bind.linearLayout.children.forEach {
                                    it.isClickable=false
                                    it.isFocusable=false
                                }
                                bind.rating.setIsIndicator(true)
                                bind.rating.clearFocus()
                                bind.rating.rating=data2[0].rating.toFloat()
                                bind.edittext.setText(data2[0].review)
                            }
                                viewdata()
                            }
                        }
                        dialog.dismiss()
                    }

                    override fun onFailure(call: Call<CustomeResponse>, t: Throwable) {
                    dialog.dismiss()
                        Toast.makeText(this@CompletedActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })
        }

    }

    @SuppressLint("SetTextI18n")
    private fun viewdata() {
        val type=getSharedPreferences("user", MODE_PRIVATE).getString("language","")
        if(type=="Tamil"){
            translate(TranslateLanguage.TAMIL)
        }else if(type=="Telugu"){
            translate(TranslateLanguage.TELUGU)
        }else if(type=="Kannada"){
            translate(TranslateLanguage.KANNADA)
        }else if(type=="Hindi"){
            translate(TranslateLanguage.HINDI)
        }else if(type=="English"){
            bind.cycle5.layoutManager=LinearLayoutManager(this)
            bind.cycle5.adapter=ItemsAdapter(this,dataview,"nontans")
            var num=0
            for (orders in dataview) {
                num+=orders.qty.toInt()*orders.price.toInt()
            }
            bind.totalview.text="Total Amount ₹$num/-"
        }

    }

    private fun translate(language: String) {
        var num=0
        for (orders in dataview) {
            num+=orders.qty.toInt()*orders.price.toInt()
        }
        val string=ArrayList<String>()
        string.add("Total Amount ₹$num/-")
        string.add(bind.edittext.hint.toString())
        string.add(bind.btncompleted.text.toString())
        string.add(string1)
                string.add(string2)
        string.add(string3)

val options=TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH)
    .setTargetLanguage(language)
    .build()
        val download=DownloadConditions.Builder()
            .build()
        Translation.getClient(options)
            .downloadModelIfNeeded(download)
            .addOnSuccessListener {
                liveData.getdata(options,dataview)
                onewordchange.option(options,string)
                onewordchange.observer().observe(this){
                    if(it.size==string.size){
                        bind.totalview.text=it[0]
                        bind.edittext.hint=it[1]
                        bind.btncompleted.text=it[2]
                        string1=it[3]
                        string2=it[4]
                        string3=it[5]
                    }
                }
                liveData.viewdata().observe(this){
                    if(it.size==dataview.size){
                        bind.cycle5.layoutManager=LinearLayoutManager(this)
                        bind.cycle5.adapter=ItemsAdapter(this,it,"trans")
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }


    }
}