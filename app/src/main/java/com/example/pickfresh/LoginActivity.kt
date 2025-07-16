package com.example.pickfresh

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.pickfresh.Admin.AddSeller
import com.example.pickfresh.Admin.AdminActivity
import com.example.pickfresh.Buyer.BuyerMainActivity
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.Responses.LoginResponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.Seller.SellerMainActivity
import com.example.pickfresh.databinding.ActivityLoginBinding
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

class LoginActivity : AppCompatActivity() {
    private lateinit var bind:ActivityLoginBinding
lateinit var dialog:Dialog
lateinit var onewordchange: Onewordchange
var realString=ArrayList<String>()
var kk=arrayOf("English","Tamil","Telugu","Kannada","Hindi")
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        onewordchange=ViewModelProvider(this)[Onewordchange::class.java]
        realString.add("${bind.titlew.text}")
        realString.add("${bind.email.text}")
        realString.add("${bind.email2.hint}")
        realString.add("${bind.password.text}")
        realString.add("${bind.password2.hint}")
        realString.add("${bind.btn.text}")
        realString.add("${bind.dont.text}")
        realString.add("${bind.create.text}")
        realString.add("${bind.create2.text}")
        realString.add("${bind.dont2.text}")
        dialog=Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
bind.create2.setOnClickListener {
startActivity(Intent(this, AddSeller::class.java))

}

bind.btn.setOnClickListener {

    val mail=bind.email2.text.toString().trim()
    val password=bind.password2.text.toString().trim()

 if(!mail.contains("@gmail.com")) {
it.toast("Please enter a valid email")
    }else if(password.isEmpty()){
        it.toast("Please enter your password")
    }/*else if(password.lowercase()=="admin"&&mail.lowercase()=="admin@gmail.com") {
        getSharedPreferences("user", MODE_PRIVATE).edit().putString("type","admin").apply()
        startActivity(Intent(this,AdminActivity::class.java))
     finishAffinity()
         }*/else{
     dialog.show()
        CoroutineScope(IO).launch {
            Retrofit.instance.login(condition = "login",email = mail, password=password).enqueue(object :Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    dialog.dismiss()
                    response.body().apply {
                        if(this!=null) {
                            if (data.isNotEmpty()) {
                                val k = data[0]
                                getSharedPreferences("user", MODE_PRIVATE).edit().apply {
                                    putString("id", k.id)
                                    putString("name", k.name)
                                    putString("mail", k.mail)
                                    putString("mobile", k.mobile)
                                    putString("password", k.password)
                                    putString("location", k.location)
                                    putString("type", k.type)
                                    putString("state", k.state)
                                    putString("language","English")
                                    apply()
                                }
                                finishAffinity()
                                if (k.type == "user") {
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            BuyerMainActivity::class.java
                                        )
                                    )
                                } else if (k.type == "seller") {
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            SellerMainActivity::class.java
                                        )
                                    )
                                }

                            } else {
                                if (message == "failed") {
                                    it.toast("Invalid user")
                                }
                            }
                        }else{
                            it.toast(response.body()!!)
                        }
                    }

                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                it.toast(t.message!!)
                    dialog.dismiss()
                }
            })
        }
    }
}



        bind.create.setOnClickListener {
        Intent(this,Signup::class.java).apply {
            putExtra("language",bind.spinner.selectedItem.toString())
            startActivity(this)
        }
        }

ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,kk)
    .apply {
        bind.spinner.adapter=this
    }

        bind.spinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
if(kk[p2]=="Tamil"){
    translate(TranslateLanguage.TAMIL)
}else if(kk[p2]=="English"){
    transulated(realString)
}else if(kk[p2]=="Telugu"){
    translate(TranslateLanguage.TELUGU)
}else if(kk[p2]=="Kannada"){
    translate(TranslateLanguage.KANNADA)
}else if(kk[p2]=="Hindi"){
    translate(TranslateLanguage.HINDI)
}
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }

    private fun transulated(it: ArrayList<String>) {
        if(realString.size==it.size){
            bind.titlew.text=it[0]
            bind.email.text=it[1]
            bind.email2.hint=it[2]
            bind.password.text=it[3]
            bind.password2.hint=it[4]
            bind.btn.text=it[5]
            bind.dont.text=it[6]
            bind.create.text=it[7]
            bind.create2.text=it[8]
            bind.dont2.text=it[9]

        }
        bind.titlew.textSize=16f
        bind.email.textSize=16f
        bind.email2.textSize=16f
        bind.password.textSize=16f
        bind.password2.textSize=16f
        bind.btn.textSize=16f
        bind.dont.textSize=16f
        bind.create.textSize=16f
    }

    private fun translate(langauage: String) {

        if(TranslateLanguage.TAMIL==langauage){
            bind.titlew.textSize=14f
            bind.email.textSize=14f
            bind.email2.textSize=14f
            bind.password.textSize=14f
            bind.password2.textSize=14f
            bind.btn.textSize=14f
            bind.dont.textSize=14f
            bind.create.textSize=14f
            bind.create2.textSize=14f
            bind.dont2.textSize=14f
        }else{
            bind.titlew.textSize=16f
            bind.email.textSize=16f
            bind.email2.textSize=16f
            bind.password.textSize=16f
            bind.password2.textSize=16f
            bind.btn.textSize=16f
            bind.dont.textSize=16f
            bind.create.textSize=16f
            bind.create2.textSize=16f
            bind.dont2.textSize=16f
        }

        dialog.show()
val option= TranslatorOptions.Builder()
    .setSourceLanguage(TranslateLanguage.ENGLISH)
    .setTargetLanguage(langauage)
    .build()
        val condition=DownloadConditions.Builder().build()
        val translation= Translation.getClient(option)
        translation.downloadModelIfNeeded(condition)
            .addOnSuccessListener {
                onewordchange.option(option, realString)
                onewordchange.observer().observe(this){
                    if(realString.size==it.size){
                        bind.titlew.text=it[0]
                        bind.email.text=it[1]
                        bind.email2.hint=it[2]
                        bind.password.text=it[3]
                        bind.password2.hint=it[4]
                        bind.btn.text=it[5]
                        bind.dont.text=it[6]
                        bind.create.text=it[7]
                        bind.create2.text=it[7]
                        bind.dont2.text=it[7]
                    }
                }
                dialog.dismiss()
            }
                .addOnFailureListener {
                    Toast.makeText(this, "1->${it.message}", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }


    }


