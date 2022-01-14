package com.example.gtm.utils.extensions

import android.widget.EditText


fun EditText.trimStringEditText(): String {
    return this.text.toString().trim()
}