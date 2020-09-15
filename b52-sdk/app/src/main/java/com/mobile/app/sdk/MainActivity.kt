package com.mobile.app.sdk

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mobile.app.sdk.databinding.ActivityMainBinding
import com.mobile.sdk.sister.SisterLib

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(window) {
            val wmLayoutParams = this.attributes
            wmLayoutParams.flags = wmLayoutParams.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            attributes = wmLayoutParams
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.csmsOpen.setOnClickListener {
            SisterLib.show(this, false)
        }

        SisterLib.show(this, false)
    }
}
