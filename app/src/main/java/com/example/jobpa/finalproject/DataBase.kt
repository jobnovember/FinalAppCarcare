package com.example.jobpa.finalproject

import com.google.firebase.firestore.FirebaseFirestore

class DataBase {
    private var db = FirebaseFirestore.getInstance()
    private var usersRef = db.collection("users")
    private var carsList = ArrayList<HashMap<String, Any>>()

    data class Car(var brand: String, var name: String, var number: String)
    data class User(var name: String = "", var phone: String = "", var cars: ArrayList<HashMap<String, Any>>? = null)
    data class Booking(var date: String="",var time: String="", var status: String="", var customer: String ="", var cars: String ="")

    fun addUser(uid: String, name: String, phone: String, cars: ArrayList<DataBase.Car>): Boolean {
        for (item in cars) {
            var result = HashMap<String, Any>()
            result["brand"] = item.brand
            result["name"] = item.name
            result["number"] = item.number
            carsList.add(result)
        }
        var data = User(name, phone, carsList)
        usersRef.document(uid).set(data).addOnSuccessListener {
        }
        return true
    }

    fun getUser(uid: String): String?{
        var msg:String? = null
        var user = db.collection("users").document(uid)
        user.get()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    var data = it.result.data
                    msg = data!!["name"].toString()
                }
            }
        return msg
    }
}