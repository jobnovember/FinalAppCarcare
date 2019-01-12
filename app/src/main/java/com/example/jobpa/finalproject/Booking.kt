package com.example.jobpa.finalproject

import android.annotation.TargetApi
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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
    private val mFirebaseAuth = FirebaseAuth.getInstance()
    //DataRef
    private var t1 = mFireStore.collection("booking")
    //ArrayList
    private var mBookList: ArrayList<DataBase.Booking>? = null
    //ArrayAdapter
    private lateinit var mAdapter:BookingAdapter
    //Activity Code
    private val BOOK:Int = 1

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == BOOK) {
           when(resultCode) {
               Activity.RESULT_OK -> {
                   var result = data!!.getStringExtra("result")
                   //result
                   show(result)
               }
           }
        }
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
        setCurrentTime()
        fetchBooking()

        mDate = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            mDatePicked = "$dayOfMonth/${month+1}/$year"
            textDate.text = mDatePicked
            fetchBooking()
        }
        listenDataChang()
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
    }

    private fun fetchBooking() {
        mBookList!!.clear()
        mBookList = ArrayList()
        mAdapter = BookingAdapter(context!!, mBookList!!)
        mRecyclerView.adapter = mAdapter
        var times = arrayListOf("8.00","8.30","9.00","9.30"
            ,"10.00","10.30","11.00","11.30","12.00"
            ,"12.30","13.00","13.30","14.00","14.30"
            ,"15.00","15.30","16.00")
        for((index,time) in times.withIndex()) {
            mBookList!!.add(DataBase.Booking(mDatePicked!!, time,"empty","-",""))
            mAdapter.notifyItemInserted(mBookList!!.size-1)
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
                            mBookList!![index] = DataBase.Booking(date,time,status,customer,car)
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                }.addOnSuccessListener {

                }
            }
        mAdapter.setOnItemClickListener(object: BookingAdapter.OnItemClickListener{
            override fun onItemClick(item: View, position: Int) {
                if(mFirebaseAuth.currentUser == null) {
                   show("Please Login First")
                }
                else if(mBookList!![position].status != "empty") {
                    show("ไม่สามารถจองเวลานี้ได้")
                }
                else if(mBookList!![position].status == "empty") {
                    var uid = mFirebaseAuth.currentUser!!.uid
                    var date = mBookList!![position].date
                    var time = mBookList!![position].time
                    book(date, time, uid)
                }
            }
        })

        if(!mBookList!!.isEmpty()) {
        }
    }

    private fun listenDataChang() {
        var times = arrayListOf("8.00","8.30","9.00","9.30"
            ,"10.00","10.30","11.00","11.30","12.00"
            ,"12.30","13.00","13.30","14.00","14.30"
            ,"15.00","15.30","16.00")
        for((index,time) in times.withIndex()) {
            t1.whereEqualTo("date",mDatePicked)
                .whereEqualTo("time",time)
                .addSnapshotListener { snapshotes, e ->
                    for(dc in snapshotes!!.documentChanges) {
                        when(dc.type) {
                            DocumentChange.Type.ADDED -> {
                                fetchBooking()
                            }
                            DocumentChange.Type.MODIFIED -> {
                                fetchBooking()
                            }
                            DocumentChange.Type.REMOVED -> {
                                fetchBooking()
                            }
                        }
                    }

                }
        }

    }

    private fun show(text:String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    private fun book(date:String ,time:String, uid:String) {
        var intent = Intent(context, BookActivity::class.java)
        intent.putExtra("date",date)
        intent.putExtra("time",time)
        intent.putExtra("uid",uid)
        startActivityForResult(intent, BOOK)
    }


}