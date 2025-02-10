package com.example.newsagencyproject


import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryManager {
    fun init(context: Context) {
        val config = mapOf(
            "cloud_name" to "dhkqbcmkv",
            "api_key" to "727251129765957",
            "api_secret" to "yI0DFDvb_eXBkp_ZN4Khxm51O9g"
        )
        MediaManager.init(context, config)
    }
}
