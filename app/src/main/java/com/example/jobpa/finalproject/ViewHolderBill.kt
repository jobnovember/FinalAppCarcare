package com.example.jobpa.finalproject

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ViewHolderBill(v: View): RecyclerView.ViewHolder(v) {
    var textTitle = v.findViewById<TextView>(R.id.textTitle)
    var textService = v.findViewById<TextView>(R.id.textService)
    var textSum = v.findViewById<TextView>(R.id.textSum)
    var imgDelete = v.findViewById<ImageView>(R.id.imgDelete)
}