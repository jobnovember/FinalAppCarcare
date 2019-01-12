package com.example.jobpa.finalproject

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class BillAdapter(var mContext: Context, var mItems:List<DataBase.Bill>): RecyclerView.Adapter<ViewHolderBill>()  {

    interface OnItemClickListener {
        fun onItemClick(date:String, time:String, position: Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderBill {
        var inflater = LayoutInflater.from(mContext)
        var view = inflater.inflate(R.layout.item_bill, p0, false)
        return ViewHolderBill(view)
    }

    override fun onBindViewHolder(p0: ViewHolderBill, p1: Int) {
        var item = mItems[p1]
        var text = "รายการ\n"
        p0.textTitle.text = "${item.date} + ${item.time}"

        for(item in mItems[p1].service!!) {
            text += "${item["name"]} ราคา ${item["price"]} \n"
        }

        p0.textService.text = text
        p0.textSum.text = item.sum
        p0.imgDelete.setOnClickListener {
            var p = p0.adapterPosition
            if(p != RecyclerView.NO_POSITION) {
                mListener!!.onItemClick(item.date, item.time, p)
            }
        }
    }
}