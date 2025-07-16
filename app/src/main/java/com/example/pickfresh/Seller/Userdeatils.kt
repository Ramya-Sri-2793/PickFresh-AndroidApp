package com.example.pickfresh.Seller

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.pickfresh.Buyer.MapsActivity
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.Model.User
import com.example.pickfresh.R
import com.example.pickfresh.Responses.LoginResponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.databinding.ActivityUserdeatilsBinding
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

class Userdeatils : AppCompatActivity() {
    private lateinit var bind:ActivityUserdeatilsBinding
    private lateinit var dialog:Dialog
    private lateinit var data24:ArrayList<User>
    private lateinit var onewordchange: Onewordchange
    val string=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivityUserdeatilsBinding.inflate(layoutInflater)
        setContentView(bind.root)
        onewordchange=ViewModelProvider(this)[Onewordchange::class.java]
        data24=ArrayList()
        dialog=Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
bind.call.setOnClickListener {
    if(string.isNotEmpty()){
        Intent(Intent.ACTION_DIAL).apply{
            data = Uri.parse("tel:${data24[0].mobile}")
            startActivity(this)
        }
    }else{
        it.toast("No user details")
    }
}
        bind.navigate.setOnClickListener {
            if(string.isNotEmpty()){
                Intent(this,MapsActivity::class.java).apply {
                    putExtra("latlon",data24[0].location)
                    startActivity(this)
                }
            }
        }


        dialog.show()
        CoroutineScope(IO).launch {
            Retrofit.instance.getuserdeatils(condition="userdeatils",id="${intent.getStringExtra("userid")}").enqueue(object :Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
response.body().apply {
    if(this!=null){
        if(data.isNotEmpty()){
            data24=data
            bind.nouser.isVisible=false
            bind.second2.isVisible=true
            string.add("Name : ${data24[0].name}")
            string.add("Mail : ${data24[0].mail}")
            string.add("Mobile : ${data24[0].mobile}")
            string.add("Call")
            string.add("Navigate")
            data2()
        }else{
            bind.nouser.isVisible=true
            bind.second2.isVisible=false
        }
    }
}
                    dialog.dismiss()
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    bind.nouser.isVisible=true
                    bind.second2.isVisible=false
                    Toast.makeText(this@Userdeatils, "${t.message}", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun data2() {
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
            bind.details6.text="${string[0]}\n${string[1]}\n${string[2]}"
            bind.call.text= string[3]
            bind.navigate.text= string[4]

        }
        if(type!="English"){
            onewordchange.observer().observe(this){
                if(it.size==string.size){
                    bind.details6.text="${it[0]}\n${it[1]}\n${it[2]}"
                    bind.call.text= it[3]
                    bind.navigate.text= it[4]
                }
            }
        }
    }

    private fun translate(language: String) {
        dialog.show()
        val option=TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(language)
            .build()
        val download=DownloadConditions.Builder().build()
        Translation.getClient(option)
            .downloadModelIfNeeded(download)
            .addOnSuccessListener {
                onewordchange.option(option,string)
                dialog.dismiss()
            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}