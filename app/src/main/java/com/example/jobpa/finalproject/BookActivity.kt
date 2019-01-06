package com.example.jobpa.finalproject

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class BookActivity : AppCompatActivity() {
    //data
    private var date: String = ""
    private var time: String = ""
    private var uid: String = ""
    private var car_brand : String = ""
    private var car_name: String = ""
    private var car_number: String = ""
    private var mUser:DataBase.User? = null
    private var mCarsList:ArrayList<HashMap<String, Any>>? = null
    private var mCars = ArrayList<DataBase.Car>()

    //view
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTextName: TextView
    private lateinit var mTextDate: TextView
    private lateinit var mTextTime: TextView
    private lateinit var mTextCarBrand: TextView
    private lateinit var mTextCarName: TextView
    private lateinit var mTextCarNumber: TextView

    //firebase
    private val mFirestore = FirebaseFirestore.getInstance()
    //DataRef
    private var userRef = mFirestore.collection("users")
    //Adapter
    private lateinit var  mAdapter: CarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
        date = intent.getStringExtra("date")
        time = intent.getStringExtra("time")
        uid = intent.getStringExtra("uid")
        initView()
    }


    private fun initView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mAdapter = CarAdapter(this!!, mCars)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mTextName = findViewById(R.id.textName)
        mTextDate = findViewById(R.id.textDate)
        mTextTime = findViewById(R.id.textTime)
        mTextCarBrand = findViewById(R.id.textCarBrand)
        mTextCarName = findViewById(R.id.textCarName)
        mTextCarNumber = findViewById(R.id.textCarNumber)

        fetchUser()

        mAdapter.setOnItemClickListener(object: CarAdapter.OnItemClickListener{
            override fun onItemClick(item: View, position: Int) {
                car_brand = mCars[position].brand
                car_name = mCars[position].name
                car_number = mCars[position].number

                mTextName.text = mUser!!.name
                mTextDate.text = date
                mTextTime.text = time
                mTextCarBrand.text = car_brand
                mTextCarName.text = car_name
                mTextCarNumber.text = car_number
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
                       mCars.add(DataBase.Car(brand,name,number))
                       mAdapter.notifyItemInserted(mCars.size-1)
                   }
               }
           }
    }

    private fun show(text:String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun sendResult() {
        intent.putExtra("result","true")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
