package com.example.pickfresh.Buyer

import android.app.Dialog
import android.bluetooth.le.AdvertiseData
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pickfresh.Model.Orderid
import com.example.pickfresh.R
import com.example.pickfresh.Responses.OrderResponseonlyid
import com.example.pickfresh.Responses.Retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class View_Pendings : AppCompatActivity() {
lateinit var dialog: Dialog
 lateinit var  data2:ArrayList<Orderid>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pendings)
data2=ArrayList()
        val cycle= findViewById<RecyclerView>(R.id.cycle2)
        cycle.layoutManager=LinearLayoutManager(this)
        dialog=Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.show()
        val id=getSharedPreferences("user", MODE_PRIVATE).getString("id","")
val type=intent.getStringExtra("type")
        CoroutineScope(IO).launch {
            if(type=="acceptedbyseller"){
Retrofit.instance.getorderview(condition = "viewaccepted",id="$id").enqueue(object :Callback<OrderResponseonlyid>{
    override fun onResponse(
        call: Call<OrderResponseonlyid>,
        response: Response<OrderResponseonlyid>
    ) {
        response.body().apply {
            if(this!=null){
                data2=data
                dataview(cycle,"accepted")
            }
        }
        dialog.dismiss()
    }

    override fun onFailure(call: Call<OrderResponseonlyid>, t: Throwable) {
    dialog.dismiss()
        Toast.makeText(this@View_Pendings, "${t.message}", Toast.LENGTH_SHORT).show()
    }
})

            }else if(type=="seller"){
    Retrofit.instance.getorderbyseing(condition = "viewbuyerrequest","$id").enqueue(object :Callback<OrderResponseonlyid>{
        override fun onResponse(
            call: Call<OrderResponseonlyid>,
            response: Response<OrderResponseonlyid>
        ) {
            response.body().apply {
                if(this!=null){
                    data2=data
                    dataview(cycle, string = "seller")
                }
            }

            dialog.dismiss()
        }

        override fun onFailure(call: Call<OrderResponseonlyid>, t: Throwable) {
        dialog.dismiss()
        }
    })
}else if(type=="viewselleraccepted"){
    Retrofit.instance.getaccepted(condition ="viewacceptedbyseller","$id").enqueue(object :Callback<OrderResponseonlyid>{
        override fun onResponse(
            call: Call<OrderResponseonlyid>,
            response: Response<OrderResponseonlyid>
        ) {
            response.body().apply {
                if(this!=null){
                    data2=data
                    dataview(cycle, string = "buyer") } }
            dialog.dismiss() }

        override fun onFailure(call: Call<OrderResponseonlyid>, t: Throwable) {
        dialog.dismiss()
            Toast.makeText(this@View_Pendings, "${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}else if(type=="viewcompleted"){
                Retrofit.instance.getaccepted(condition ="viewcompleted","$id").enqueue(object :Callback<OrderResponseonlyid>{
                    override fun onResponse(
                        call: Call<OrderResponseonlyid>,
                        response: Response<OrderResponseonlyid>
                    ) {
                        response.body().apply {
                            if(this!=null){
                                data2=data
                                dataview(cycle, string = "completed") }
                        }
                        dialog.dismiss() }

                    override fun onFailure(call: Call<OrderResponseonlyid>, t: Throwable) {
                        dialog.dismiss()
                        Toast.makeText(this@View_Pendings, "${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
}else {
    Retrofit.instance.getpendings(condition = "viewbyids", id = "$id")
        .enqueue(object : Callback<OrderResponseonlyid> {
            override fun onResponse(
                call: Call<OrderResponseonlyid>,
                response: Response<OrderResponseonlyid>
            ) {
                response.body().apply {
                    if (this != null) {
                        data2=data
                        dataview(cycle,string ="buyer")
                    }
                }
                dialog.dismiss()
            }

            override fun onFailure(call: Call<OrderResponseonlyid>, t: Throwable) {
                Toast.makeText(this@View_Pendings, "${t.message}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })
}
}
    }

    private fun dataview(cycle: RecyclerView,string:String) {
        cycle.layoutManager=LinearLayoutManager(this)
        cycle.adapter=AdapterForPendings(this,data2,string)
    }
}