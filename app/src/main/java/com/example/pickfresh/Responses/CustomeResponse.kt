package com.example.pickfresh.Responses

import com.example.pickfresh.Model.Orders
import com.example.pickfresh.Model.Review

data class CustomeResponse (var error:Boolean,var message:String,var data:ArrayList<Orders>,
var data2:ArrayList<Review>)