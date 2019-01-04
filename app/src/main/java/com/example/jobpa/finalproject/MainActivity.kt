package com.example.jobpa.finalproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mPagerAdapter: PagerAdapter
    private lateinit var mPager: ViewPager
    private lateinit var mTabLayout: TabLayout
    //Firabse
    private val mFirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(mFirebaseAuth.currentUser != null) {
            gotoSecondActivity()
        }
        initView()
    }

    private fun initView() {
        mPagerAdapter = PagerAdapter(supportFragmentManager)
        mPager = findViewById(R.id.view_pager)
        mPager.adapter = mPagerAdapter
        mTabLayout = findViewById(R.id.tabLayout)
        mTabLayout.setupWithViewPager(mPager)
    }

    private fun gotoSecondActivity() {
        this.finish()
        var i = Intent(this, SecondActivity::class.java)
        startActivity(i)
    }
}
