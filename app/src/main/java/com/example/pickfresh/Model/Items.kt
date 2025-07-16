package com.example.pickfresh.Model

import android.os.Parcel
import android.os.Parcelable

data class Items(
    var id:Int,
    var itemname:String,
    var sellerid:String,
    var itemphoto:String,
    var price:String,
    var status:String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(itemname)
        parcel.writeString(sellerid)
        parcel.writeString(itemphoto)
        parcel.writeString(price)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Items> {
        override fun createFromParcel(parcel: Parcel): Items {
            return Items(parcel)
        }

        override fun newArray(size: Int): Array<Items?> {
            return arrayOfNulls(size)
        }
    }
}