package com.example.pickfresh.Seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pickfresh.Model.Items
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class ViewModel:ViewModel() {
  private var livedata=MutableLiveData<ArrayList<Items>>()

fun data(data:ArrayList<Items>,translatorOptions: TranslatorOptions){

    val download= DownloadConditions.Builder().build()
    val transulator= Translation.getClient(translatorOptions)
    transulator.downloadModelIfNeeded(download)
        .addOnSuccessListener {
            data.forEach {the->
                transulator.translate("Name :"+the.itemname).addOnSuccessListener {
                    the.itemname=it.trim()
                }
                transulator.translate("Status :"+the.status).addOnSuccessListener {
                    the.status=it.trim()
                }
                transulator.translate("price : ₹${the.price}/-").addOnSuccessListener {
                    the.price=it
                    livedata.value=data
                }
            }
        }


}

    fun viewdata():LiveData<ArrayList<Items>>{
return livedata
    }
}