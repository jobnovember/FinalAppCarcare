package com.example.jobpa.finalproject

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CarAdapter(var mContext: Context,var mItems:List<DataBase.Car>): RecyclerView.Adapter<ViewHolderCar>() {

    interface OnItemClickListener {
        fun onItemClick(item: View, position:Int)
        fun onLongItemClick(item: View, position:Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener:OnItemClickListener) {
        mListener = listener
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int):ViewHolderCar {
        var inflater = LayoutInflater.from(mContext)
        var view = inflater.inflate(R.layout.item_car, p0, false)
        var vHolder = ViewHolderCar(view)

        view.setOnLongClickListener {
            var pos = vHolder.adapterPosition
            if(mListener != null) {
                if(pos != RecyclerView.NO_POSITION) {
                    mListener!!.onLongItemClick(view, pos)
                }
            }
            return@setOnLongClickListener true
        }
        view.setOnClickListener {
            var pos = vHolder.adapterPosition
            if(mListener != null) {
                if(pos != RecyclerView.NO_POSITION) {
                    mListener!!.onItemClick(view, pos)
                }
            }
        }
        return vHolder
    }

    override fun onBindViewHolder(p0: ViewHolderCar, p1: Int) {
        var item = mItems[p1]
        p0.textBrand.text = item.brand
        p0.textName.text = item.name
        p0.textNumber.text = item.number
    }

}