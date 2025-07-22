// File: AudioVisualizerViewModel.kt
package com.snowi.snuzz.presentation.features.home

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowi.snuzz.data.repository.UserPreferencesRepository
import com.snowi.snuzz.utils.AlarmHelper
import com.snowi.snuzz.utils.AlarmSoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.sqrt

@HiltViewModel
class AudioVisualizerViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> = _amplitude

    private val _decibel = MutableStateFlow(0f)
    val decibel: StateFlow<Float> = _decibel

    private val _isAlarmPlaying = MutableStateFlow(false)
    val isAlarmPlaying: StateFlow<Boolean> = _isAlarmPlaying

    private val _decibelHistory = MutableStateFlow<List<Float>>(emptyList())
    val decibelHistory: StateFlow<List<Float>> = _decibelHistory

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var hasTriggeredAlarm = false

    @RequiresApi(Build.VERSION_CODES.O)
    fun startRecording(context: Context) {
        if (recordingJob != null) return

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        audioRecord?.startRecording()

        recordingJob = viewModelScope.launch(Dispatchers.IO) {
            val buffer = ShortArray(bufferSize)

            while (isActive) {
                val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (readSize > 0) {
                    val rms = calculateRMS(buffer, readSize)
                    val db = 20 * log10(rms / 32768.0).toFloat().coerceAtLeast(-90f) + 90f

                    _amplitude.value = rms
                    _decibel.value = db
                    updateDecibelHistory(db)

                    val preferences = repository.getPreferences().getOrDefault(emptyList())
                    val threshold = preferences.firstOrNull { it.tag == "Office" }?.thresholdDecibel ?: 70

                    if (db > threshold && !hasTriggeredAlarm) {
                        hasTriggeredAlarm = true
                        withContext(Dispatchers.Main) {
                            _isAlarmPlaying.value = true
                            AlarmSoundPlayer.play(context)
                            AlarmHelper.showAlarmNotification(context, db)
                        }
                    } else if (db < threshold - 10) {
                        hasTriggeredAlarm = false
                    }
                }

                delay(1000L)
            }
        }
    }

    fun stopAlarm() {
        _isAlarmPlaying.value = false
        AlarmSoundPlayer.stop()
    }

    fun stopRecording() {
        recordingJob?.cancel()
        recordingJob = null
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun updateDecibelHistory(newDecibel: Float) {
        val updated = _decibelHistory.value.toMutableList()
        updated.add(newDecibel)
        if (updated.size > 60) updated.removeFirst()
        _decibelHistory.value = updated
    }

    private fun calculateRMS(buffer: ShortArray, readSize: Int): Int {
        var sum = 0L
        for (i in 0 until readSize) {
            sum += buffer[i] * buffer[i]
        }
        return sqrt(sum / readSize.toDouble()).toInt()
    }

    override fun onCleared() {
        super.onCleared()
        stopRecording()
    }
}
