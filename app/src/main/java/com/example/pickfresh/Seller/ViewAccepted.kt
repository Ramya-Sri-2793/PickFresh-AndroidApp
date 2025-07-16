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
import com.example.pickfresh.Buyer.ItemsAdapter
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.Model.OrderLiveData
import com.example.pickfresh.Model.Orders
import com.example.pickfresh.R
import com.example.pickfresh.Responses.CommonReponse
import com.example.pickfresh.Responses.OrderResponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.databinding.ActivityViewAcceptedBinding
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

class ViewAccepted : AppCompatActivity() {
    private lateinit var bind: ActivityViewAcceptedBinding
    val string = ArrayList<String>()
    lateinit var dialog: Dialog
    lateinit var liveData: OrderLiveData
    lateinit var data2: ArrayList<Orders>
    lateinit var onewordchange: Onewordchange
    var title = "Are you Completed the Request ?"
    var message = "Press 'yes' for Completion and no for 'cancel' !!"
    var yes = "yes"
    var no = "Cancel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityViewAcceptedBinding.inflate(layoutInflater)
        setContentView(bind.root)
        data2 = ArrayList()
        bind.recyclerView2.layoutManager = LinearLayoutManager(this)
        liveData = ViewModelProvider(this)[OrderLiveData::class.java]
        onewordchange = ViewModelProvider(this)[Onewordchange::class.java]
        dialog = Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        bind.viewuser.setOnClickListener {
            if (data2.isNotEmpty()) {
                Intent(this, Userdeatils::class.java).apply {
                    putExtra("userid", data2[0].userid)
                    startActivity(this)
                }
            }

        }
        bind.appCompatButton2.setOnClickListener {
            if (data2.isNotEmpty()) {
                AlertDialog.Builder(this).apply {
                    setTitle(title)
                    setMessage(message)
                    setPositiveButton(yes) { dialog, _ ->
                        dialog.dismiss()
                        updated(data2[0].orderid, state = "Completed")
                    }
                    setNegativeButton(no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }

            }
        }
        dialog.show()

        CoroutineScope(IO).launch {
            Retrofit.instance.getbuyerdetails(
                id = intent.getStringExtra("id")!!,
                condition = "vieworders"
            ).enqueue(object : Callback<OrderResponse> {
                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    dialog.dismiss()
                    Toast.makeText(this@ViewAccepted, "${t.message}", Toast.LENGTH_SHORT).show()
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {
                    response.body().apply {
                        if (this != null) {
                            data2 = data
                            var num = 0
                            data2.forEach {
                                num += it.price.toInt() * it.qty.toInt()
                            }
                            bind.textView.text = "Total Amount : ₹$num/-"
                            string.add(bind.appCompatButton2.text.toString())
                            string.add("Total Amount : ₹$num/-")
                            string.add(bind.viewuser.text.toString())
                            string.add(title)
                            string.add(message)
                            string.add(yes)
                            string.add(no)
                            data()
                        }
                    }

                    dialog.dismiss()
                }
            })
        }
    }

    private fun updated(orderid: String, state: String) {
        dialog.show()
        CoroutineScope(IO).launch {
            Retrofit.instance.updatefun(condition = "updateorder", id = orderid, state = state)
                .enqueue(object : Callback<CommonReponse> {
                    override fun onResponse(
                        call: Call<CommonReponse>,
                        response: Response<CommonReponse>
                    ) {
                        dialog.dismiss()
                        response.body().apply {

                            if (this != null) {
                                if (message == "success") {
                                    finishAffinity()
                                    startActivity(
                                        Intent(
                                            this@ViewAccepted,
                                            SellerMainActivity::class.java
                                        )
                                    )
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<CommonReponse>, t: Throwable) {
                        dialog.dismiss()
                    }
                })
        }
    }

    private fun data() {
        val type = getSharedPreferences("user", MODE_PRIVATE).getString("language", "")!!
        if (type == "Tamil") {
            translate(TranslateLanguage.TAMIL)
        } else if (type == "Telugu") {
            translate(TranslateLanguage.TELUGU)
        } else if (type == "Kannada") {
            translate(TranslateLanguage.KANNADA)
        } else if (type == "Hindi") {
            translate(TranslateLanguage.HINDI)
        } else if (type == "English") {
            bind.recyclerView2.adapter = ItemsAdapter(this, data2, "nontans")
        }
        if (type != "English") {
            liveData.viewdata().observe(this) {
                if (it.size == data2.size) {
                    bind.recyclerView2.adapter = ItemsAdapter(this, it, "trans")
                }
            }
            onewordchange.observer().observe(this) {
                if (it.size == string.size) {
                    bind.appCompatButton2.text = it[0]
                    bind.textView.text = it[1]
                    bind.viewuser.text = it[2]
                    title = it[3]
                    message = it[4]
                    yes = it[5]
                    no = it[6]
                }
            }
        }
    }

    private fun translate(language: String) {
        dialog.show()
        val option = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(language)
            .build()
        val download = DownloadConditions.Builder().build()
        Translation.getClient(option)
            .downloadModelIfNeeded(download)
            .addOnSuccessListener {
                dialog.dismiss()
                liveData.getdata(option, data2)
                onewordchange.option(option, string)
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}