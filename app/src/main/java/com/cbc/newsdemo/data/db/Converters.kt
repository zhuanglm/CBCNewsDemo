package com.cbc.newsdemo.data.db

import androidx.room.TypeConverter
import com.cbc.newsdemo.data.models.Image
import com.cbc.newsdemo.data.models.Source
import com.cbc.newsdemo.data.models.TypeAttributes
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromImage(image: Image): String{
    return Gson().toJson(image)
    }

    @TypeConverter
    fun toImage(imageJson: String): Image{
    return Gson().fromJson(imageJson, Image::class.java)
    }

    @TypeConverter
    fun fromTypeAttr(typeAttributes: TypeAttributes): String{
        return Gson().toJson(typeAttributes)
    }

    @TypeConverter
    fun toTypeAttr(typeAttr: String): TypeAttributes{
        return Gson().fromJson(typeAttr, TypeAttributes::class.java)
    }
}