package com.prototype.newkmm

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect class PlatformUtil {
    fun createAudioUtil(): MainAudioUtil
}


expect class AudioUtilImpl{
    fun initUtil(): MainAudioUtil
}
expect interface MainAudioUtil{

    @Composable
    fun registerPermission(isRecording: (Boolean) -> Unit)

    fun startAudioProcess(scope: CoroutineScope, isRecording: (Boolean) -> Unit)
    fun stopAudioProcess(scope: CoroutineScope, isRecording: (Boolean) -> Unit)
}