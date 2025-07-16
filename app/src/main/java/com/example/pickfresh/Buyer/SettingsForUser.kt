package com.example.pickfresh.Buyer

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.pickfresh.MainActivity
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.R
import com.example.pickfresh.databinding.ActivitySettingsForUserBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class SettingsForUser : AppCompatActivity() {
    private lateinit var bind:ActivitySettingsForUserBinding
        val string=ArrayList<String>()
    lateinit var dialog:Dialog
    lateinit var tamil:RadioButton
    lateinit var telugu:RadioButton
    lateinit var kannada:RadioButton
    lateinit var english:RadioButton
    lateinit var hindi:RadioButton
    lateinit var onewordchange: Onewordchange
    lateinit var process:Dialog
    var title2="Do you want Logout ?"
    var message="Press Yes to logout or No Cancel !!"
    var yes="Yes"
    var no="No"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivitySettingsForUserBinding.inflate(layoutInflater)
        setContentView(bind.root)
        onewordchange=ViewModelProvider(this)[Onewordchange::class.java]
        dialog=Dialog(this).apply {
            setContentView(R.layout.language)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            tamil=this.findViewById(R.id.tamil)
            telugu=this.findViewById(R.id.telugu)
            kannada=this.findViewById(R.id.kannada)
            english=this.findViewById(R.id.english)
            hindi=this.findViewById(R.id.hindi)
        }
        process=Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        string.add(bind.languagetxt2.text.toString())
        string.add(bind.logout3.text.toString())
        string.add(title2)
        string.add(message)
        string.add(yes)
        string.add(no)

        bind.language1.setOnClickListener {
            val language=getSharedPreferences("user", MODE_PRIVATE).getString("language","")
            dialog.show()
            if(language=="Tamil"){
                tamil.isChecked=true
            }else if(language=="Kannada"){
                kannada.isChecked=true
            }else if(language=="Telugu"){
              telugu.isChecked=true
            }else if(language=="Hindi"){
           hindi.isChecked=true
            }else if(language=="English"){
                english.isChecked=true
            }
        }
        bind.logout1.setOnClickListener { dialogbox() }
        tamil.setOnClickListener { view(tamil.text.toString()) }
        telugu.setOnClickListener { view(telugu.text.toString()) }
        kannada.setOnClickListener { view(kannada.text.toString()) }
        english.setOnClickListener { view(english.text.toString()) }
        hindi.setOnClickListener { view(hindi.text.toString()) }
    }

    private fun dialogbox() {
        AlertDialog.Builder(this).apply {
            setTitle(title2)
            setMessage(message)
            setPositiveButton(yes) { dialog, _ ->
                dialog.dismiss()
                getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply()
                finishAffinity()
                startActivity(Intent(this@SettingsForUser, MainActivity::class.java))
            }
            setNegativeButton(no){dialog,_->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun view(language: String) {
        if("Tamil"==language){
            viewdata(TranslateLanguage.TAMIL,"Tamil")
        }else if("Telugu"==language){
            viewdata(TranslateLanguage.TELUGU,"Telugu")
        }else if("Kannada"==language){
            viewdata(TranslateLanguage.KANNADA,"Kannada")
        }else if("English"==language){

            getSharedPreferences("user", MODE_PRIVATE).edit().putString("language","English").apply()
            bind.languagetxt2.text=string[0]
            bind.logout3.text=string[1]
            title2=string[2]
            message=string[3]
            yes=string[4]
            no=string[5]
        }else if("Hindi"==language){
            viewdata(TranslateLanguage.HINDI,"Hindi")
        }
        dialog.dismiss()
    }
    fun viewdata(language: String, tamil: String){
        process.show()
        val option=TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(language)
            .build()
        DownloadConditions.Builder().build()
        Translation.getClient(option)
            .downloadModelIfNeeded()
            .addOnSuccessListener {
                onewordchange.option(option,string)
                getSharedPreferences("user", MODE_PRIVATE).edit().putString("language",tamil).apply()
                process.dismiss()
            }
            .addOnFailureListener {
                process.dismiss()
            }
    }

    override fun onStart() {
        super.onStart()
    val shared=getSharedPreferences("user", MODE_PRIVATE).getString("language","")!!
    view(shared)

        onewordchange.observer().observe(this){
            if(it.size==string.size){
                bind.languagetxt2.text=it[0]
                bind.logout3.text=it[1]
                title2=it[2]
                message=it[3]
                yes=it[4]
                no=it[5]
            }
        }




    }



}