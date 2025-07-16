package com.example.pickfresh.Buyer

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.pickfresh.Model.LiveResponse
import com.example.pickfresh.Model.User
import com.example.pickfresh.R
import com.example.pickfresh.databinding.ActivitySellersfargmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Sellersfargment(val liveResponse: LiveResponse) : Fragment(), OnMapReadyCallback {
   private lateinit var bind: ActivitySellersfargmentBinding
   lateinit var maps: SupportMapFragment
   lateinit var dialog: Dialog
   var user = ArrayList<User>()
   override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?,
   ): View {
      bind = ActivitySellersfargmentBinding.inflate(inflater)
      maps = SupportMapFragment.newInstance()



      bind.searchQuery.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
         override fun onQueryTextSubmit(query: String?): Boolean {
            liveResponse.getData(query.toString())
            return true
         }

         override fun onQueryTextChange(newText: String?): Boolean {
            liveResponse.getData(newText.toString())
            return true
         }
      })

      childFragmentManager.beginTransaction().add(R.id.maps, maps).commit()
      maps.getMapAsync(this)

      dialog = Dialog(requireActivity()).apply {
         setContentView(R.layout.progressdi)
         setCancelable(false)
         window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      }
      return bind.root
   }

   @SuppressLint("UseCompatLoadingForDrawables")
   override fun onMapReady(p0: GoogleMap) {
      var num = 0
      liveResponse.user().observe(requireActivity()) { it ->
         num = 0
         p0.clear()
         user = it
         for (user in it) {
            val k = user.location.split(",")
            if (k.size == 2) {
               val platoon = LatLng(k[0].toDouble(), k[1].toDouble())
               val marker = MarkerOptions().title(user.name).position(platoon)
                  .icon(function(requireContext().getDrawable(R.drawable.pointer)!!))
               p0.addMarker(marker)
               if (num == 0) {
                  p0.animateCamera(CameraUpdateFactory.newLatLngZoom(platoon, 12f))
               }
               num++
            }
         }

      }

      p0.setOnMarkerClickListener { tt ->
         user.forEach {
            if (tt.title == it.name) {
               Intent(requireContext(), ViewItems::class.java).apply {
                  putExtra("data", it)
                  requireContext().startActivity(this)
               }
            }
         }
         true
      }
   }

   private fun function(pointer: Drawable): BitmapDescriptor {
      val canvas = Canvas()
      val bitmap = Bitmap.createBitmap(
         pointer.intrinsicWidth,
         pointer.intrinsicHeight,
         Bitmap.Config.ARGB_8888
      )
      canvas.setBitmap(bitmap)
      pointer.setBounds(0, 0, pointer.intrinsicWidth, pointer.intrinsicHeight)
      pointer.draw(canvas)
      return BitmapDescriptorFactory.fromBitmap(bitmap)
   }

   override fun onDestroy() {
      super.onDestroy()
      maps.onDestroy()
   }

   override fun onStart() {
      super.onStart()
      liveResponse.getData("")
   }

   override fun onResume() {
      super.onResume()
      liveResponse.getData("")
   }

}