package com.example.pickfresh.Model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class OrderLiveData:ViewModel() {
    private var dataview=MutableLiveData<ArrayList<Orders>>()
fun getdata(option :TranslatorOptions,data:ArrayList<Orders>){
    val o=Translation.getClient(option)
data.forEach {the->
    o.translate("Item Name : ${the.itemname}").addOnSuccessListener {
the.itemname=it
    }
    o.translate("Price ₹ ${the.price}/-").addOnSuccessListener {
the.price=it
    }
    o.translate("Quantity :"+the.qty).addOnSuccessListener {
the.qty=it
    }
    o.translate("Status :"+the.status).addOnSuccessListener {
the.status=it
    }
    o.translate("Order date : "+the.date).addOnSuccessListener {
        the.date=it
        dataview.value=data
    }
}
}
    fun viewdata()=dataview
}