package com.example.pickfresh.Admin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import coil.load
import com.example.pickfresh.R
import com.example.pickfresh.Responses.CommonReponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.databinding.ActivityAddSellerBinding
import com.example.pickfresh.toast
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import android.location.Location

class AddSeller : AppCompatActivity() {
    private lateinit var bind:ActivityAddSellerBinding
    var encoded=""

    @SuppressLint("MissingPermission", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind= ActivityAddSellerBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val dialog=Dialog(this).apply {
           setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val image=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
if(it.data!=null){
    val bitmap= it.data!!.extras!!.get("data")as Bitmap
    val  out=ByteArrayOutputStream()
    Toast.makeText(this, "$bitmap", Toast.LENGTH_SHORT).show()
    bitmap.compress(Bitmap.CompressFormat.PNG,100,out)
    encoded=Base64.encodeToString(out.toByteArray(),Base64.NO_WRAP)
    val imageview=bind.imageView2
    imageview.load(bitmap)
    imageview.scaleType=ImageView.ScaleType.CENTER_CROP
    imageview.setPadding(5)

}
        }
        bind.cardview.setOnClickListener {
            image.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }


        val fused=LocationServices.getFusedLocationProviderClient(this)

        bind.btnadd.setOnClickListener { tt ->
            val name=bind.name.text.toString().trim()
            val email=bind.email.text.toString().trim()
            val password=bind.password.text.toString().trim()
            val mobilenumber=bind.mobilenumber.text.toString().trim()

            if(name.isEmpty()){
                tt.toast("Please Enter Seller name")
            }else
            if(email.isEmpty()){
                tt.toast("Please Enter Seller email")
            }else
            if(password.isEmpty()){
                tt.toast("Please Enter Seller password")
            }else
            if(mobilenumber.isEmpty()){
                tt.toast("Please Enter Seller Mobile number")
            }else if(mobilenumber.length!=10){
                tt.toast("Please Enter Valid number of Seller")
            }else if(!email.contains("@gmail.com")){
                tt.toast("Please Enter a  valid Mail-ID")
            }else {
                dialog.show()
                fused.lastLocation.addOnSuccessListener {
                    if(it!=null){
                        val decimalFormat=DecimalFormat("##.#######")
                        val simple=SimpleDateFormat("dd/MMMM/yyy HH:mm")
                        CoroutineScope(IO).launch { Retrofit.instance.addseller(
                            name=name,
                            email=email,
                            password=password,
                            mobilenumber=mobilenumber,
                            encoded=encoded,
                            date=simple.format(Date()),
                            location = "${decimalFormat.format(it.latitude)},${decimalFormat.format(it.longitude)}"
                        ).enqueue(object :Callback<CommonReponse>{
                            override fun onResponse(
                                call: Call<CommonReponse>,
                                response: Response<CommonReponse>
                            ) {
                            dialog.dismiss()
                                response.body().apply {
                                    if(this!=null){
                                        when (message) {
                                            "success" -> {
                                                finish()
                                                tt.toast(message)
                                            }
                                            "already Exists" -> {
                                                bind.textInputLayout.isFocusable=true
                                                bind.textInputLayout.error="Please try with another Email"
                                            }
                                            else -> {
                                                tt.toast(message)
                                            }
                                        }
                                    }else{
                                        Toast.makeText(this@AddSeller, "Body null", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }

                            override fun onFailure(call: Call<CommonReponse>, t: Throwable) {
                               dialog.dismiss()
                                tt.toast(t.message!!)
                            }
                        })}
                    }else{
                        tt.toast("Location is not getting!!")
                        dialog.dismiss()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }



            }
        }

    }




}