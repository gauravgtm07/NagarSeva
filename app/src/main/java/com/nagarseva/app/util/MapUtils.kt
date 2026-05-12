package com.nagarseva.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object MapUtils {
    
    fun openMapsWithLocation(
        context: Context,
        latitude: Double,
        longitude: Double,
        label: String = "Reported Defect"
    ) {
        // Try Google Maps first with geo URI
        // geo:lat,lng?q=lat,lng(label)
        val geoUri = Uri.parse(
            "geo:$latitude,$longitude?" +
            "q=$latitude,$longitude" +
            "(${Uri.encode(label)})"
        )
        
        val mapsIntent = Intent(
            Intent.ACTION_VIEW, geoUri)
        mapsIntent.setPackage(
            "com.google.android.apps.maps")
        
        if (mapsIntent.resolveActivity(
            context.packageManager) != null) {
            // Google Maps is installed
            context.startActivity(mapsIntent)
        } else {
            // Google Maps not installed
            // Fall back to browser with 
            // Google Maps URL
            val browserUri = Uri.parse(
                "https://www.google.com/maps/" +
                "search/?api=1&query=" +
                "$latitude,$longitude"
            )
            val browserIntent = Intent(
                Intent.ACTION_VIEW, browserUri)
            
            try {
                context.startActivity(browserIntent)
            } catch (e: ActivityNotFoundException) {
                // No browser either — show toast
                Toast.makeText(
                    context,
                    "No maps app found. " +
                    "Please install Google Maps.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    fun getStaticMapUrl(
        latitude: Double,
        longitude: Double,
        zoom: Int = 16
    ): String {
        // OpenStreetMap tile URL for static preview
        // No API key needed
        return "https://www.openstreetmap.org/" +
               "export/embed.html" +
               "?bbox=${longitude - 0.005}," +
               "${latitude - 0.005}," +
               "${longitude + 0.005}," +
               "${latitude + 0.005}" +
               "&layer=mapnik" +
               "&marker=$latitude,$longitude"
    }
}
