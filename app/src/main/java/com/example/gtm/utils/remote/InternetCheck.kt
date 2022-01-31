package com.example.gtm.utils.remote

import android.os.AsyncTask
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

internal class InternetCheck(private val onInternetChecked: (Boolean) -> Unit) :
    AsyncTask<Void, Void, Boolean>() {
        init {
            execute()
        }


    override fun doInBackground(vararg p0: Void?): Boolean {
        return try {
            val sock = Socket()
            sock.connect(InetSocketAddress("8.8.8.8",53),5000)
            sock.close()
            true
        } catch (e:IOException) {
            false
        }
    }


    override fun onPostExecute(internet: Boolean) {
        onInternetChecked(internet)
    }

}
