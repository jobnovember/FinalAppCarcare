package com.example.jobpa.finalproject

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Bill: Fragment() {
    private lateinit var mBillList: ArrayList<DataBase.Bill>
    private var mBill:DataBase.Bill ?= null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: BillAdapter
    //firebase
    private val mFirebaseAuth = FirebaseAuth.getInstance()
    private val mFireStore = FirebaseFirestore.getInstance()
    //ref
    private val billRef = mFireStore.collection("bills")
    companion object {
        fun newInstance():Bill { return Bill()}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
        fetchBill()
    }

    private fun initView(v:View) {
        mBillList = ArrayList()
        mRecyclerView = v.findViewById(R.id.recycler_view)
        mAdapter = BillAdapter(context!!, mBillList)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context!!)

        mAdapter.setOnItemClickListener(object: BillAdapter.OnItemClickListener{
            override fun onItemClick(date: String, time: String, position: Int) {
                mBillList.removeAt(position)
                mAdapter.notifyItemRemoved(position)
                delete(date, time)
            }
        })
    }

    private fun fetchBill() {
        var uid = mFirebaseAuth.currentUser!!.uid
        billRef
            .whereEqualTo("uid",uid)
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    var bill = task.result!!.toObjects(DataBase.Bill::class.java)
                    for(item in bill) {
                        mBillList.add(item)
                    }
                    mAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun delete(date:String, time:String) {
        billRef
            .whereEqualTo("date",date)
            .whereEqualTo("time",time)
            .get()
            .addOnCompleteListener {
                var document = it.result
                for(item in document) {
                    var id = item.id
                    billRef.document(id).delete()
                }
            }
    }

    private fun show(text:String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }




}