package com.example.jobpa.finalproject

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login: Fragment() {
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var dialogProgress: Dialog
    //Firebase
    private val mFirebaseAuth = FirebaseAuth.getInstance()
    companion object {
        fun newInstance(): Login {
           return Login()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
    }

    private fun initView(v:View) {

        editEmail = v.findViewById(R.id.editEmail)
        editPassword = v.findViewById(R.id.editPassword)
        btnLogin = v.findViewById(R.id.btnLogin)
        dialogProgress = ProgressDialog.progressDialog(context!!)
        btnLogin.setOnClickListener {
            dialogProgress.show()
            var email = editEmail.text.toString()
            var password = editPassword.text.toString()
            if(!email.isEmpty() && !password.isEmpty()) {
                mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task->
                    if(task.isSuccessful) {
                        dialogProgress.dismiss()
                        gotoSecondActivity()
                    }else {
                        dialogProgress.dismiss()
                        show(task.exception?.message.toString())
                    }
                }
            }else {
                dialogProgress.dismiss()
                show("Email and Password can't empty")
            }
        }
    }

    private fun show(text:String) {
        Toast.makeText(context,text,Toast.LENGTH_LONG).show()
    }

    private fun gotoSecondActivity() {
        activity!!.finish()
        var i = Intent(context, SecondActivity::class.java)
        startActivity(i)
    }
}

