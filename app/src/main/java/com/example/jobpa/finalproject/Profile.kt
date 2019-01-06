package com.example.jobpa.finalproject

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class Profile: Fragment() {
    private lateinit var mTextName: TextView
    private lateinit var mTextNumber: TextView
    private lateinit var mImageAdd: ImageView
    private lateinit var mRecyclerView: RecyclerView
    //Dialog
    private lateinit var mDialogProgressDialog: Dialog
    //Data
    private lateinit var mUid:String
    //Firebase
    private val mFirebaseAuth = FirebaseAuth.getInstance()
    private val mFireStore = FirebaseFirestore.getInstance()
    //DataRef
    private var userRef = mFireStore.collection("users")
    private val brandRef = mFireStore.collection("brand")
    private val carsRef = mFireStore.collection("cars")
    //Data
    private var mUser:DataBase.User? = null
    private var mCarsList:ArrayList<HashMap<String, Any>>? = null
    private var mCars = ArrayList<DataBase.Car>()
    //Adapter
    private lateinit var mAdapterCar: CarAdapter
    companion object {
        fun newInstance(): Profile {
            return Profile()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
        fetchUser()
    }

    private fun initView(v:View) {
        mDialogProgressDialog = ProgressDialog.progressDialog(context!!)
        mTextName = v.findViewById(R.id.textName)
        mTextNumber = v.findViewById(R.id.textNumber)
        mImageAdd = v.findViewById(R.id.imageAdd)
        mRecyclerView = v.findViewById(R.id.recycler_view)
        mAdapterCar = CarAdapter(context!!, mCars)
        mRecyclerView.adapter = mAdapterCar
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        mImageAdd.setOnClickListener {
            createInputDialog()
        }

        mAdapterCar.setOnItemClickListener(object :CarAdapter.OnItemClickListener{
            override fun onItemClick(item: View, position: Int) {

            }

            override fun onLongItemClick(item: View, position: Int) {
                createMenuDialog(position)
            }

        })

        mTextName.setOnClickListener {
            editInput("Edit your name","name")
        }

        mTextNumber.setOnClickListener {
            editInput("Edit your phone number","number")
        }

        mDialogProgressDialog.show()
    }

    private fun show(text:String?) {
       Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    private fun fetchUser() {
        mUid = mFirebaseAuth.currentUser!!.uid
        userRef.document(mUid)
            .get()
            .addOnCompleteListener {task->
                if(task.isSuccessful) {
                    mUser = task.result!!.toObject(DataBase.User::class.java)
                    mTextName.text = mUser!!.name
                    mTextNumber.text = mUser!!.phone
                    mCarsList = mUser!!.cars
                    for(item in mCarsList!!.iterator()) {
                        var brand = item["brand"].toString()
                        var name = item["name"].toString()
                        var number = item["number"].toString()
                        mCars.add(DataBase.Car(brand,name,number))
                        mAdapterCar.notifyItemInserted(mCars.size-1)
                    }
                }
                mDialogProgressDialog.dismiss()
            }.addOnFailureListener {it->
                show(it!!.message.toString())
            }
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
                        mRecyclerView.scrollToPosition(pos)
                        save()
                    }
                    "Edit"-> {
                        createInputDialog(pos)
                    }
                    else-> show("Nothing")
                }
            }
            .setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun createInputDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_car, null)
        var spinBrand = view.findViewById<Spinner>(R.id.spinBrand)
        var spinName = view.findViewById<Spinner>(R.id.spinName)
        var editNumber = view.findViewById<EditText>(R.id.editNumber)

        var carBrand = mutableListOf<String>()
        val carName = mutableListOf<String>()

        var carBrandAdapter = ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item, carBrand)
        var carNameAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, carName)

        var name = ""
        var brand = ""

       mDialogProgressDialog.show()

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
                mDialogProgressDialog.dismiss()
            }else {
                show(task.exception.toString())
            }
        }

        spinBrand.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mDialogProgressDialog.show()
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
                        }
                        spinName.adapter = carNameAdapter
                        mDialogProgressDialog.dismiss()
                    }
                }
            }
        }

        spinName.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                name = carName[position]
            }
        }

        AlertDialog.Builder(context!!)
            .setTitle("Add Your Car")
            .setView(view)
            .setPositiveButton("OK") { dialog, which ->
                mCars.add(DataBase.Car(brand ,name,editNumber.text.toString()))
                mAdapterCar.notifyItemInserted(mCars.size-1)
                mRecyclerView.scrollToPosition(mCars.size-1)
                save()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createInputDialog(pos:Int) {
        val view = layoutInflater.inflate(R.layout.dialog_car, null)
        var spinBrand = view.findViewById<Spinner>(R.id.spinBrand)
        var spinName = view.findViewById<Spinner>(R.id.spinName)
        var editNumber = view.findViewById<EditText>(R.id.editNumber)
        var carBrand = mutableListOf<String>()
        val carName = mutableListOf<String>()

        var carBrandAdapter = ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item, carBrand)
        var carNameAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, carName)

        var name = mCars[pos].name
        var brand = mCars[pos].brand
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
            }
        }

        AlertDialog.Builder(context!!)
            .setTitle("Edit Cars")
            .setView(view)
            .setPositiveButton("OK") { dialog, which ->
                mCars[pos] = DataBase.Car(brand,name, editNumber.text.toString())
                mAdapterCar.notifyItemChanged(pos)
                mRecyclerView.scrollToPosition(pos)
                save()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun editInput(title:String,type:String) {
        val view = layoutInflater.inflate(R.layout.dialog_edit, null)
        var textTitle = view.findViewById<TextView>(R.id.textTitle)
        var editData = view.findViewById<EditText>(R.id.editData)
        when(type) {
            "name"->{
                editData.inputType = InputType.TYPE_CLASS_TEXT
                editData.text = Editable.Factory.getInstance().newEditable(mTextName.text)
            }
            "number"->{
                editData.inputType = InputType.TYPE_CLASS_PHONE
                editData.text = Editable.Factory.getInstance().newEditable(mTextNumber.text)
            }
            else-> {

            }
        }
        AlertDialog.Builder(context!!)
            .setTitle(title)
            .setView(view)
            .setPositiveButton("OK") { dialog, which ->
                when(type) {
                    "name"-> {
                        mTextName.text = editData.text.toString()
                    }
                    "number"->{
                        mTextNumber.text = editData.text.toString()
                    }
                    else-> {show("Error")}
                }
                save()
            }
            .setNegativeButton("Cancel") {dialog, which ->
                dialog.dismiss()
            }.show()
    }

    private fun save() {
        if(DataBase().addUser(mUid, mTextName.text.toString(),mTextNumber.text.toString(),mCars)) {
            show("saved")
        }
    }


}

