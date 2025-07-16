package com.example.pickfresh.Buyer

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.Model.OrderLiveData
import com.example.pickfresh.Model.Orders
import com.example.pickfresh.R
import com.example.pickfresh.Responses.CommonReponse
import com.example.pickfresh.Responses.LoginResponse
import com.example.pickfresh.Responses.OrderResponse
import com.example.pickfresh.Responses.Retrofit
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

class ViewOrderitems : AppCompatActivity() {
    lateinit var dialog: Dialog
    lateinit var item: RecyclerView
    lateinit var data3: ArrayList<Orders>
    lateinit var orderLiveData: OrderLiveData
    lateinit var onewordchange: Onewordchange
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_orderitems)
        orderLiveData = ViewModelProvider(this)[OrderLiveData::class.java]
        onewordchange = ViewModelProvider(this)[Onewordchange::class.java]
        data3 = ArrayList()
        textView = findViewById(R.id.totalamount)
        dialog = Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val btn=findViewById<FloatingActionButton>(R.id.pay)

        val id = intent.getStringExtra("id")
        val launvh=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
dialog.show()
            Retrofit.instance.updateorder(id=id!!).enqueue(object :Callback<CommonReponse>{
                override fun onResponse(
                    call: Call<CommonReponse>,
                    response: Response<CommonReponse>
                ) {
                    dialog.dismiss()

                    response.body()?.apply {
                        if(message=="success"){
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<CommonReponse>, t: Throwable) {
                   dialog.dismiss()
                }
            })
        }
        btn.setOnClickListener{

            if(id!=null){
                dialog.show()
Retrofit.instance.getuser(id=id).enqueue(object :Callback<LoginResponse>{
    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
        dialog.dismiss()
    response.body()?.apply {
        if(data.isNotEmpty()){
            var num=""
            textView.text.forEach {
                if(it.isDigit()){
                    num+=it
                }
            }
            try {
                val k=data[0]
                val upi=
                    "upi://pay?pa=${k.mobile}@ybl&pn=${k.name}&mc= &tid= &tr= &tn=DummyPayment&am=$num&cu=INR&refUrl="
                val dk=upi.replace(" ","+")
                launvh.launch(Intent(Intent.ACTION_VIEW , Uri.parse(dk)))
            }catch (e:Exception){
                Toast.makeText(this@ViewOrderitems, "Sorry No Upi", Toast.LENGTH_SHORT).show()
            }

        }
    }
    }

    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
dialog.dismiss()
        Toast.makeText(this@ViewOrderitems, "${t.message}", Toast.LENGTH_SHORT).show()
    }
})
            }else{
                Toast.makeText(this, "Sorry Something went Wrong", Toast.LENGTH_SHORT).show()
            }
        }
        item = findViewById(R.id.items)
        item.layoutManager = LinearLayoutManager(this)
        dialog.show()
        CoroutineScope(IO).launch {
            Retrofit.instance.getorderitems(
                condition = "vieworders",
                id = "$id"
            ).enqueue(object : Callback<OrderResponse> {
                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    response.body().apply {
                        if (this != null) {
                            data3 = data

                            var num = 0
                            data3.forEach {
                                num += it.qty.toInt() * it.price.toInt()
                            }
                            val k = ArrayList<String>()
                            k.add("Total Amount ₹$num/-")
                            viewdata(k)
                        }
                    }
                    dialog.dismiss()
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    dialog.dismiss()
                    Toast.makeText(this@ViewOrderitems, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    private fun viewdata(text: ArrayList<String>) {
        when (getSharedPreferences("user", MODE_PRIVATE).getString("language", "")) {
            "Tamil" -> {
                translate(TranslateLanguage.TAMIL, text)
            }
            "Kannada" -> {
                translate(TranslateLanguage.KANNADA, text)
            }
            "Telugu" -> {
                translate(TranslateLanguage.TELUGU, text)
            }
            "Hindi" -> {
                translate(TranslateLanguage.HINDI, text)
            }
            "English" -> {
                textView.text = text[0]
                item.adapter = ItemsAdapter(this@ViewOrderitems, data3, "nontans")
            }
        }
    }

    private fun translate(langauge: String, text: ArrayList<String>) {
        dialog.dismiss()
        val option = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(langauge)
            .build()
        val download = DownloadConditions.Builder().build()
        Translation.getClient(option)
            .downloadModelIfNeeded(download).addOnSuccessListener {
                orderLiveData.getdata(option, data3)

                onewordchange.option(option, text)
                onewordchange.observer().observe(this) {
                    if (it.size == text.size) {
                        textView.text = it[0]
                    }
                }
                orderLiveData.viewdata().observe(this) {
                    if (it.size == data3.size) {
                        item.adapter = ItemsAdapter(this@ViewOrderitems, it, "trans")
                    }
                }
                dialog.dismiss()

            }
            .addOnFailureListener {
                dialog.dismiss()
            }
    }

}