package com.example.gtm.utils.extensions

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Patterns
import android.widget.EditText


fun EditText.trimStringEditText(): String {
    return this.text.toString().trim()
}


fun EditText.isValidName(): Boolean = this.text.isNotEmpty() && this.text.length < 30

fun EditText.isValidEmail(): Boolean =
    this.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this.text).matches()

fun EditText.isNumeric(): Boolean =
    this.text.isNotEmpty() && this.text.length == 8 && this.text.all { it in '0'..'9' }

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}
