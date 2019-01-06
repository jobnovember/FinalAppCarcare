package com.example.jobpa.finalproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SecondActivity : AppCompatActivity() {
    private lateinit var mDrawerLayout: DrawerLayout
    private val mFirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }
        initView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
       return when (item!!.itemId) {
           android.R.id.home -> {
               mDrawerLayout.openDrawer(GravityCompat.START)
               true
           }
           else -> super.onOptionsItemSelected(item)
       }
    }

    private fun initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem->
            menuItem.isChecked = true
            when(menuItem.itemId) {
                R.id.nav_booking-> {
                    var fragment = Booking.newInstance()
                    repalceFragment(fragment)
                }
                R.id.nav_profile-> {
                    var fragment = Profile.newInstance()
                    repalceFragment(fragment)
                }
                R.id.nav_logout-> {
                    logout()
                }
                else-> {
                    show("else")
                }
            }
            mDrawerLayout.closeDrawers()
            true
        }
        var fragment = Booking.newInstance()
        repalceFragment(fragment)
    }

    private fun show(text:String) {
        Toast.makeText(applicationContext,text,Toast.LENGTH_LONG).show()
    }

    private fun logout() {
        mFirebaseAuth.signOut()
        var i = Intent(applicationContext, MainActivity::class.java)
        finish()
        startActivity(i)
    }

    private fun repalceFragment(fragment:Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit()
    }
}
