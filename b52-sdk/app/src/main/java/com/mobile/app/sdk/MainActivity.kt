package com.mobile.app.sdk

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mobile.app.sdk.databinding.ActivityMainBinding
import com.mobile.sdk.sister.SisterX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testSisterX()
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
            SisterX.setUsername(binding.editUsername.text.toString())
            lifecycleScope.launch(Dispatchers.Default) {
                delay(3000)
                withContext(Dispatchers.Main) {
                    binding.editUsername.setText("")
                    SisterX.show(this@MainActivity, false)
                }
            }
        }
    }
}
