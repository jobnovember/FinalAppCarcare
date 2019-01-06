package com.example.jobpa.finalproject

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class BookingAdapter(var mContext: Context, var mItems:List<DataBase.Booking>): RecyclerView.Adapter<ViewHolderBooking>() {

    interface OnItemClickListener {
        fun onItemClick(item: View, position:Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener:OnItemClickListener) {
        mListener = listener
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderBooking {
        var inflater = LayoutInflater.from(mContext)
        var view = inflater.inflate(R.layout.item_booking, p0, false)
        var vHolder = ViewHolderBooking(view)

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

    override fun onBindViewHolder(p0: ViewHolderBooking, p1: Int) {
        var item = mItems[p1]
        p0.textTime.text = item.time
        p0.textStatus.text = item.status
        when(item.status) {
           "empty"-> p0.textStatus.setBackgroundColor(Color.GREEN)
            "waiting"-> p0.textStatus.setBackgroundColor(Color.YELLOW)
            "finish"-> p0.textStatus.setBackgroundColor(Color.RED)
        }
        p0.textCustomer.text = item.customer
    }
}