package com.example.pickfresh.Responses


import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface   Api {
  @FormUrlEncoded
 @POST("user.php")
  fun signup(
  @Field("name")name:String,
  @Field("email")email:String,
  @Field("mobile")mobile:String,
  @Field("password")password:String,
  @Field("location")location:String
):Call<CommonReponse>
  @FormUrlEncoded
  @POST("user.php")
  fun login(
      @Field("condition")condition:String,
   @Field("email")email:String,
   @Field("password")password:String,
   ):Call<LoginResponse>

  @FormUrlEncoded
  @POST("addseller.php")
    fun addseller(
      @Field("name")name:String,
      @Field("email")email:String,
      @Field("password")password:String,
      @Field("mobilenumber")mobilenumber:String,
      @Field("encoded")encoded:String,
      @Field("date")date:String,
      @Field("location")location:String
  ):Call<CommonReponse>

    @GET("getdata.php")
    fun getSellers():Call<LoginResponse>
    @FormUrlEncoded
    @POST("additems.php")
    fun additems(
        @Field("itemname")itemname:String,
        @Field("sellerid")sellerid:String,
        @Field("itemphoto")itemphoto:String,
        @Field("price")price:String,

    ):Call<CommonReponse>

    @FormUrlEncoded
    @POST("getdata.php")
     fun getitems(
        @Field("condition")condition:String,
        @Field("id")id: String):Call<ProductResponse>

     @FormUrlEncoded
     @POST("updatefun.php")
    fun update(
         @Field("id")id:String,
         @Field("state")state:String,
         @Field("cost")cost:String
    ):Call<CommonReponse>

    @FormUrlEncoded
    @POST("updatefun.php")
    fun stateupdate(
        @Field("id")id:String,
        @Field("condition")condition:String,
        @Field("state")state:String
    ):Call<CommonReponse>

    @FormUrlEncoded
    @POST("getdata.php")
    fun getuser(
        @Field("id")id:String,
        @Field("condition")condition:String
    ):Call<LoginResponse>
@FormUrlEncoded
@POST("getdata.php")
    fun getData(
   @Field("condition") condition: String,
   @Field("search")search: String
):Call<LoginResponse>
@FormUrlEncoded
@POST("getdata.php")
    fun getitemsbyid(
        @Field("id")id:String,
        @Field("condition")condition:String
    ):Call<GoodResponse>

    @FormUrlEncoded
    @POST("insertdata.php")
    fun orderitem(

        @Field("userid")userid:String,
        @Field("orderid")orderid:String,
        @Field("sellerid")sellerid:String,
        @Field("status")status:String,
        @Field("itemphoto")itemphoto:String,
        @Field("itemname")itemname:String,
        @Field("qty")qty:String,
        @Field("price")price:String,
        @Field("date")date:String):Call<CommonReponse>
@FormUrlEncoded
@POST("getdata.php")
    fun getpendings(
    @Field("condition")condition:String,
    @Field("id")id:String
    ):Call<OrderResponseonlyid>

    @FormUrlEncoded
    @POST("getdata.php")
    fun getorderitems(
        @Field("condition")condition:String,
        @Field("id")id:String
    ):Call<OrderResponse>

    @FormUrlEncoded
    @POST("getdata.php")
    fun getorderbyseing(
        @Field("condition")condition:String,
        @Field("id")id:String
    ):Call<OrderResponseonlyid>

    @FormUrlEncoded
    @POST("getdata.php")
    fun getbuyerdetails(
        @Field("id")id:String,
        @Field("condition")condition:String
    ):Call<OrderResponse>

    @FormUrlEncoded
    @POST("updatefun.php")
    fun updaterequest(
        @Field("condition")condition:String,
        @Field("id")id:String,
        @Field("state")state:String
    ):Call<CommonReponse>

    @FormUrlEncoded
    @POST("getdata.php")
    fun getorderview(
        @Field("condition")condition:String,
        @Field("id")id:String
    ):Call<OrderResponseonlyid>

    @FormUrlEncoded
    @POST("getdata.php")
     fun getuserdeatils(
        @Field("condition")condition: String,
        @Field("id") id: String
                       ):Call<LoginResponse>

     @FormUrlEncoded
     @POST("updatefun.php")
    fun updatefun(
         @Field("condition")condition:String,
         @Field("id")id:String,
         @Field("state")state:String
    ):Call<CommonReponse>

    @FormUrlEncoded
    @POST("getdata.php")
    fun getaccepted(
        @Field("condition")condition: String,
        @Field("id")id:String,
    ):Call<OrderResponseonlyid>

    @FormUrlEncoded
    @POST("getdata.php")
    fun getorderdetails(
        @Field("condition")condition:String,
        @Field("id")id:String
    ):Call<CustomeResponse>

    @FormUrlEncoded
    @POST("orderreview.php")
    fun addreview(
        @Field("rating")rating:String,
        @Field("review")review:String,
        @Field("buyerid")buyerid:String,
        @Field("productid")productid:String,
    ):Call<CommonReponse>

    @FormUrlEncoded
    @POST("ratings.php")
    fun getrating(
        @Field("id")id:String,
    ):Call<Reviewresponse>

    @FormUrlEncoded
    @POST("getsmsdata.php")
    fun getmobilenumber(
        @Field("id") id:String
    ):Call<MessagingResponse>

    @FormUrlEncoded
    @POST("getuser.php")
    fun getuser(
        @Field("id")id:String
    ):Call<LoginResponse>
@FormUrlEncoded
@POST("update.php")
     fun updateorder(@Field("id")id: String):Call<CommonReponse>

     @GET("updateLocations.php")
     suspend fun updateLocations(
        @Query("id")id:String,
        @Query("location")location:String,
        @Query("address")address:String
     ):Response<CommonReponse>


}

