package com.example.pickfresh.Responses

import android.util.Base64
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
     val base="https://wizzie.online/PickFresh/"
    val AUTH="Basic"+Base64.encodeToString("sss".toByteArray(),Base64.NO_WRAP)

    val api=okhttp3.OkHttpClient.Builder()
        .addInterceptor {chain->
        val request=chain.request()
            val build=request.newBuilder()
    .method(request.method,request.body)
    .addHeader("Authotization", AUTH)

        val requ=build.build()
            chain.proceed(requ)
        }.build()
    val instance : Api by lazy  {
val retofit=Retrofit.Builder()
            .baseUrl(base)
            .addConverterFactory(GsonConverterFactory.create())
    .client(api)
    .build()
retofit.create(Api::class.java)
    }
    }
