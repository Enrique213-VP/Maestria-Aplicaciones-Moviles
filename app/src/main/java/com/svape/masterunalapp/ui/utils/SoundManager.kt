package com.svape.masterunalapp.ui.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.media.AudioManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.svape.masterunalapp.R

class SoundManager(
    private val context: Context,
    lifecycleOwner: LifecycleOwner
) {
    private var humanMediaPlayer: MediaPlayer? = null
    private var computerMediaPlayer: MediaPlayer? = null
    private var toneGenerator: ToneGenerator? = null
    private var isSoundEnabled = true
    private var useCustomSounds = true

    init {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> initializeSoundPlayers()
                    Lifecycle.Event.ON_PAUSE -> releaseSoundPlayers()
                    Lifecycle.Event.ON_DESTROY -> releaseSoundPlayers()
                    else -> {}
                }
            }
        })
    }

    private fun initializeSoundPlayers() {
        if (useCustomSounds) {
            initializeMediaPlayers()
        } else {
            initializeToneGenerator()
        }
    }

    private fun initializeMediaPlayers() {
        try {
            releaseSoundPlayers()

            val humanSoundId = context.resources.getIdentifier("human_move", "raw", context.packageName)
            val computerSoundId = context.resources.getIdentifier("computer_move", "raw", context.packageName)

            if (humanSoundId != 0) {
                humanMediaPlayer = MediaPlayer.create(context, humanSoundId)
            }

            if (computerSoundId != 0) {
                computerMediaPlayer = MediaPlayer.create(context, computerSoundId)
            }

            if (humanMediaPlayer == null || computerMediaPlayer == null) {
                useCustomSounds = false
                initializeToneGenerator()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            useCustomSounds = false
            initializeToneGenerator()
        }
    }

    private fun initializeToneGenerator() {
        try {
            toneGenerator?.release()
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 50)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseSoundPlayers() {
        humanMediaPlayer?.release()
        computerMediaPlayer?.release()
        toneGenerator?.release()

        humanMediaPlayer = null
        computerMediaPlayer = null
        toneGenerator = null
    }

    fun playHumanMoveSound() {
        if (!isSoundEnabled) return

        try {
            if (useCustomSounds && humanMediaPlayer != null) {
                humanMediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        player.seekTo(0)
                    } else {
                        player.start()
                    }
                }
            } else {
                // Fallback a tono del sistema
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playComputerMoveSound() {
        if (!isSoundEnabled) return

        try {
            if (useCustomSounds && computerMediaPlayer != null) {
                computerMediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        player.seekTo(0)
                    } else {
                        player.start()
                    }
                }
            } else {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 200)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    fun isSoundEnabled(): Boolean = isSoundEnabled

    fun isUsingCustomSounds(): Boolean = useCustomSounds
}