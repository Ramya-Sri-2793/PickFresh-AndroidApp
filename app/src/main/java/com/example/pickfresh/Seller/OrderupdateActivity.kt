package com.example.pickfresh.Seller

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.pickfresh.Model.Items
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.R
import com.example.pickfresh.Responses.CommonReponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.databinding.ActivityOrderupdateBinding
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
import java.lang.StringBuilder

class OrderupdateActivity : AppCompatActivity() {
   private lateinit var bind: ActivityOrderupdateBinding
   lateinit var dialog: Dialog
   val dummy = ArrayList<String>()
   lateinit var view_model: Onewordchange

   val k = arrayOf("All Completed", "Still there")
   var lang = ""

   @SuppressLint("SetTextI18n")
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      bind = ActivityOrderupdateBinding.inflate(layoutInflater)
      setContentView(bind.root)
      view_model = ViewModelProvider(this)[Onewordchange::class.java]

      val item = intent.getParcelableExtra<Items>("data")?.apply {

         val string = StringBuilder()
         price.forEach {
            if (it.isDigit()) {
               string.append(it)
            }
         }
         bind.ruppes.setText(string)
         bind.imager.load(Uri.parse(this.itemphoto)) {
            crossfade(750)
            size(200)
         }
         bind.imager.scaleType = ImageView.ScaleType.CENTER

         val text = "${this.itemname}\n${this.price}"
         bind.details3.text = text


      }

      dummy.add(bind.btnupdate.text.toString())
      dummy.add(bind.checkstatus.text.toString())
      dummy.add(k[0])
      dummy.add(k[1])
      ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, k).apply {
         bind.spinner2.adapter = this
      }
      if (item == null) {
         bind.details3.text = "Something Went Wrong"
      }



      bind.btnupdate.setOnClickListener {
         val real = arrayOf("All Completed", "Still there")
         val cost = bind.ruppes.text.toString().trim()

         if (cost.isEmpty()) {
            Toast.makeText(this, "Please enter Some Amout", Toast.LENGTH_SHORT).show()
         } else {
            update_fun(
               string = real[bind.spinner2.selectedItemPosition],
               id = item!!.id.toString(),
               cost
            )
         }

      }

      dialog = Dialog(this).apply {
         setContentView(R.layout.progressdi)
         setCancelable(false)
         window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      }
      data()
   }

   private fun update_fun(string: String, id: String, cost: String) {
      dialog.show()
      CoroutineScope(IO).launch {
         Retrofit.instance.update(id = id, state = string, cost = cost)
            .enqueue(object : Callback<CommonReponse> {
               override fun onResponse(
                  call: Call<CommonReponse>,
                  response: Response<CommonReponse>
               ) {
                  dialog.dismiss()
                  response.body().apply {
                     if (this != null) {
                        if (message == "success") {
                           finish()
                        }
                        if (getSharedPreferences("user", MODE_PRIVATE).getString(
                              "language",
                              ""
                           ) != "English"
                        ) {
                           view_model.string(message)
                           view_model.toast().observe(this@OrderupdateActivity) {
                              Toast.makeText(this@OrderupdateActivity, it, Toast.LENGTH_SHORT)
                                 .show()
                           }


                        } else {
                           Toast.makeText(this@OrderupdateActivity, message, Toast.LENGTH_SHORT)
                              .show()
                        }


                     } else {
                        Toast.makeText(
                           this@OrderupdateActivity,
                           "$this",
                           Toast.LENGTH_SHORT
                        ).show()
                     }

                  }
               }

               override fun onFailure(call: Call<CommonReponse>, t: Throwable) {
                  dialog.dismiss()
                  Toast.makeText(this@OrderupdateActivity, "${t.message}", Toast.LENGTH_SHORT)
                     .show()
               }
            })
      }
   }

   private fun data() {
      when (getSharedPreferences("user", MODE_PRIVATE).getString("language", "")!!) {
         "Tamil" -> {
            translate(TranslateLanguage.TAMIL)
         }

         "Telugu" -> {
            translate(TranslateLanguage.TELUGU)
         }

         "Kannada" -> {
            translate(TranslateLanguage.KANNADA)
         }

         "Hindi" -> {
            translate(TranslateLanguage.HINDI)
         }
      }
   }

   private fun translate(language: String) {
      dialog.show()
      val option = TranslatorOptions.Builder()
         .setSourceLanguage(TranslateLanguage.ENGLISH)
         .setTargetLanguage(language)
         .build()
      val downlaod = DownloadConditions.Builder().build()
      val translation = Translation.getClient(option)
      translation.downloadModelIfNeeded(downlaod)
         .addOnSuccessListener {


            view_model.option(option, dummy)
            view_model.observer().observe(this) {
               if (it.size == dummy.size) {
                  bind.btnupdate.text = it[0]
                  bind.checkstatus.text = HtmlCompat.fromHtml(
                     "<u>${it[1]}</u>",
                     HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS
                  )
                  k[0] = it[2]
                  k[1] = it[3]
               }

               ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, k).apply {
                  bind.spinner2.adapter = this
               }
            }
            dialog.dismiss()
         }.addOnFailureListener {
            dialog.dismiss()
         }


   }

}