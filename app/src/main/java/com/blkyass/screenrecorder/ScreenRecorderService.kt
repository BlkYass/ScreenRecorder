package com.blkyass.screenrecorder

import android.app.*
import android.content.Intent
import android.media.*
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Environment
import android.os.IBinder
import java.io.File

class ScreenRecorderService : Service() {

    private lateinit var mediaProjection: MediaProjection
    private lateinit var mediaRecorder: MediaRecorder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val code = intent?.getIntExtra("code", -1) ?: return START_NOT_STICKY
        val data = intent.getParcelableExtra<Intent>("data")!!

        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = manager.getMediaProjection(code, data)

        startForeground(1, createNotification())

        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "recording.mp4"
        )

        mediaRecorder = MediaRecorder()
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.REMOTE_SUBMIX)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setOutputFile(file.absolutePath)
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setVideoSize(720, 1280)
        mediaRecorder.setVideoFrameRate(30)
        mediaRecorder.prepare()

        val surface = mediaRecorder.surface
        mediaProjection.createVirtualDisplay(
            "ScreenRecorder",
            720,
            1280,
            resources.displayMetrics.densityDpi,
            0,
            surface,
            null,
            null
        )

        mediaRecorder.start()

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "recorder_channel"
        val channel = NotificationChannel(
            channelId,
            "Screen Recorder",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        return Notification.Builder(this, channelId)
            .setContentTitle("Recording Screen")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
