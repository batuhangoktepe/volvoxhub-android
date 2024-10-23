package com.volvoxmobile.volvoxhub.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalizationTypeConverter {
    private val gson by lazy { Gson() }

    @TypeConverter
    fun localizationsToJson(localizations: Map<String, String>): String = gson.toJson(localizations)

    @TypeConverter
    fun jsonToLocalizations(value: String): Map<String, String>? = gson.fromJson(value, object : TypeToken<Map<String, String>>() {}.type)
}
