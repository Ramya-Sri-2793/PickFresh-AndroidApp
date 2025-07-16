package com.example.pickfresh.Buyer

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.R
import com.example.pickfresh.databinding.ActivityPrfolieBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import org.w3c.dom.Text

class ProflieActivity : AppCompatActivity() {
    lateinit var onewordchange: Onewordchange
    private lateinit var bind:ActivityPrfolieBinding
    val string=ArrayList<String>()
    private lateinit var dialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivityPrfolieBinding.inflate(layoutInflater)
        setContentView(bind.root)
        onewordchange=ViewModelProvider(this)[Onewordchange::class.java]
        dialog=Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        bind.root.forEach {
            it.isFocusable=false
            it.isClickable=false
        }
    getSharedPreferences("user", MODE_PRIVATE).apply {
        bind.emailtext.setText("${getString("mail","")}")
        bind.mobiletxt.setText("${getString("mobile","")}")
        bind.nametext.setText("${getString("name","")}")
        bind.emailtext.apply {
            isFocusable=false
            isClickable=false
        }
        bind.mobiletxt.apply {
            isFocusable=false
            isClickable=false
        }
        bind.nametext.apply {
            isFocusable=false
            isClickable=false
        }
    }

        string.add(bind.mobilehint.hint.toString())
        string.add(bind.emailhint.hint.toString())
        string.add(bind.namehint.hint.toString())

        data()
    }

    private fun data() {
        val type=getSharedPreferences("user", MODE_PRIVATE).getString("language","")
        if(type=="Tamil"){
            translate(TranslateLanguage.TAMIL)
        }else if(type=="Telugu"){
            translate(TranslateLanguage.TELUGU)
        }else if(type=="Kannada"){
            translate(TranslateLanguage.KANNADA)
        }else if(type=="Hindi"){
            translate(TranslateLanguage.HINDI)
        }

    }

    private fun translate(language: String) {
        dialog.show()
val option=TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH)
    .setTargetLanguage(language)
    .build()

        val download=DownloadConditions.Builder().build()
        Translation.getClient(option).downloadModelIfNeeded(download)
            .addOnSuccessListener {
                dialog.dismiss()
                onewordchange.option(option,string)
                    onewordchange.observer().observe(this){
                        if(it.size==string.size){
                            bind.mobilehint.hint=it[0]
                            bind.emailhint.hint=it[1]
                            bind.namehint.hint=it[2]
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}