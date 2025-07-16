package com.example.pickfresh.Buyer

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.pickfresh.Model.*
import com.example.pickfresh.R
import com.example.pickfresh.Responses.*
import com.example.pickfresh.databinding.ActivityViewRequestsBinding
import com.example.pickfresh.toast
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ViewItems : AppCompatActivity(), click {
    var item: User? = null
    lateinit var onewordchange: Onewordchange
    var array = ArrayList<String>()
    lateinit var dialog: Dialog
    lateinit var user: ArrayList<Seconddata>
    var datclass: User? = null
    lateinit var realdata: ArrayList<Seconditem>
    var string = "please add any for Request"
    lateinit var viewModel: com.example.pickfresh.Seller.ViewModel
    private lateinit var bind: ActivityViewRequestsBinding

    @SuppressLint("SimpleDateFormat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityViewRequestsBinding.inflate(layoutInflater)
        setContentView(bind.root)
        user = ArrayList()
        realdata = ArrayList()
        dialog = Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        bind.cycle.layoutManager = LinearLayoutManager(this@ViewItems)
        onewordchange = ViewModelProvider(this)[Onewordchange::class.java]
        viewModel = ViewModelProvider(this)[com.example.pickfresh.Seller.ViewModel::class.java]
        intent.getParcelableExtra<User>("data").apply {
            if (this != null) {
                datclass = this
                item = this
                array.add(id)
                array.add(name)
                array.add(mail)
                array.add(mobile)
                array.add(password)
                array.add(location)
                array.add(type)
                array.add(image)
                array.add(date)
                array.add(state)
                array.add("Name")
                array.add("Phone Number")
                array.add("Registered in")
                array.add("please add any for Request")
                array.add(bind.textview45.text.toString())
                bind.image3.load(image) {
                    crossfade(750)
                    size(250)
                }
            } else {
                item = null
            }
        }
        val id = getSharedPreferences("user", MODE_PRIVATE).getString("id", "")!!
        bind.card.setOnClickListener { it ->
            val simple = SimpleDateFormat("dd/MMMM/yyyy(hh:mm:ss)")
            if (user.isNotEmpty()) {
                dialog.show()
                user.forEach {
                    if (it.quatity != 0) {
                        CoroutineScope(IO).launch {
                            Retrofit.instance.orderitem(
                                userid = id,
                                orderid = "PFID${System.currentTimeMillis()}",
                                sellerid = item!!.id,
                                status = "process",
                                itemphoto = it.itemphoto,
                                itemname = it.itemname,
                                qty = "${it.quatity}",
                                price = it.price,
                                date = simple.format(Date()),
                            ).enqueue(object : Callback<CommonReponse> {
                                override fun onResponse(
                                    call: Call<CommonReponse>,
                                    response: Response<CommonReponse>
                                ) {
                                    response.body().apply {
                                        if (this != null) {
                                            if (getSharedPreferences(
                                                    "user",
                                                    MODE_PRIVATE
                                                ).getString("language", "") != "English"
                                            ) {
                                                onewordchange.string(message)
                                            }

                                            if (message == "success") {
                                                finish()
                                            }
                                        }
                                    }
                                    dialog.dismiss()
                                }

                                override fun onFailure(call: Call<CommonReponse>, t: Throwable) {
                                    dialog.dismiss()
                                    if (getSharedPreferences("user", MODE_PRIVATE).getString(
                                            "language",
                                            ""
                                        ) != "English"
                                    ) {
                                        onewordchange.string("${t.message}")
                                    }
                                }
                            })
                        }
                    }
                }
            } else {
                it.toast(string)

            }
        }
        onewordchange.toast().observe(this@ViewItems) {
            Toast.makeText(this@ViewItems, it, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onStart() {
        super.onStart()
        data(data = ArrayList())
        viewdata()
        getdata()
        getrating()
    }

    private fun getrating() {
        CoroutineScope(IO).launch {
            Retrofit.instance.getrating(datclass!!.id).enqueue(object : Callback<Reviewresponse> {
                override fun onResponse(
                    call: Call<Reviewresponse>,
                    response: Response<Reviewresponse>
                ) {
                    response.body().apply {
                        if (this != null) {
                            var num = 0.0f
                            for (data in data) {
                                num += data.rating.toFloat()
                            }
                            bind.rating2.rating = num / data.size

                        }

                    }
                }

                override fun onFailure(call: Call<Reviewresponse>, t: Throwable) {
                    Toast.makeText(this@ViewItems, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun getdata() {
        dialog.show()
        CoroutineScope(IO).launch {
            Retrofit.instance.getitemsbyid(
                id = item!!.id, condition = "getitems"
            ).enqueue(object : Callback<GoodResponse> {
                override fun onResponse(
                    call: Call<GoodResponse>,
                    response: Response<GoodResponse>
                ) {
                    response.body().apply {
                        if (this != null) {
                            realdata = second
                            data(data)
                            viewModel.viewdata().observe(this@ViewItems) {
                                if (it.size == data.size) {
                                    bind.cycle.setItemViewCacheSize(it.size)
                                    bind.cycle.adapter = RequestAdapter(
                                        this@ViewItems, it,
                                        user, this@ViewItems, "trans",
                                        second
                                    )
                                }
                            }
                        }
                    }
                    dialog.dismiss()
                }

                override fun onFailure(call: Call<GoodResponse>, t: Throwable) {
                    dialog.dismiss()
                    Toast.makeText(this@ViewItems, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    private fun data(data: ArrayList<Items>) {
        val translate = getSharedPreferences("user", MODE_PRIVATE).getString("language", "")
        if ("Tamil" == translate) {
            trnaslati(TranslateLanguage.TAMIL, data)
        } else if ("Kannada" == translate) {
            trnaslati(TranslateLanguage.KANNADA, data)
        } else if ("Hindi" == translate) {
            trnaslati(TranslateLanguage.HINDI, data)
        } else if ("Telugu" == translate) {
            trnaslati(TranslateLanguage.TELUGU, data)
        } else if ("English" == translate) {

            bind.cycle.setItemViewCacheSize(data.size)
            bind.cycle.adapter =
                RequestAdapter(this@ViewItems, data, user, this@ViewItems, "nontrnas", realdata)

            if (item != null) {

                val string = "<b>${array[10]} </b>${item!!.name}<br></br>" +
                        "<b>${array[11]}: </b>${item!!.mobile}<br></br>" +
                        "<b>${array[12]} : </b>${item!!.date}<br></br>"
                bind.textview.text = HtmlCompat.fromHtml(string, FROM_HTML_OPTION_USE_CSS_COLORS)
                bind.textview45.text = array[13]
            }
        }
    }

    private fun viewdata() {
        onewordchange.observer().observe(this) {
            if (it.size == array.size) {
                var string = "<b>${it[10]} : </b>${it[1]}<br></br>" +
                        "<b>${it[11]}: </b>${it[3]}<br></br>" +
                        "<b>${it[12]} : </b>${item!!.date}<br></br>"
                bind.textview.text = HtmlCompat.fromHtml(string, FROM_HTML_OPTION_USE_CSS_COLORS)
                bind.textview45.text = it[13]
                string = it[14]
            }
        }
    }

    private fun trnaslati(language: String, data: ArrayList<Items>) {

        val option = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(language)
            .build()
        val donwnload = DownloadConditions.Builder().build()
        Translation.getClient(option)
            .downloadModelIfNeeded(donwnload)
            .addOnSuccessListener {
                if (data.isNotEmpty()) {
                    viewModel.data(data = data, option)
                } else {
                    onewordchange.option(option, array)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun item(data: Int) {

        bind.card.isVisible = data > 0
    }
}
