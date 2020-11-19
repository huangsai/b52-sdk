package com.mobile.app.sdk

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mobile.app.sdk.databinding.ActivityMainBinding
import com.mobile.guava.android.mvvm.Msg
import com.mobile.sdk.sister.SisterX

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // testSisterX()
    }

    private fun testSisterX() {
        with(window) {
            val params = this.attributes
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            attributes = params
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.csmsOpen.setOnClickListener {
            SisterX.setUser(binding.editUsername.text.toString())
            SisterX.show(this@MainActivity, false)
            binding.editUsername.setText("")
        }

        binding.csmsReopen.setOnClickListener {
            if (SisterX.hasUser) {
                SisterX.show(this@MainActivity, false)
            } else {
                Msg.toast("请先登录一次用户，点击\"打开客服\"按钮")
            }
        }
    }
}
