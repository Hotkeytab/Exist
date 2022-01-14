package com.example.gtm.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gtm.R
import com.example.gtm.ui.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        logout.setOnClickListener {
            val intent = Intent(this,AuthActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }

    }
}