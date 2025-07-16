package com.example.pickfresh.Responses

import com.example.pickfresh.Model.Orders

data class OrderResponse (var error:Boolean,var message:String,var data:ArrayList<Orders>)