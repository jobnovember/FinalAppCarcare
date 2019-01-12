package com.example.jobpa.finalproject

import com.google.firebase.firestore.FirebaseFirestore

class DataBase {
    private var db = FirebaseFirestore.getInstance()

    data class Car(var brand: String="", var name: String="", var number: String="", var type: String="")

    data class User(var name: String = "", var phone: String = "", var cars: ArrayList<HashMap<String, Any>>? = null)

    data class Booking(var date: String="",var time: String=""
                       , var status: String="", var customer: String =""
                       , var uid: String = ""
                       , var cars: String ="", var sum:String =""
                       , var service: ArrayList<HashMap<String, Any>>? = null)

    data class Service(var name: String="", var price: String="", var description: String="", var checked: Boolean = false)

    data class Bill(var date:String="", var time:String="", var car:String="", var sum:String=""
                    , var service: ArrayList<HashMap<String, Any>>? =null, var uid: String ="") {

    }

    fun addUser(uid: String, name: String, phone: String, cars: ArrayList<DataBase.Car>): Boolean {
        val usersRef = db.collection("users")
        var carsList = ArrayList<HashMap<String, Any>>()

        for (item in cars) {
            var result = HashMap<String, Any>()
            result["brand"] = item.brand
            result["name"] = item.name
            result["number"] = item.number
            result["type"] = item.type
            carsList.add(result)
        }
        var data = User(name, phone, carsList)
        usersRef.document(uid).set(data).addOnSuccessListener {
        }
        return true
    }

    fun addBill(uid: String, name: String,car: String, sum: String, date: String, time: String, service: ArrayList<DataBase.Service>) {
        val bookingRef = db.collection("booking")
        val billRef = db.collection("bills")

        var billList = ArrayList<HashMap<String, Any>>()

        for(item in service) {
            var result = HashMap<String, Any>()
            result["name"] = item.name
            result["description"] = item.description
            result["price"] = item.price
            billList.add(result)
        }

        val data = DataBase.Booking(date, time,"waiting",name,uid,car,sum,billList)
        bookingRef.add(data).addOnSuccessListener {
            var id = it.id
            val data = HashMap<String, Any>()
            data["uid"] = uid
            data["car"] = car
            data["date"] = date
            data["time"] = time
            data["sum"] = sum
            data["service"] = billList
            billRef.document(id).set(data).addOnSuccessListener {  }
        }
        .addOnFailureListener {

        }

    }
}