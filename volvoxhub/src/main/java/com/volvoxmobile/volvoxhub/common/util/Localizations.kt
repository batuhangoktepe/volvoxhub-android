package com.volvoxmobile.volvoxhub.common.util

import android.content.Context
import com.volvoxmobile.volvoxhub.common.extensions.resIdByName

object Localizations {

    private val localizationsMap: MutableMap<String, String> = mutableMapOf()

    fun set(localizationsMap: Map<String, String>) {
        this.localizationsMap.clear()
        this.localizationsMap.putAll(localizationsMap)
    }

    fun get(context: Context, key: String): String {
        return if (localizationsMap.containsKey(key)) {
            localizationsMap[key].orEmpty()
        } else {
            try {
                context.getString(context.resIdByName(key, "string"))
            } catch (e: Exception) {
                key
            }
        }
    }

    fun getOrEmpty(key: String): String {
        return tryOrNull { localizationsMap[key] } ?: StringUtils.EMPTY
    }

    fun getOrKey(key: String): String {
        return tryOrNull { localizationsMap[key] } ?: key
    }

    fun getOrDefault(key: String, default: String): String {
        return tryOrNull { localizationsMap[key] } ?: default
    }
}