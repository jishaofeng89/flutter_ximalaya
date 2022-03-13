package com.ximalaya.flutter.flutter_ximalaya

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ximalaya.ting.android.miyataopensdk.IntentXmUISdk

class ProxyActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IntentXmUISdk.getInstance().startMain(applicationContext)
    }
}