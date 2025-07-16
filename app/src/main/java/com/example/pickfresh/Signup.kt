package com.example.pickfresh

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.Responses.CommonReponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.databinding.ActivitySignupBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class Signup: AppCompatActivity() {
    private lateinit var bind:ActivitySignupBinding

    val realstring=ArrayList<String>()
    private lateinit var dialog:Dialog
    val permission= arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
    lateinit var view:Onewordchange
    lateinit var fused:FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(bind.root)
        dialog=Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        view= ViewModelProvider(this)[Onewordchange::class.java]
        fused=LocationServices.getFusedLocationProviderClient(this)
        realstring.add("${bind.signtitle.text}")
        realstring.add("${bind.namett.text}")
        realstring.add("${bind.name1.hint}")
        realstring.add("${bind.emailti.text}")
        realstring.add("${bind.email1.hint}")
        realstring.add("${bind.passworti.text}")
        realstring.add("${bind.passwor.hint}")
        realstring.add("${bind.mobilenum.text}")
        realstring.add("${bind.mobile.hint}")
        realstring.add("${bind.register.text}")
        realstring.add("${bind.already.text}")
        realstring.add("${bind.login.text}")
        type(language = intent.getStringExtra("language")!!)

        bind.login.setOnClickListener { /*finish()*/
      finish()
        }

        bind.register.setOnClickListener { it ->
            val name1=bind.name1.text.toString()
            val email1=bind.email1.text.toString()
            val passwor=bind.passwor.text.toString()
            val mobile=bind.mobile.text.toString()

            if(name1.isEmpty()){
                it.toast("Please enter your Name")
            }else if(email1.isEmpty()){
                it.toast("Please enter your Email")

            }else if(!email1.contains("@gmail.com")){
                it.toast("Enter valid Mail")
            }else if(passwor.isEmpty()){
                it.toast("Please enter your Password")
            }else if(mobile.isEmpty()){
                it.toast("Please enter your Mobile")
            }else if(mobile.length!=10){
                it.toast("Please give valid Mobile number")
            }else {
                dialog.show()
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission[0]
                    ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                        this,
                        permission[1]
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permission, 100)
                    } else {
                        ActivityCompat.requestPermissions(this, permission, 102)
                    }
                } else {
                    fused.lastLocation.addOnSuccessListener {

if(it!=null) {

val format=DecimalFormat("##.#######")
    MainScope().launch {
        Retrofit.instance.signup(
            name = name1,
            email = email1,
            mobile = mobile,
            password = passwor,
            location = "${format.format(it.latitude)},${format.format(it.longitude)}"
        ).enqueue(object : Callback<CommonReponse> {
            override fun onResponse(
                call: Call<CommonReponse>,
                response: Response<CommonReponse>
            ) {
                dialog.dismiss()
                response.body()!!.apply {
                    Toast.makeText(this@Signup, message, Toast.LENGTH_SHORT).show()
                    if (message == "success") {
                        finish()
                    }
                }


            }

            override fun onFailure(call: Call<CommonReponse>, t: Throwable) {
                dialog.dismiss()
                Toast.makeText(this@Signup, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
}else{
    dialog.dismiss()
}
                    }.addOnFailureListener {
    dialog.dismiss()
                        Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
}
                        .addOnCanceledListener {
                            dialog.dismiss()
                        }


            }
            }
        }


    }




    private fun type(language:String){
        when (language) {
            "English" -> {
                transulated(realstring)
            }
            "Tamil" -> {
                traslatemodel(TranslateLanguage.TAMIL)
            }
            "Telugu" -> {
                traslatemodel(TranslateLanguage.TELUGU)
            }
            "Kannada" -> {
                traslatemodel(TranslateLanguage.KANNADA)
            }
            "Hindi" -> {
                traslatemodel(TranslateLanguage.HINDI)
            }
        }
    }

    private fun traslatemodel(language: String) {
        dialog.show()
val option=TranslatorOptions.Builder()
    .setSourceLanguage(TranslateLanguage.ENGLISH)
    .setTargetLanguage(language)
    .build()
        val download=DownloadConditions.Builder()
            .build()

        val transulte=Translation.getClient(option)
        transulte.downloadModelIfNeeded(download)
            .addOnSuccessListener {
                view.option(option,realstring)
                view.observer().observe(this){
                    if(it.size==realstring.size){
                        bind.signtitle.text=it[0]
                        bind.namett.text=it[1]
                        bind.name1.hint=it[2]
                        bind.emailti.text=it[3]
                        bind.email1.hint=it[4]
                        bind.passworti.text=it[5]
                        bind.passwor.hint=it[6]
                        bind.mobilenum.text=it[7]
                        bind.mobile.hint=it[8]
                        bind.register.text=it[9]
                        bind.already.text=it[10]
                        bind.login.text=it[11]
                    }
                }
                dialog.dismiss()
            }.addOnFailureListener {
                bind.root.toast("1-> ${it.message}")
                dialog.dismiss()
            }
    }

    private fun transulated(it: ArrayList<String>) {
        if(it.size==realstring.size){
            bind.signtitle.text=it[0]
            bind.namett.text=it[1]
            bind.name1.hint=it[2]
            bind.emailti.text=it[3]
            bind.email1.hint=it[4]
            bind.passworti.text=it[5]
            bind.passwor.hint=it[6]
            bind.mobilenum.text=it[7]
            bind.mobile.hint=it[8]
            bind.register.text=it[9]
            bind.already.text=it[10]
            bind.login.text=it[11]
        }

    }

}