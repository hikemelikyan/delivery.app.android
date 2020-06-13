package com.hmelikyan.deliveryapp.shared.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable

object ImageUtil {
    fun createBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable != null) {
            try {
                val bitmap: Bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                return bitmap
            } catch (e: OutOfMemoryError) {
            }
        }
        return null
    }
}