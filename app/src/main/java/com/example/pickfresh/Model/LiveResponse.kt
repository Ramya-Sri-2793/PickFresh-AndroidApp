package com.example.pickfresh.Model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.pickfresh.Responses.LoginResponse
import com.example.pickfresh.Responses.Retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LiveResponse:androidx.lifecycle.ViewModel() {
    private var userresponse=MutableLiveData<ArrayList<User>>()


    fun getData(search:String){
        CoroutineScope(IO).launch {
            Retrofit.instance.getData(condition = "getdatabytrue",search=search).enqueue(object :Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    response.body().apply {
                        if(this!=null){
                                userresponse.value = data
                                Log.i("LiveResponse123", "$data")
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.i("LiveResponse123","${t.message}")
                }
            })
        }
    }
    fun user()=userresponse
}
