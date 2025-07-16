package com.example.pickfresh.Responses

import android.os.Message
import com.example.pickfresh.Model.Review

data class Reviewresponse (var error:Boolean,var message: String,var data:ArrayList<Review>)