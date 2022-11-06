package com.kjy.recorderproject

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }

    private val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO,
                                                Manifest.permission.READ_EXTERNAL_STORAGE)

    // 녹음된 오디오를 저장할 경로
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    // 녹음기 프로퍼티 선언
    private var recorder: MediaRecorder?= null

    // 미디어 플레이어 프로퍼티 선언
    private var player: MediaPlayer?= null

    private var state = State.BEFORE_RECORDING
        // setter
        set(value) {
            field = value
            recordButton.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 앱을 시작하자마 권한을 요청함.
        requestAudioPermission()
        initViews()
        bindViews()
    }

    // 요청에 대한 결과를 받음
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 권한이 부여가 된것
        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        // 거절을 할경우에는 앱을 종료
        if(!audioRecordPermissionGranted) {
            finish()
        }
    }

    // 권한 요청 메서드
    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }


    // 상태 초기화
    private fun initViews() {
        recordButton.updateIconWithState(state)
    }

    private fun bindViews() {
        recordButton.setOnClickListener {
            when(state) {
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }

    // Recorder 초기화
    /*
    audioSource 지정, outputFormat 지정, Encoder 지정, 녹음물이 저장될곳을 지정.
     */
    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)              // 마이크에서 들어온 source를 AMR_NB 방식으로 압축
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
        state = State.ON_RECORDING
    }

    // 녹음기 멈춤
    private fun stopRecording() {
        // null이 아닐경우 stop() 후 release()
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        state = State.AFTER_RECORDING
    }

    // 재생 기능 구현
    private fun startPlaying() {
        player = MediaPlayer()
            .apply {
                setDataSource(recordingFilePath)
                prepare()
            }
        player?.start()     // 재생
        state = State.ON_PLAYING
    }

    // 정지 기능 구현
    private fun stopPlaying() {
        player?.release()
        player = null
        state = State.AFTER_RECORDING
    }
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}