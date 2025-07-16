package com.example.pickfresh.Admin

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pickfresh.LoginActivity
import com.example.pickfresh.MainActivity
import com.example.pickfresh.R
import com.example.pickfresh.Responses.LoginResponse
import com.example.pickfresh.Responses.Retrofit
import com.example.pickfresh.toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {
    lateinit var cycle:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        findViewById<FloatingActionButton>(R.id.add).setOnClickListener {
startActivity(Intent(this,AddSeller::class.java))
        }
        supportActionBar.apply {
            title="hi Admin!!"
        }
        findViewById<FloatingActionButton>(R.id.logout).setOnClickListener {
            dialog()
        }
    }

    private fun dialog() {
        AlertDialog.Builder(this).apply {
        setTitle("Do you want Logout ?")
            setMessage("Press \"Yes\" to logout or \"No\" Cancel !!")
            setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply()
                finishAffinity()
                startActivity(Intent(this@AdminActivity, MainActivity::class.java))
            }
            setNegativeButton("No"){dialog,_->
                dialog.dismiss()
            }
            show()
        }
    }

    override fun onStart() {
        super.onStart()
        cycle=findViewById(R.id.recyclerView)

        cycle.layoutManager=LinearLayoutManager(this)

        data()

    }

    override fun onResume() {
        super.onResume()
    data()
    }

    private fun data() {
        CoroutineScope(IO).launch {
            Retrofit.instance.getSellers().enqueue(object :Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    response.body().apply {
                        if(this!=null){
                            cycle.adapter=AdapterForSellerList(this@AdminActivity,data)
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                cycle.toast(t.message!!)
                }
            })
        }
    }
}