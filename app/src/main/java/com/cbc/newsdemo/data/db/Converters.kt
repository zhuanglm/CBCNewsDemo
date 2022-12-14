package com.cbc.newsdemo.data.db

import androidx.room.TypeConverter
import com.cbc.newsdemo.data.models.Source

class Converters {

    //Convert Source to String
    @TypeConverter
    fun fromSource(source: Source): String{
    return source.name
    }


    //Convert string to Source
    @TypeConverter
    fun toSource(name: String): Source{
    return Source(name, name)
    }
}