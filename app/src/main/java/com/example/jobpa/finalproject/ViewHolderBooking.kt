package com.example.jobpa.finalproject

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ViewHolderBooking(v: View): RecyclerView.ViewHolder(v) {

    var textTime = v.findViewById<TextView>(R.id.textTime)
    var textStatus = v.findViewById<TextView>(R.id.textStatus)
    var textCustomer = v.findViewById<TextView>(R.id.textCustomer)
    var textColor = v.findViewById<TextView>(R.id.textStatusColor)
}