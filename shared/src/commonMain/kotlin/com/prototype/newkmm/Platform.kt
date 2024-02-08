package com.prototype.newkmm

import androidx.compose.runtime.Composable
import com.prototype.newkmm.presentation.screen.RecordingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getViewPlatform(): Platform

expect class PlatformUtil {
    fun createAudioUtil(): MainAudioUtil
}


expect class AudioUtilImpl{
    fun initUtil(): MainAudioUtil
}
expect interface MainAudioUtil{
    val recordingState : StateFlow<RecordingState>
    @Composable
    fun registerPermission(isRecording: (Boolean) -> Unit)

    fun startAudioProcess(scope: CoroutineScope, resume:Boolean, pause: Boolean)
    fun stopAudioProcess(scope: CoroutineScope, stop:Boolean)
}