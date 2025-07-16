package com.example.pickfresh.Responses

import com.example.pickfresh.Model.Items

data class ProductResponse (var error:Boolean,var message:String,var data:ArrayList<Items> )