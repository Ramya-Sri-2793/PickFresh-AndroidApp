package com.example.pickfresh.Seller

import android.app.BackgroundServiceStartNotAllowedException
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.telecom.Call
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.BitmapCompat
import coil.load
import com.example.pickfresh.R
import com.example.pickfresh.Responses.CommonReponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.databinding.ActivityAdditemsBinding
import com.example.pickfresh.toast
import com.google.android.gms.common.internal.service.Common
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder
import javax.security.auth.callback.Callback

class Additems : AppCompatActivity() {
    private lateinit var bind: ActivityAdditemsBinding

    val string = StringBuilder()
    lateinit var dialog: Dialog
    var encoded = ""
    var one = ""
    var two = ""
    var three = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAdditemsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        dialog = Dialog(this).apply {
            setCancelable(false)
            setContentView(R.layout.progressdi)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val onactivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val data = it.data
                if (data != null) {
                    val bitmap = data.extras!!.get("data") as Bitmap
                    val out = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    encoded = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
                    bind.shapeimage2.load(bitmap)
                }
            }
        bind.shapeimage2.setOnClickListener {
            onactivity.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
        one = "Please enter your item Name"
        two = "Please enter your item Price"
        three = "Please capture image"


        string.append(bind.namehint.hint.toString() + ",")
        string.append(bind.pricehit.hint.toString() + ",")
        string.append(bind.additems.text.toString() + ",$one,$two,$three")

        bind.additems.setOnClickListener {
            val name = bind.name2.text.toString().trim()
            val price = bind.price.text.toString().trim()
            if (name.isEmpty()) {
                it.toast(one)
            } else if (price.isEmpty()) {
                it.toast(two)
            } else if (encoded.isEmpty()) {
                it.toast(three)
            } else {
                dialog.show()
                val shared = getSharedPreferences("user", MODE_PRIVATE)
                CoroutineScope(IO).launch {
                    Retrofit.instance.additems(
                        itemname = name,
                        sellerid = shared.getString("id", "")!!,
                        itemphoto = encoded,
                        price = price
                    ).enqueue(object : retrofit2.Callback<CommonReponse> {
                        override fun onResponse(
                            call: retrofit2.Call<CommonReponse>,
                            response: Response<CommonReponse>
                        ) {
                            dialog.dismiss()
                            response.body().apply {
                                if (this != null) {
                                    if (message == "success") {
                                        finish()
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<CommonReponse>, t: Throwable) {
                            dialog.dismiss()
                        }
                    })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        data()
    }

    override fun onStart() {
        super.onStart()
        data()
    }

    private fun data() {
        val type = getSharedPreferences("user", MODE_PRIVATE).getString("language", "")
        if (type == "Tamil") {
            transalte(TranslateLanguage.TAMIL)
        } else if (type == "Telugu") {
            transalte(TranslateLanguage.TELUGU)
        } else if (type == "Kannada") {
            transalte(TranslateLanguage.KANNADA)
        } else if (type == "Hindi") {
            transalte(TranslateLanguage.HINDI)
        } else if (type == "English") {
            var num = 0
            string.split(",").forEach {
                if (num == 0) {
                    bind.name2.hint = it.trim()
                } else if (num == 1) {
                    bind.price.hint = it.trim()
                } else if (num == 2) {
                    bind.additems.text = it.trim()
                } else if (num == 3) {
                    one = it
                } else if (num == 4) {
                    two = it
                } else if (num == 5) {
                    three = it
                }
                num++
            }
        }
    }

    private fun transalte(language: String) {
        dialog.show()
        val option = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(language)
            .build()
        val download = DownloadConditions.Builder().build()
        val trans = Translation.getClient(option)
        trans.downloadModelIfNeeded(download)
            .addOnSuccessListener {
                var num = 0

                string.split(",").forEach {
                    trans.translate(it).addOnSuccessListener {
                        when (num) {
                            0 -> {
                                bind.namehint.hint = it.trim()
                            }
                            1 -> {
                                bind.pricehit.hint = it.trim()
                            }
                            2 -> {
                                bind.additems.text = it.trim()
                            }
                            3 -> {
                                one = it
                            }
                            4 -> {
                                two = it
                            }
                            5 -> {
                                three = it
                            }
                        }
                        num++
                    }
                        .addOnFailureListener {
                        }
                }
                dialog.dismiss()
            }
            .addOnFailureListener {
                dialog.dismiss()
            }

    }
}