package com.snowi.snuzz.utils

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri

object AlarmSoundPlayer {
    private var ringtone: Ringtone? = null

    fun play(context: Context) {
        if (ringtone == null) {
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(context, alarmUri)
        }

        if (ringtone?.isPlaying == false) {
            ringtone?.play()
        }
    }

    fun stop() {
        ringtone?.let {
            if (it.isPlaying) {
                it.stop()
            }
        }
    }
}
