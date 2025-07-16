package com.example.pickfresh.Responses

import com.example.pickfresh.Model.Orderid

data class OrderResponseonlyid (var id:Boolean,var message:String,
var data:ArrayList<Orderid>)