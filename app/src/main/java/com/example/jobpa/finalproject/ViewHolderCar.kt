package com.example.jobpa.finalproject

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ViewHolderCar(v: View): RecyclerView.ViewHolder(v){
    var textBrand: TextView = v.findViewById(R.id.TextBrand)
    var textName: TextView = v.findViewById(R.id.TextName)
    var textNumber: TextView = v.findViewById(R.id.TextNumber)
}