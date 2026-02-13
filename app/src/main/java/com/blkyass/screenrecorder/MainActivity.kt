package com.blkyass.screenrecorder

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blkyass.screenrecorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var projectionManager: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        binding.btnStart.setOnClickListener {
            startActivityForResult(
                projectionManager.createScreenCaptureIntent(),
                1000
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            val serviceIntent = Intent(this, ScreenRecorderService::class.java)
            serviceIntent.putExtra("code", resultCode)
            serviceIntent.putExtra("data", data)
            startForegroundService(serviceIntent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
