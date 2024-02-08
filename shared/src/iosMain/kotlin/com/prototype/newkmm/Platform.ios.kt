package com.prototype.newkmm

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.CoroutineScope
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

    @Composable
    actual fun registerPermission(isRecording: (Boolean) -> Unit)

    actual fun startAudioProcess(scope: CoroutineScope, isRecording: (Boolean) -> Unit)
    actual fun stopAudioProcess(scope: CoroutineScope, isRecording: (Boolean) -> Unit)
}

class AudioUtil: MainAudioUtil {

    override fun startAudioProcess(scope: CoroutineScope, isRecording: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun stopAudioProcess(scope: CoroutineScope, isRecording: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

    @Composable
    override fun registerPermission(isRecording: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }
}