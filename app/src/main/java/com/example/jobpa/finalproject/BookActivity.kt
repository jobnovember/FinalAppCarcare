package com.example.jobpa.finalproject

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.jobpa.finalproject.ProgressDialog.Companion.progressDialog
import com.google.firebase.firestore.FirebaseFirestore

class BookActivity : AppCompatActivity() {
    //data
    private var date: String = ""
    private var time: String = ""
    private var uid: String = ""
    private var car_brand : String = ""
    private var car_name: String = ""
    private var car_number: String = ""
    private var car_type: String = ""
    private var mUser:DataBase.User? = null
    private var mCarsList:ArrayList<HashMap<String, Any>>? = null
    private var mCars = ArrayList<DataBase.Car>()
    private var mServiceList = ArrayList<DataBase.Service>()
    private var mBill = ArrayList<DataBase.Service>()
    //view
    private lateinit var mRecyclerView: RecyclerView
    //firebase
    private val mFirestore = FirebaseFirestore.getInstance()
    //DataRef
    private var userRef = mFirestore.collection("users")
    private var serviceRef = mFirestore.collection("service")
    //Adapter
    private lateinit var  mAdapter: CarAdapter
    private lateinit var  mServiceAdapter: ServiceAdapter
    //Dialog
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
        date = intent.getStringExtra("date")
        time = intent.getStringExtra("time")
        uid = intent.getStringExtra("uid")
        initView()
    }


    private fun initView() {
        //Adapter
        mAdapter = CarAdapter(this!!, mCars)
        mServiceAdapter = ServiceAdapter(this!!, R.layout.item_service, mServiceList)
        //View
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mProgressDialog = ProgressDialog.progressDialog(this)
        fetchUser()

        mAdapter.setOnItemClickListener(object: CarAdapter.OnItemClickListener{
            override fun onItemClick(item: View, position: Int) {
                car_brand = mCars[position].brand
                car_name = mCars[position].name
                car_number = mCars[position].number
                car_type = mCars[position].type
                dialogService()
            }
            override fun onLongItemClick(item: View, position: Int) {
            }
        })
    }

    private fun fetchUser() {
       userRef.document(uid)
           .get()
           .addOnCompleteListener { task->
               if(task.isSuccessful) {
                   mUser = task.result!!.toObject(DataBase.User::class.java)
                   mCarsList = mUser!!.cars
                   for(item in mCarsList!!.iterator()) {
                       var brand = item["brand"].toString()
                       var name = item["name"].toString()
                       var number = item["number"].toString()
                       var type = item["type"].toString()
                       mCars.add(DataBase.Car(brand,name,number,type))
                       mAdapter.notifyItemInserted(mCars.size-1)
                   }
               }
           }
    }

    private fun fetchService() {
        mProgressDialog.show()
        mServiceList.clear()
        serviceRef
            .whereEqualTo("type",car_type)
            .get()
            .addOnCompleteListener { task->
                if(task.isSuccessful) {
                    var document = task.result!!.documents
                    for(item in document) {
                        var name = item["name"].toString()
                        var price = item["price"].toString()
                        var description = item["description"].toString()
                        mServiceList.add(DataBase.Service(name, price, description))
                    }
                    mProgressDialog.dismiss()
                    mServiceAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun dialogService() {
        fetchService()
        val view = layoutInflater.inflate(R.layout.dialog_service, null)
        var listView = view.findViewById<ListView>(R.id.listView)
        var sum:Int = 0
        listView.adapter = mServiceAdapter
        AlertDialog.Builder(this)
            .setTitle("Select Service")
            .setView(view)
            .setPositiveButton("OK") { dialog, which ->
                for(item in mServiceAdapter.getCheckedItems()) {
                    if(item.checked) {
                        mBill.add(item)
                        sum += item.price.toInt()
                    }
                }
                dialog.dismiss()
                //when finished
                DataBase().addBill(uid, mUser!!.name, car_name, sum.toString(), date, time, mBill)
                sendResult("true")
            }.setNegativeButton("Cancel") {dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun show(text:String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun sendResult(text:String) {
        intent.putExtra("result",text)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}
