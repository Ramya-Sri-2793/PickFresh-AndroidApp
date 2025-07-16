package com.example.pickfresh.Responses

import com.example.pickfresh.Model.Items
import com.example.pickfresh.Model.Seconditem

data class GoodResponse (var error:Boolean,var messsage:String,var data:ArrayList<Items>,
var second:ArrayList<Seconditem>)