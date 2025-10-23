package com.example.playlistmaker.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.example.playlistmaker.R

fun showCustomToast(context: Context, message: String) {
    val inflater = LayoutInflater.from(context)
    val layout = inflater.inflate(R.layout.toast, null)
    val textView = layout.findViewById<TextView>(R.id.toastText)

    textView.text = message

    Toast(context).apply {
        duration = Toast.LENGTH_SHORT
        view = layout
        setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 8)
    }.show()
}
