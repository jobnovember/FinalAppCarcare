package com.example.jobpa.finalproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView

class ServiceAdapter(context:Context, layout:Int, arrayList:ArrayList<DataBase.Service>): ArrayAdapter<DataBase.Service>(context, layout, arrayList){
    private var mContext:Context? = null
    private var mArrayList: ArrayList<DataBase.Service>? = null
    private var mLayout: Int? = null

    init {
        this.mContext = context
        this.mLayout = layout
        this.mArrayList = arrayList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        var inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if(view == null) {
            view = inflater.inflate(mLayout!!, parent, false)
        }
        var item = mArrayList!![position]
        var textName = view!!.findViewById<TextView>(R.id.textName)
        var textPrice = view!!.findViewById<TextView>(R.id.textPrice)
        var textDescription = view!!.findViewById<TextView>(R.id.textDescription)
        var checkbox = view!!.findViewById<CheckBox>(R.id.checkbox)

        textName.text = item.name
        textPrice.text = item.price
        textDescription.text = item.description
        checkbox.tag = position
        checkbox.isChecked = item.checked

        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            var tag = buttonView.tag as Int
            var item = getItem(tag)
            item.checked = isChecked
        }

        return view
    }

    fun getCheckedItems(): ArrayList<DataBase.Service> {
        var checkedItems = ArrayList<DataBase.Service>()
        for((index,item) in mArrayList!!.withIndex()) {
            if(mArrayList!![index].checked) {
               checkedItems.add(mArrayList!![index])
            }
        }
        return checkedItems
    }


}