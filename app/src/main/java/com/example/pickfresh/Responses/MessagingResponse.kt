package com.example.pickfresh.Responses

import com.example.pickfresh.Model.User

data class MessagingResponse (var error:Boolean,var message:String,var data: ArrayList<User>)