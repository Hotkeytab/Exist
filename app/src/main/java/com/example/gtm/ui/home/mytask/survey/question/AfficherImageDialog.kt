package com.example.gtm.ui.home.mytask.survey.question

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.gtm.R
import dagger.hilt.android.AndroidEntryPoint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.gtm.BuildConfig
import com.example.gtm.data.entities.ui.Image
import kotlinx.android.synthetic.main.dialog_afficher_image.*
import kotlinx.android.synthetic.main.dialog_choix_image.*
import kotlinx.android.synthetic.main.dialog_internet_connection.*
import kotlinx.android.synthetic.main.fragment_question.*
import java.io.File
import java.security.Permission


@AndroidEntryPoint
class AfficherImageDialog(
    position2: Int,
    listaImage2: ArrayList<Image>,
    linearImage2: LinearLayout,
    plus_image2: CardView,
    adapterImage2: ImageAdapter,
    recycle_view2: RecyclerView

) :
    DialogFragment() {


    private var position = position2
    private val listaImage = listaImage2
    private val linearImage = linearImage2
    private val plus_image = plus_image2
    private val adapterImage = adapterImage2
    private val recycle_view = recycle_view2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_afficher_image, container, false)
    }

    override fun onStart() {
        super.onStart()

        val width = (resources.displayMetrics.widthPixels).toInt()
        val height = (resources.displayMetrics.heightPixels).toInt()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        afficher_image.setImageBitmap(listaImage[position].url)
        Log.i("imageposition", position.toString())

        return_from_dialog.setOnClickListener {
            dismiss()
        }

        left_arrow.setOnClickListener {
            if (position != 0 && listaImage.size != 1) {
                position--
                afficher_image.setImageBitmap(listaImage[position].url)
            }
        }

        right_arrow.setOnClickListener {
            if (listaImage.size - 1 != position && listaImage.size != 1) {
                position++
                afficher_image.setImageBitmap(listaImage[position].url)
            }

        }

        delete.setOnClickListener {
            if (listaImage.size == 1) {
                listaImage.removeAt(0)
                linearImage.visibility = View.VISIBLE
                plus_image.visibility = View.GONE
                adapterImage.setItems(listaImage)
                recycle_view.visibility = View.GONE
                dismiss()

            } else if (listaImage.size > 1) {
                if (position == 0) {

                    listaImage.removeAt(0)
                    afficher_image.setImageBitmap(listaImage[position].url)
                    adapterImage.setItems(listaImage)

                } else if (position == listaImage.size - 1) {

                    listaImage.removeAt(listaImage.size - 1)
                    position --
                    afficher_image.setImageBitmap(listaImage[position].url)
                    adapterImage.setItems(listaImage)
                } else {
                    listaImage.removeAt(position)
                    afficher_image.setImageBitmap(listaImage[position].url)
                    adapterImage.setItems(listaImage)
                }
            } else
                dismiss()
        }

    }


}


