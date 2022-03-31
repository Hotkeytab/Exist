package com.example.gtm.ui.home.kpi.kpifilter



import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.view.*



@AndroidEntryPoint
class ProgressUploadDialog(

) :
    DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_progress_upload, container, false)
    }

    override fun onStart() {
        super.onStart()

        val width = (resources.displayMetrics.widthPixels )
        val height = (resources.displayMetrics.heightPixels)
        dialog!!.window?.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.setCancelable(false)

    }

}

