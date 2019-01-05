package com.example.jobpa.finalproject

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@TargetApi(Build.VERSION_CODES.O)
class Booking: Fragment() {
    private lateinit var textDate: TextView
    private lateinit var btnDate: Button
    private lateinit var mDate :DatePickerDialog.OnDateSetListener
    private lateinit var mRecyclerView: RecyclerView
    private val mCalendar = Calendar.getInstance()
    private var mDatePicked:String? = ""
    private lateinit var dialogProgress: Dialog
    //Firebase
    private val mFireStore = FirebaseFirestore.getInstance()
    //DataRef
    private var t1 = mFireStore.collection("booking")
    //ArrayList
    private var mBookList: ArrayList<DataBase.Booking>? = null
    //ArrayAdapter
    private lateinit var mAdapter:BookingAdapter

    companion object {
        fun newInstance():Booking {
            return Booking()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
    }

    private fun initView(v:View) {
        textDate = v.findViewById(R.id.textDate)
        btnDate = v.findViewById(R.id.btnDate)
        dialogProgress = ProgressDialog.progressDialog(context!!)
        mRecyclerView = v.findViewById(R.id.recycler_view)
        mBookList = ArrayList()
        mAdapter = BookingAdapter(context!!, mBookList!!)
        mRecyclerView.adapter = mAdapter

        mRecyclerView.layoutManager = LinearLayoutManager(context)

        dialogProgress.show()
        setCurrentTime()

        mDate = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            dialogProgress.show()
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            mDatePicked = "$dayOfMonth/${month+1}/$year"
            textDate.text = mDatePicked
            fetchBooking()
        }

        btnDate.setOnClickListener {
            DatePickerDialog(context,
                mDate,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setCurrentTime() {
        var sdf = SimpleDateFormat("d/M/yyyy")
        var currentDate = sdf.format(Date())
        mDatePicked = currentDate
        textDate.text = currentDate
        fetchBooking()
    }

    private fun fetchBooking() {
        mBookList!!.clear()
        mAdapter = BookingAdapter(context!!, mBookList!!)
        mRecyclerView.adapter = mAdapter
        var times = arrayListOf("8.00","8.30","9.00","9.30","10.00","10.30","11.00","11.30","12.00"
            ,"12.30","13.00","13.30","14.00","14.30","15.00","15.30","16.00")
        for(time in times) {
            t1.whereEqualTo("date",mDatePicked)
                .whereEqualTo("time",time)
                .get()
                .addOnCompleteListener { task->
                    if(task.isSuccessful) {
                        val document = task.result.documents
                        for(item in document) {
                            var date = time
                            var time = item.data!!["time"].toString()
                            var status = item.data!!["status"].toString()
                            var customer = item.data!!["customer"].toString()
                            var car = item.data!!["car"].toString()
                            mBookList!!.add(DataBase.Booking(date,time,status,customer,car))
                            mAdapter.notifyItemInserted(mBookList!!.size-1)
                        }
                    }else {
                        mBookList!!.add(DataBase.Booking(mDatePicked!!, time,"ว่าง","",""))
                        mAdapter.notifyItemInserted(mBookList!!.size-1)
                    }
                }
        }
        show(mBookList.toString())
        dialogProgress.dismiss()
    }

    private fun show(text:String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}