package com.example.pickfresh.Seller

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.pickfresh.Buyer.BroadcastReceiver
import com.example.pickfresh.Buyer.View_Pendings
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.R
import com.example.pickfresh.Responses.CommonReponse
import com.example.pickfresh.Responses.LoginResponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.databinding.SellerBinding
import com.google.android.gms.location.LocationServices
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SellerMainActivity : AppCompatActivity() {
   private lateinit var bind: SellerBinding
   lateinit var status: TextView
   lateinit var swith: SwitchCompat
   lateinit var layout: LinearLayout
   val string = ArrayList<String>()
   lateinit var shared: SharedPreferences
   lateinit var onewordchange: Onewordchange
   lateinit var dialog: Dialog
   var text = ""
   private lateinit var pending: PendingIntent
   private lateinit var alarmManager: AlarmManager
   private val fused by lazy {
      LocationServices.getFusedLocationProviderClient(this)
   }

   @SuppressLint("SetTextI18n", "ResourceAsColor")

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      bind = SellerBinding.inflate(layoutInflater)
      setContentView(bind.root)
      swith = bind.switche
      status = bind.status
      layout = bind.switchback
      dialog = Dialog(this).apply {
         setContentView(R.layout.progressdi)
         setCancelable(false)
         window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      }
      bind.itemtxt.setOnClickListener {
         startActivity(Intent(this, Viewitems::class.java))
      }
      shared = getSharedPreferences("user", MODE_PRIVATE)
      val id = shared.getString("id", "")
      dialog.show()

      bind.reques.setOnClickListener {
         Intent(this, View_Pendings::class.java).apply {
            putExtra("type", "seller")
            startActivity(this)
         }

      }

      onewordchange = ViewModelProvider(this)[Onewordchange::class.java]
      onewordchange.observer().observe(this) {
         if (it.size == string.size) {
            bind.status.text = it[0]
            bind.requeststxt.text = it[1]
            bind.acceptedtxt.text = it[2]
            bind.additemtxt.text = it[3]
            bind.itemtxt.text = it[4]
            bind.settingtxt.text = it[5]
         }
      }
      string.add(bind.status.text.toString())
      string.add(bind.requeststxt.text.toString())
      string.add(bind.acceptedtxt.text.toString())
      string.add(bind.additemtxt.text.toString())
      string.add(bind.itemtxt.text.toString())
      string.add(bind.settingtxt.text.toString())

      swith.setOnClickListener {
         whentrue(b = swith.isChecked, id = id!!)
      }
      bind.acceptedbtn.setOnClickListener {
         Intent(this, View_Pendings::class.java).apply {
            putExtra("type", "acceptedbyseller")
            startActivity(this)
         }
      }
      bind.additem.setOnClickListener { startActivity(Intent(this, Additems::class.java)) }
      bind.settings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }

      object : CountDownTimer(400000, 1000) {
         @SuppressLint("MissingPermission", "Range")
         override fun onTick(millisUntilFinished: Long) {
            fused.lastLocation.addOnSuccessListener {
               it?.let {the ->
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                     Geocoder(this@SellerMainActivity).getFromLocation(it.latitude,it.longitude,1){
                        updateLocation(it[0].getAddressLine(0),the)
                     }
                  }else{
                     val addressLine =
                        Geocoder(this@SellerMainActivity)
                           .getFromLocation(the.latitude, the.longitude, 1)
                           ?.get(0)
                           ?.getAddressLine(0)
                     updateLocation("$addressLine", the)
                  }


               }
            }
         }

         override fun onFinish() {
        Log.i("finished","O,a")
         }

      }.start()


   }

   private fun updateLocation(addressLine: String, it: Location) {
      CoroutineScope(IO).async {
         async {
            try {
               Retrofit.instance.updateLocations(
                  id = "${shared.getString("id","")}",
                  location = "${it.latitude},${it.longitude}",
                  address = addressLine
               )

            } catch (e: Exception) {
               Log.i("NothingWe","${e.message}")
               null
            }
         }.await()?.let {
            Log.i("NothingWe","${it.body()}")
         }
      }.start()
   }

   private fun mydata() {
      CoroutineScope(IO).launch {
         Retrofit.instance.getuser(
            id = shared.getString("id", "")!!,
            condition = "getuserbyid",
         ).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
               call: Call<LoginResponse>,
               response: Response<LoginResponse>,
            ) {
               response.body().apply {
                  if (this != null) {
                     if (data.isNotEmpty()) {
                        if (data[0].state == "true") {
                           swith.isChecked = true
                           text = "Online"
                           online()
                        } else {
                           swith.isChecked = false
                           offline()
                           text = "Offline"
                        }

                        if (shared.getString("language", "") != "English") {
                           onewordchange.string(text)
                           onewordchange.toast().observe(this@SellerMainActivity) {
                              if (it != null) {
                                 status.text = it
                              }
                           }
                        } else {
                           bind.status.text = text
                        }
                        bind.cardView.isVisible = true

                     }
                  }
               }
               dialog.dismiss()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
               dialog.dismiss()
               Toast.makeText(this@SellerMainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
         })
      }
   }

   @SuppressLint("ResourceAsColor", "SetTextI18n")
   private fun whentrue(b: Boolean, id: String) {
      CoroutineScope(IO).launch {
         Retrofit.instance.stateupdate(
            id = id, condition = "update", state = b.toString()
         ).enqueue(object : Callback<CommonReponse> {
            override fun onResponse(
               call: Call<CommonReponse>,
               response: Response<CommonReponse>,
            ) {
               response.body().apply {
                  if (this != null) {
                     if (error) {
                        if (text == "Online") {
                           offline()
                        } else if (text == "Offline") {
                           online()
                        }

                        val k = getSharedPreferences("user", MODE_PRIVATE).getString("language", "")
                        if (k != "English") {
                           smallfun()
                           onewordchange.string(text)
                        } else {
                           smallfun()
                           status.text = text
                        }

                     }
                  } else {
                     Log.i("SellerMainActivity123", "$this")
                  }

               }
               dialog.dismiss()
            }

            override fun onFailure(call: Call<CommonReponse>, t: Throwable) {
               Log.i("SellerMainActivity123", "${t.message}")
               dialog.dismiss()
            }
         })
      }


   }

   private fun smallfun() {
      if (text == "Online") {
         text = "Offline"
      } else if (text == "Offline") {
         text = "Online"
      }
   }

   @SuppressLint("ResourceAsColor")
   private fun offline() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         layout.setBackgroundColor(getColor(R.color.offline))
      } else {
         layout.setBackgroundColor(R.color.offline)
      }
   }

   @SuppressLint("ResourceAsColor")
   private fun online() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         layout.setBackgroundColor(getColor(R.color.online))
      } else {
         layout.setBackgroundColor(R.color.online)
      }
   }

   override fun onResume() {
      super.onResume()
      translate()
      mydata()
   }

   private fun translate() {
      val type = getSharedPreferences("user", MODE_PRIVATE).getString("language", "")
      if (type == "Tamil") {
         translate(TranslateLanguage.TAMIL)
      } else if (type == "Telugu") {
         translate(TranslateLanguage.TELUGU)
      } else if (type == "Kannada") {
         translate(TranslateLanguage.KANNADA)
      } else if (type == "Hindi") {
         translate(TranslateLanguage.HINDI)
      } else if (type == "English") {
         data(string)
      }
   }

   private fun data(string: ArrayList<String>) {
      if (string.size == string.size) {
         bind.status.text = string[0]
         bind.requeststxt.text = string[1]
         bind.acceptedtxt.text = string[2]
         bind.additemtxt.text = string[3]
         bind.itemtxt.text = string[4]
         bind.settingtxt.text = string[5]
      }
   }

   private fun translate(languages: String) {
      dialog.show()
      val option = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH)
         .setTargetLanguage(languages).build()
      val download = DownloadConditions.Builder().build()
      val trans = Translation.getClient(option)
      trans.downloadModelIfNeeded(download).addOnSuccessListener {
         onewordchange.option(option, string)
         dialog.dismiss()
      }.addOnFailureListener {
         dialog.dismiss()
         Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
      }
   }

   override fun onStart() {
      super.onStart()
      translate()
      broadcast("start")
      onewordchange = ViewModelProvider(this)[Onewordchange::class.java]
      mydata()
   }

   override fun onDestroy() {
      super.onDestroy()
      broadcast("")
   }

   @SuppressLint("UnspecifiedImmutableFlag")
   fun broadcast(command: String) {
      if (command == "start") {
         pending = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
               this, 100, Intent(this, BroadcastReceiver::class.java), PendingIntent.FLAG_MUTABLE
            )
         } else {
            PendingIntent.getBroadcast(this, 100, Intent(this, BroadcastReceiver::class.java), 0)
         }
         alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
         alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, pending
         )
      } else {
         alarmManager.cancel(pending)
         pending.cancel()
      }
   }
}