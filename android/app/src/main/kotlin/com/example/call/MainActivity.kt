package com.example.call

import android.content.Context
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Environment
import android.preference.PreferenceManager
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : FlutterActivity() {
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        val CHANNEL = "recordChannel"
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "startRecord") {
                startRecord("file")
                result.success(true)
                if (true) {
                    result.success(true)
                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }
            } else if (call.method == "stopRecord") {
                stopRecording()
                if (true) {
                    result.success(true)
                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }
            } else {
                result.notImplemented()
            }
            // Note: this method is invoked on the main thread.
            // TODO
        }
    }

    private fun startRecord(name: String) {
        val SP = PreferenceManager.getDefaultSharedPreferences(context)
        val source = SP.getString("RECORDER", "4")!!.toInt()
        val sampleDir: File
        val dir = ""
        sampleDir = if (dir.isEmpty()) {
            File(Environment.getExternalStorageDirectory().absolutePath, "/CallRecorder")
        } else {
            File(dir)
        }
        if (!sampleDir.exists()) {
            sampleDir.mkdirs()
        }
        try {
            audiofile = File.createTempFile(name, ".3gpp", sampleDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        when (source) {
            0 -> recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            1 -> {
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                audioManager = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager!!.setStreamVolume(3, audioManager!!.getStreamMaxVolume(3), 0)
                audioManager!!.isSpeakerphoneOn = true
            }
            2 -> recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            3 -> recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
            4 -> recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
            5 -> recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
            else -> recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
        }
        recorder.setAudioSamplingRate(8000)
        recorder.setAudioEncodingBitRate(12200)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(audiofile!!.absolutePath)
        try {
            recorder.prepare()
            recorder.start()
            record = true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (record) {
            recorder.stop()
        }
        if (audioManager != null) {
            audioManager!!.isSpeakerphoneOn = false
        }
    }

    companion object {
        var recorder = MediaRecorder()
        var audioManager: AudioManager? = null
        var audiofile: File? = null
        var record = false
    }
}




