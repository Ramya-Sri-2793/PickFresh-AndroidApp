package com.example.pickfresh.Seller

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pickfresh.Model.Items
import com.example.pickfresh.R
import com.example.pickfresh.Responses.ProductResponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.databinding.ActivityViewitemsBinding
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

class Viewitems : AppCompatActivity() {
    private lateinit var bind: ActivityViewitemsBinding
    lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityViewitemsBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.cycle.layoutManager = LinearLayoutManager(this)
        dialog = Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.show()
        data()
    }

    private fun data() {
        val shared = getSharedPreferences("user", MODE_PRIVATE)
        CoroutineScope(IO).launch {
            Retrofit.instance.getitems(condition = "items", id = shared.getString("id", "")!!)
                .enqueue(object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse>,
                        response: Response<ProductResponse>
                    ) {
                        response.body().apply {
                            if (this != null) {
                                converter(data)
                            } else {
                                dialog.dismiss()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                        dialog.dismiss()
                        Toast.makeText(this@Viewitems, "${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun converter(data: ArrayList<Items>) {
        when (getSharedPreferences("user", MODE_PRIVATE).getString("language", "")!!) {
            "Tamil" -> {
                translate(TranslateLanguage.TAMIL, data)
            }
            "Telugu" -> {
                translate(TranslateLanguage.TELUGU, data)
            }
            "Kannada" -> {
                translate(TranslateLanguage.KANNADA, data)
            }
            "Hindi" -> {
                translate(TranslateLanguage.HINDI, data)
            }
            "English" -> {
                bind.cycle.adapter = AdapterForViewItems(this, data, "not change")
                dialog.dismiss()
            }
        }
    }

    private fun translate(language: String, data: ArrayList<Items>) {
        val option = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(language)
            .build()
        val model = ViewModelProvider(this)[ViewModel::class.java]
        model.data(data, option)
        model.viewdata().observe(this) {
            if (it != null) {
                bind.cycle.adapter = AdapterForViewItems(this, it, "change")
            }
        }

        dialog.dismiss()

    }

    override fun onResume() {
        super.onResume()
        data()
    }
}