package com.mobile.app.sdk

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mobile.app.sdk.databinding.ActivityMainBinding
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.mobile.sdk.sister.ThirdPart
import com.mobile.sdk.sister.SisterX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(window) {
            val params = this.attributes
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            attributes = params
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.csmsOpen.setOnClickListener {
            SisterX.show(this, false)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val source = SisterX.component.sisterRepository().token()
            when (source) {
                is Source.Success -> {
                    ThirdPart(
                        "anbey123456",
                        "",
                        "123",
                        source.requireData().token,
                        R.drawable.ic_launcher_background
                    ).also {
                        SisterX.setThirdPart(it)
                        SisterX.show(this@MainActivity, false)
                    }
                }
                is Source.Error -> {
                }
            }.exhaustive
        }
    }
}
