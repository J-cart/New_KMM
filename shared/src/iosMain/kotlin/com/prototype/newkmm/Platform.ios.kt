package com.prototype.newkmm

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import com.prototype.newkmm.presentation.screen.RecordingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()
actual fun getViewPlatform(): Platform = IOSPlatform()

actual class PlatformUtil{

    actual fun createAudioUtil(): MainAudioUtil = AudioUtil()
}


actual class AudioUtilImpl{
    actual  fun initUtil(): MainAudioUtil = AudioUtil()
}
actual interface MainAudioUtil{

    actual val recordingState : StateFlow<RecordingState>

    @Composable
    actual fun registerPermission(isRecording: (Boolean) -> Unit)

    actual fun startAudioProcess(scope: CoroutineScope, resume:Boolean, pause: Boolean)
    actual fun stopAudioProcess(scope: CoroutineScope, stop:Boolean)
}

class AudioUtil: MainAudioUtil {

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)

    override val recordingState: StateFlow<RecordingState> = _recordingState
    override fun startAudioProcess(scope: CoroutineScope, resume:Boolean, pause: Boolean) {
        TODO("Not yet implemented")
    }

    override fun stopAudioProcess(scope: CoroutineScope, stop:Boolean) {
        TODO("Not yet implemented")
    }

    @Composable
    override fun registerPermission(isRecording: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }
}