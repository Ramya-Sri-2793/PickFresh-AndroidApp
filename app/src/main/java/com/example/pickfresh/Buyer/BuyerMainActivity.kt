package com.example.pickfresh.Buyer

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pickfresh.Model.LiveResponse
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.R
import com.example.pickfresh.databinding.ActivityBuyerMainBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class BuyerMainActivity : AppCompatActivity() {
   private lateinit var bind: ActivityBuyerMainBinding
   lateinit var onewordchange: Onewordchange
   lateinit var shared: SharedPreferences
   lateinit var dialog: Dialog

   lateinit var home: MenuItem
   lateinit var profile: MenuItem
   lateinit var settings: MenuItem
   lateinit var sellers: MenuItem
   lateinit var requests: MenuItem

   lateinit var requestsfragement: Fragment
   lateinit var Sellersfargment: Fragment
   var string = ArrayList<String>()

   @SuppressLint("RtlHardcoded")
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      bind = ActivityBuyerMainBinding.inflate(layoutInflater)
      setContentView(bind.root)

      dialog = Dialog(this).apply {
         setContentView(R.layout.progressdi)
         setCancelable(false)
         window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      }
      onewordchange = ViewModelProvider(this)[Onewordchange::class.java]
      shared = getSharedPreferences("user", MODE_PRIVATE)
      val nav = bind.navigationView.menu
      val bottom = bind.bottomNavigationView.menu
      home = nav.findItem(R.id.home1)
      profile = nav.findItem(R.id.profile)
      settings = nav.findItem(R.id.settings2)
      sellers = bottom.findItem(R.id.sellers)
      requests = bottom.findItem(R.id.request)
      string.add(home.title.toString())
      string.add(profile.title.toString())
      string.add(settings.title.toString())
      string.add(sellers.title.toString())
      string.add(requests.title.toString())
      bind.navigationView.setNavigationItemSelectedListener {
         when (it.itemId) {
            R.id.home1 -> {
               bind.drawer.closeDrawers()
            }

            R.id.profile -> {
               bind.drawer.closeDrawers()
               startActivity(Intent(this, ProflieActivity::class.java))
            }

            R.id.settings2 -> {
               startActivity(Intent(this, SettingsForUser::class.java))
               bind.drawer.closeDrawers()
            }
         }

         true
      }
      val ff = ViewModelProvider(this)[LiveResponse::class.java]

      requestsfragement = RequestsFragement()
      Sellersfargment = Sellersfargment(ff)





      fragment(Sellersfargment)
      bind.bottomNavigationView.setOnItemSelectedListener {
         when (it.itemId) {
            R.id.sellers -> {
               fragment(Sellersfargment)
            }

            R.id.request -> {
               fragment(requestsfragement)
            }
         }
         true
      }


      bind.card.setOnClickListener {
         bind.drawer.openDrawer(Gravity.LEFT)
         ff.getData("")
      }
   }

   private fun fragment(fragment: Fragment) {
      supportFragmentManager
         .beginTransaction()
         .replace(R.id.frameLayout, fragment)
         .addToBackStack("back")
         .commit()
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
      val type = getSharedPreferences("user", MODE_PRIVATE).getString("language", "")!!
      when (type) {
         "Tamil" -> {
            translate(TranslateLanguage.TAMIL)
         }

         "Kannada" -> {
            translate(TranslateLanguage.KANNADA)
         }

         "Telugu" -> {
            translate(TranslateLanguage.TELUGU)
         }

         "Hindi" -> {
            translate(TranslateLanguage.HINDI)
         }

         "English" -> {
            normal()
         }
      }

      if (type != "English") {
         onewordchange.observer().observe(this) {
            if (it.size == string.size) {
               home.title = it[0]
               profile.title = it[1]
               settings.title = it[2]
               sellers.title = it[3]
               requests.title = it[4]
            }
         }
      }
   }

   private fun normal() {
      home.title = string[0]
      profile.title = string[1]
      settings.title = string[2]
      sellers.title = string[3]
      requests.title = string[4]
   }

   private fun translate(language: String) {
      dialog.show()
      val option = TranslatorOptions.Builder()
         .setSourceLanguage(TranslateLanguage.ENGLISH)
         .setTargetLanguage(language)
         .build()
      val download = DownloadConditions.Builder().build()
      Translation.getClient(option)
         .downloadModelIfNeeded(download)
         .addOnSuccessListener {
            onewordchange.option(option, string)
            dialog.dismiss()
         }
         .addOnFailureListener {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
         }
   }


}