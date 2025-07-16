package com.example.pickfresh.Buyer

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pickfresh.Responses.MessagingResponse
import com.example.pickfresh.Responses.Retrofit
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.math.log

class BroadcastReceiver : BroadcastReceiver() {
   private lateinit var location: FusedLocationProviderClient
   override fun onReceive(p0: Context?, p1: Intent?) {
      viewdata(p0!!)
   }

   @SuppressLint("MissingPermission", "Range")
   private fun viewdata(p0: Context) {
      location = LocationServices.getFusedLocationProviderClient(p0)
      val id =
         p0.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE).getString("id", "")!!
      location.lastLocation.addOnSuccessListener { latlon ->
         if (latlon != null) {
            CoroutineScope(IO).launch {
               Retrofit.instance.getmobilenumber(id)
                  .enqueue(object : retrofit2.Callback<MessagingResponse> {
                     override fun onResponse(
                        call: retrofit2.Call<MessagingResponse>,
                        response: Response<MessagingResponse>,
                     ) {
                        response.body().apply {
                           if (this != null) {
                              data.forEach {
                                 val k = it.location.split(",")
                                 if (k.size == 2) {
                                    val locationa = Location("A")
                                    locationa.latitude = k[0].toDouble()
                                    locationa.longitude = k[1].toDouble()
                                    val locationb = Location("B")
                                    locationb.latitude = latlon.latitude
                                    locationb.longitude = latlon.longitude
                                    val o = locationb.distanceTo(locationa) * 0.01 / 1000
                                    Log.i("BrodcastReceiverRetr", "$o")
                                    if (o.toInt() <= 1) {
                                       sendsms(it.mobile, o, latlon)
                                    }
                                 }
                              }
                           }
                        }
                     }

                     override fun onFailure(
                        call: retrofit2.Call<MessagingResponse>,
                        t: Throwable,
                     ) {
                        Log.i("BrodcastReceiverRetr", "${t.message}")
                     }
                  })
            }


            /*Update Locations*/



         }
      }
   }



   private fun sendsms(mobile: String, o: Double, latlon: Location) {
      try {
         val sms = SmsManager.getDefault()
         sms.sendTextMessage(
            mobile,
            null,
            "The Sellet is near your location at radius of $o " +
                    "http://maps.google.com/maps?q=loc:${latlon.latitude},${latlon.longitude}",
            null,
            null
         )
      } catch (e: Exception) {
         Log.i("BrodcastReceiverRetr", "${e.message}")
      }

   }
}