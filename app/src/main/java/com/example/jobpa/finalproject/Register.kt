package com.example.jobpa.finalproject

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Register: Fragment() {
    private lateinit var mEditEmail: EditText
    private lateinit var mEditPassword: EditText
    private lateinit var mEditConfirm: EditText
    private lateinit var mEditName: EditText
    private lateinit var mEditPhone: EditText
    private lateinit var mImageAdd: ImageView
    private lateinit var mBtnRegister: Button
    private lateinit var mRecyClerView: RecyclerView
    //firebase
    private var mFirebaseAuth = FirebaseAuth.getInstance()
    private val mFireStore = FirebaseFirestore.getInstance()
    //Reference
    private val brandRef = mFireStore.collection("brand")
    private val carsRef = mFireStore.collection("cars")
    //adapter
    private lateinit var mAdapterCar: CarAdapter
    //data
    private var mCars = ArrayList<DataBase.Car>()
    //progress dialog
    private lateinit var progressDialog:Dialog

    companion object {
        fun newInstance():Register {
            return Register()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)

    }

    private fun initView(v: View) {
        mEditEmail = v.findViewById(R.id.editEmail)
        mEditPassword = v.findViewById(R.id.editPassword)
        mEditConfirm = v.findViewById(R.id.editConfirm)
        mEditName = v.findViewById(R.id.editName)
        mEditPhone = v.findViewById(R.id.editPhone)
        mImageAdd = v.findViewById(R.id.imageAdd)
        mRecyClerView = v.findViewById(R.id.recycler_view)
        mAdapterCar = CarAdapter(context!!, mCars)
        mBtnRegister = v.findViewById(R.id.btnRegister)
        progressDialog = ProgressDialog.progressDialog(context!!)

        mRecyClerView.adapter = mAdapterCar
        mRecyClerView.layoutManager = LinearLayoutManager(context)

        mImageAdd.setOnClickListener {
            createInputDialog()
        }

        mBtnRegister.setOnClickListener {
            register()
        }

        mAdapterCar.setOnItemClickListener(object:CarAdapter.OnItemClickListener{
            override fun onItemClick(item: View, position: Int) {
                createMenuDialog(position)
            }

            override fun onLongItemClick(item: View, position: Int) {
                createMenuDialog(position)
            }
        })
    }

    private fun register() {
        progressDialog.show()
        var email = mEditEmail.text.toString()
        var password = mEditPassword.text.toString()
        var confirm = mEditConfirm.text.toString()
        var name = mEditName.text.toString()
        var phone = mEditPhone.text.toString()
        if(!TextUtils.isEmpty(email)
            && !TextUtils.isEmpty(password)
            && !TextUtils.isEmpty(confirm)
            && !TextUtils.isEmpty(name)
            && !TextUtils.isEmpty(phone)){
            if(password == confirm && !mCars.isEmpty()) {
               mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                   .addOnCompleteListener {
                      if(it.isSuccessful) {
                          var uid = mFirebaseAuth.uid!!
                          DataBase().addUser(uid, name, phone, mCars)
                          //register success
                          gotoSecondActivity()
                      }else {
                          show("Can't register")
                          progressDialog.dismiss()
                      }
                   }
            }else {
                show("Password is not Match")
                progressDialog.dismiss()
            }
        }else {
            show("Can't register")
            progressDialog.dismiss()
        }
    }

    private fun show(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    private fun createMenuDialog(pos:Int){
        val items = arrayOf("Delete","Edit")
        AlertDialog.Builder(context!!)
            .setTitle("Menu")
            .setItems(items) { dialog, which ->
                when(items[which]) {
                    "Delete"->{
                        mCars.removeAt(pos)
                        mAdapterCar.notifyItemRemoved(pos)
                        mRecyClerView.scrollToPosition(pos)
                    }
                    "Edit"-> createInputDialog(pos)
                    else-> show("Nothing")
                }
            }
            .setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun createInputDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_car, null)
        val spinBrand = view.findViewById<Spinner>(R.id.spinBrand)
        val spinName = view.findViewById<Spinner>(R.id.spinName)
        val editNumber = view.findViewById<EditText>(R.id.editNumber)

        val carBrand = mutableListOf<String>()
        val carName = mutableListOf<String>()
        val carType = mutableListOf<String>()

        val carBrandAdapter = ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item, carBrand)
        val carNameAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, carName)

        var name = ""
        var brand = ""
        var type = ""

        progressDialog.show()

        brandRef.document("brand").get().addOnCompleteListener { task->
            if(task.isSuccessful) {
                val document = task.result!!
                val data = document.data
                val snapshot: Map<String, Any> = HashMap(data)
                var args = snapshot["name"] as? ArrayList<String>

                for(item in args!!) {
                   carBrand.add(item)
                }
                spinBrand.adapter = carBrandAdapter
                progressDialog.dismiss()
            }else {
                show(task.exception.toString())
            }
        }

        spinBrand.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                progressDialog.show()
                brand = carBrand[position]
                carsRef.document("data").get().addOnCompleteListener {
                    if(it.isSuccessful) {
                        carName.clear()
                        carType.clear()
                        val document = it.result!!
                        val data = document.data
                        val snapshot = HashMap(data)
                        val args = snapshot[brand] as? ArrayList<HashMap<String, Any>>
                        for(item in args!!) {
                            carName.add(item["name"].toString())
                            carType.add(item["type"].toString())
                        }
                        spinName.adapter = carNameAdapter
                        progressDialog.dismiss()
                    }
                }
            }
        }

        spinName.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                name = carName[position]
                type = carType[position]
            }
        }

        AlertDialog.Builder(context!!)
            .setTitle("Add Your Car")
            .setView(view)
            .setPositiveButton("OK") { dialog, which ->
                if(type ==""){
                    type="test"
                }
                mCars.add(DataBase.Car(brand ,name,editNumber.text.toString(),type))
                mAdapterCar.notifyItemInserted(mCars.size-1)
                mRecyClerView.scrollToPosition(mCars.size-1)
                mAdapterCar.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createInputDialog(pos:Int) {
        val view = layoutInflater.inflate(R.layout.dialog_car, null)
        val spinBrand = view.findViewById<Spinner>(R.id.spinBrand)
        val spinName = view.findViewById<Spinner>(R.id.spinName)
        val editNumber = view.findViewById<EditText>(R.id.editNumber)

        val carBrand = mutableListOf<String>()
        val carName = mutableListOf<String>()
        val carType = mutableListOf<String>()

        val carBrandAdapter = ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item, carBrand)
        val carNameAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, carName)

        var name = mCars[pos].name
        var brand = mCars[pos].brand
        var type = mCars[pos].type

        editNumber.text = Editable.Factory.getInstance().newEditable(mCars[pos].number)
        brandRef.document("brand").get().addOnCompleteListener { task->
            if(task.isSuccessful) {
                val document = task.result!!
                val data = document.data
                val snapshot: Map<String, Any> = HashMap(data)
                var args = snapshot["name"] as? ArrayList<String>

                for(item in args!!) {
                    carBrand.add(item)
                }
                spinBrand.adapter = carBrandAdapter
                var brandPosition = carBrandAdapter.getPosition(brand)
                spinBrand.setSelection(brandPosition)
            }else {
                show(task.exception.toString())
            }
        }

        spinBrand.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                brand = carBrand[position]
                carsRef.document("data").get().addOnCompleteListener {
                    if(it.isSuccessful) {
                        carName.clear()
                        val document = it.result!!
                        val data = document.data
                        val snapshot = HashMap(data)
                        val args = snapshot[brand] as? ArrayList<HashMap<String, Any>>
                        for(item in args!!) {
                            carName.add(item["name"].toString())
                            carType.add(item["type"].toString())
                        }
                        spinName.adapter = carNameAdapter
                    }
                }
            }
        }

        spinName.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                name = carName[position]
                type = carType[position]
            }
        }

        AlertDialog.Builder(context!!)
            .setTitle("Edit Cars")
            .setView(view)
            .setPositiveButton("OK") { dialog, which ->
                mCars[pos] = DataBase.Car(brand,name, editNumber.text.toString(), type)
                mAdapterCar.notifyItemChanged(pos)
                mRecyClerView.scrollToPosition(pos)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun gotoSecondActivity() {
        activity!!.finish()
        var i = Intent(context, SecondActivity::class.java)
        startActivity(i)
    }
}

