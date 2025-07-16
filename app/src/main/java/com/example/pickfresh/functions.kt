package com.example.pickfresh

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.time.Duration


fun View.toast(message:Any){
        Toast.makeText(context, "$message", Toast.LENGTH_SHORT).show()
    }
@SuppressLint("ResourceAsColor")
fun View.snackbar(message: Any){
    Snackbar.make(this,"$message",Snackbar.LENGTH_SHORT).apply {
        setAction("Action") {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }

        show()
    }

}

