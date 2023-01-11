package com.example.htbeyond.util

import android.content.Context
import android.content.Intent
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.htbeyond.R
import com.example.htbeyond.databinding.MyBottomSheetBinding
import com.example.htbeyond.view.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyWebAppInterface(private val mContext: Context) {

    @JavascriptInterface
    fun showToast(toast: String){
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun showPopup(toast: String){
        (mContext as AppCompatActivity).runOnUiThread {
            val bottomSheetView = (mContext as AppCompatActivity).layoutInflater.inflate(R.layout.my_bottom_sheet, null)
            val bottomSheetDialog = BottomSheetDialog(mContext)
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }

    @JavascriptInterface
    fun showLoginActivity(){
        ContextCompat.startActivity(mContext, Intent(mContext, LoginActivity::class.java), null)
    }

}