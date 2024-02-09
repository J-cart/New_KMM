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


actual interface MainAudioUtil{

    actual val recordingState : StateFlow<RecordingState>

    @Composable
    actual fun registerPermission(isRecording: (Boolean) -> Unit)

    @Composable
    actual fun RecordScreenView(
        isAudioRecording: Boolean,
        onStartRec: () -> Unit,
        onStopRec: (String?, Boolean) -> Unit,
        onNavigateUp: () -> Unit
    )

    actual fun startAudioProcess(scope: CoroutineScope, resume:Boolean, pause: Boolean,isRecording: (Boolean) -> Unit)
    actual fun stopAudioProcess(scope: CoroutineScope, stop:Boolean, isRecording: (Boolean) -> Unit)
}

class AudioUtil: MainAudioUtil {

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)

    override val recordingState: StateFlow<RecordingState> = _recordingState
    override fun startAudioProcess(scope: CoroutineScope, resume:Boolean, pause: Boolean,isRecording: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun stopAudioProcess(scope: CoroutineScope, stop:Boolean,isRecording: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

    @Composable
    override fun registerPermission(isRecording: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

    @Composable
    override fun RecordScreenView(
        isAudioRecording: Boolean,
        onStartRec: () -> Unit,
        onStopRec: (String?, Boolean) -> Unit,
        onNavigateUp: () -> Unit
    ) {
        TODO("Not yet implemented")
    }
}