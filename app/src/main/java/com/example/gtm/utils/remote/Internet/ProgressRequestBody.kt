package com.example.gtm.utils.remote.Internet

import android.os.Handler
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.IOException
import android.os.Looper
import android.util.Log
import java.io.FileInputStream


class ProgressRequestBody(file: File, content_type: String?, listener: UploadCallbacks) :
    RequestBody() {
    private var mFile: File? = null
    private val mPath: String? = null
    private var mListener: UploadCallbacks? = null
    private var content_type: String? = null
    private val DEFAULT_BUFFER_SIZE = 2048

    init {
        this.content_type = content_type
        mFile = file
        mListener = listener
    }

    override fun contentType(): MediaType? {
        return "$content_type/*".toMediaTypeOrNull()
    }


    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile!!.length()
    }


    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile!!.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val `in` = FileInputStream(mFile)
        var uploaded: Long = 0
        try {
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (`in`.read(buffer).also { read = it } != -1) {

                // update progress on UI thread
                handler.post(ProgressUpdater(uploaded, fileLength, mListener))
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        } finally {
            `in`.close()
        }
    }

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)
        fun onError()
        fun onFinish(finished:Boolean)
    }


    private class ProgressUpdater(
        private val mUploaded: Long,
        private val mTotal: Long,
        private val lisstener: UploadCallbacks?,
    ) :
        Runnable {
        override fun run() {
            lisstener!!.onProgressUpdate((100 * mUploaded / mTotal).toInt())

        }

    }



}






