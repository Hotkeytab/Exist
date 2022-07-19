package com.example.gtm.ui.home.suivie.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gtm.R
import com.example.gtm.data.entities.custom.ArrayListDataX
import com.example.gtm.data.entities.response.suivieplanning.ResponsesOfAllQuestions
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuiviDetailActivity : AppCompatActivity() {

    var afterSuiviArray = ArrayList<ResponsesOfAllQuestions>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suivi_detail)
        val  value : String? = intent.extras?.getString("mainobject")
        val trush = "\"myFinal\""
        val newValue = "{$trush:$value}"

        val gson = Gson()
        val objectList = gson.fromJson(newValue, ArrayListDataX::class.java)

        afterSuiviArray = objectList.myFinal

    }
}